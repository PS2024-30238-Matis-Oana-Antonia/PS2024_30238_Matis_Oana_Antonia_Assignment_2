package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.OrderItemLogger;
import com.example.carturestibackend.constants.ProductLogger;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.dtos.mappers.OrderItemMapper;
import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.repositories.OrderItemRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.validators.OrderItemValidator;
import com.example.carturestibackend.validators.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to order items.
 */
@Service
public class OrderItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderItemService.class);
    private final OrderItemRepository orderItemRepository;
    private ProductService productService;
    private OrderItemValidator orderItemValidator;

    /**
     * Constructs a new OrderItemService with the specified OrderItemRepository.
     *
     * @param orderItemRepository The OrderItemRepository used to interact with order item data in the database.
     * @param productRepository
     */
    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, ProductRepository productRepository, OrderItemValidator orderItemValidator) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemValidator = orderItemValidator;

    }

    /**
     * Retrieves all order items from the database.
     *
     * @return A list of OrderItemDTO objects representing the order items.
     */
    public List<OrderItemDTO> findOrderItems() {
        LOGGER.error(OrderItemLogger.ALL_ORDER_ITEMS_RETRIEVED);
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        return orderItemList.stream()
                .map(OrderItemMapper::toOrderItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param id The ID of the order item to retrieve.
     * @return The OrderItemDTO object representing the retrieved order item.
     * @throws ResourceNotFoundException if the order item with the specified ID is not found.
     */
    public OrderItemDTO findOrderItemById(String id) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);
        if (!orderItemOptional.isPresent()) {
            LOGGER.error(OrderItemLogger.ORDER_ITEM_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(OrderItem.class.getSimpleName() + " with id: " + id);
        }
        return OrderItemMapper.toOrderItemDTO(orderItemOptional.get());
    }

    /**
     * Inserts a new order item into the database.
     *
     * @param orderItemDTO The OrderItemDTO object representing the order item to insert.
     * @return The ID of the newly inserted order item.
     */
    public String insert(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = OrderItemMapper.fromOrderItemDTO(orderItemDTO);
        OrderItemValidator.validateOrderItem(orderItem);
        orderItem = (OrderItem) orderItemRepository.save(orderItem);
        LOGGER.debug(OrderItemLogger.ORDER_ITEM_INSERTED, orderItem.getId_order_item());
        return orderItem.getId_order_item();
    }

    /**
     * Deletes an order item from the database by its ID.
     *
     * @param id The ID of the order item to delete.
     * @throws ResourceNotFoundException if the order item with the specified ID is not found.
     */
    public void deleteOrderItemById(String id) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);
        if (orderItemOptional.isPresent()) {
            orderItemRepository.delete(orderItemOptional.get());
            LOGGER.debug(OrderItemLogger.ORDER_ITEM_DELETED, id);
        } else {
            LOGGER.error(OrderItemLogger.ORDER_ITEM_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(OrderItem.class.getSimpleName() + " with id: " + id);
        }
    }

    /**
     * Updates an existing order item in the database.
     *
     * @param id          The ID of the order item to update.
     * @param orderItemDTO The updated OrderItemDTO object representing the new state of the order item.
     * @return The updated OrderItemDTO object.
     * @throws ResourceNotFoundException if the order item with the specified ID is not found.
     */
    public OrderItemDTO updateOrderItem(String id, OrderItemDTO orderItemDTO) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);
        if (!orderItemOptional.isPresent()) {
            LOGGER.error(OrderItemLogger.ORDER_ITEM_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(OrderItem.class.getSimpleName() + " with id: " + id);
        }

        OrderItem existingOrderItem = orderItemOptional.get();
        existingOrderItem.setQuantity(orderItemDTO.getQuantity());
        existingOrderItem.setPrice_per_unit(orderItemDTO.getPrice_per_unit());

        OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);
        LOGGER.debug(OrderItemLogger.ORDER_ITEM_UPDATED, updatedOrderItem.getId_order_item());

        return OrderItemMapper.toOrderItemDTO(updatedOrderItem);
    }

    public void addProductToOrderItem(OrderItem orderItem, Product product, long quantity) {
        LOGGER.info("Adding {} units of product {} to order item {}", quantity, product.getName(), orderItem.getId_order_item());
        orderItem.getProducts().add(product);
        orderItem.setQuantity(orderItem.getQuantity() + quantity);
        productService.decreaseProductStock(product.getId_product(), quantity);
        LOGGER.info(ProductLogger.STOCK_DECREASED, product.getName(), quantity, product.getStock());
        LOGGER.info("Product {} added successfully to order item {}", product.getName(), orderItem.getId_order_item());
    }

    public void removeProductFromOrderItem(OrderItem orderItem, Product product, long quantity) {
        LOGGER.info("Removing {} units of product {} from order item {}", quantity, product.getName(), orderItem.getId_order_item());
        orderItem.getProducts().remove(product);
        orderItem.setQuantity(orderItem.getQuantity() - quantity);
        productService.increaseProductStock(product.getId_product(), quantity);
        LOGGER.info(ProductLogger.STOCK_INCREASED, product.getName(), quantity, product.getStock());
        LOGGER.info("Product {} removed successfully from order item {}", product.getName(), orderItem.getId_order_item());
    }
}

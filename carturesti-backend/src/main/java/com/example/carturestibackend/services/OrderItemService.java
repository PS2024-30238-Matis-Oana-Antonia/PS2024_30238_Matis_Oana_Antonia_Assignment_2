package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.OrderItemLogger;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.dtos.mappers.OrderItemMapper;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.repositories.CartRepository;
import com.example.carturestibackend.repositories.OrderItemRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.validators.OrderItemValidator;
import jakarta.transaction.Transactional;
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
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private OrderItemValidator orderItemValidator;

    /**
     * Constructs a new OrderItemService with the specified OrderItemRepository and ProductRepository.
     *
     * @param orderItemRepository The OrderItemRepository used to interact with order item data in the database.
     * @param productRepository   The ProductRepository used to interact with product data in the database.
     * @param cartRepository
     */
    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, ProductRepository productRepository, CartRepository cartRepository, OrderItemValidator orderItemValidator) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
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
    @Transactional
    public String insert(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = OrderItemMapper.fromOrderItemDTO(orderItemDTO);
        OrderItemValidator.validateOrderItem(orderItem);
        orderItem = orderItemRepository.save(orderItem);
        LOGGER.debug(OrderItemLogger.ORDER_ITEM_INSERTED, orderItem.getId_order_item());
        return orderItem.getId_order_item();
    }


    /**
     * Deletes an order item from the database by its ID.
     *
     * @param orderId The ID of the order item to delete.
     * @throws ResourceNotFoundException if the order item with the specified ID is not found.
     */
    @Transactional
    public void deleteOrderItemById(String orderId) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderId);
        if (orderItemOptional.isPresent()) {
            OrderItem orderItem = orderItemOptional.get();

            orderItemRepository.delete(orderItem);
            LOGGER.debug(OrderItemLogger.ORDER_ITEM_DELETED, orderId);
        } else {
            LOGGER.error(OrderItemLogger.ORDER_ITEM_NOT_FOUND_BY_ID, orderId);
            throw new ResourceNotFoundException(OrderItem.class.getSimpleName() + " with id: " + orderId);
        }
    }

    /**
     * Updates an existing order item in the database.
     *
     * @param id           The ID of the order item to update.
     * @param orderItemDTO The updated OrderItemDTO object representing the new state of the order item.
     * @return The updated OrderItemDTO object.
     * @throws ResourceNotFoundException if the order item with the specified ID is not found.
     */
    @Transactional
    public OrderItemDTO updateOrderItem(String id, OrderItemDTO orderItemDTO) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);
        if (!orderItemOptional.isPresent()) {
            LOGGER.error(OrderItemLogger.ORDER_ITEM_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(OrderItem.class.getSimpleName() + " with id: " + id);
        }

        OrderItem existingOrderItem = orderItemOptional.get();
        int oldQuantity = (int) existingOrderItem.getQuantity();
        int newQuantity = (int) orderItemDTO.getQuantity();
        existingOrderItem.setQuantity(newQuantity);

        // Recalculate the price per unit and total price based on the new quantity
        double pricePerUnit = existingOrderItem.getProduct().getPrice();
        double totalPrice = pricePerUnit * newQuantity;
        existingOrderItem.setPrice_per_unit(pricePerUnit);
        existingOrderItem.setPrice_per_unit(totalPrice);

        OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);
        LOGGER.debug(OrderItemLogger.ORDER_ITEM_UPDATED, updatedOrderItem.getId_order_item());

        // Update the total price of the cart
        Cart cart = updatedOrderItem.getCart();
        double totalCartPrice = calculateTotalCartPrice(cart);
        cart.setTotal_price(totalCartPrice);
        cartRepository.save(cart);

        return OrderItemMapper.toOrderItemDTO(updatedOrderItem);
    }
    private double calculateTotalCartPrice(Cart cart) {
        return cart.getOrderItems().stream()
                .mapToDouble(orderItem -> orderItem.getPrice_per_unit())
                .sum();
    }
}

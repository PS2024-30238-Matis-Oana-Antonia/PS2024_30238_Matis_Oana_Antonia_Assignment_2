package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.OrderItemLogger;
import com.example.carturestibackend.constants.OrderLogger;
import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.dtos.mappers.OrderItemMapper;
import com.example.carturestibackend.dtos.mappers.OrderMapper;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.repositories.OrderItemRepository;
import com.example.carturestibackend.repositories.OrderRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.repositories.UserRepository;
import com.example.carturestibackend.validators.OrderValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to orders.
 */
@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderValidator orderValidator;

    /**
     * Constructs a new OrderService with the specified OrderRepository.
     *
     * @param orderRepository     The OrderRepository used to interact with order data in the database.
     * @param orderItemRepository
     * @param userRepository
     * @param productRepository
     * @param orderValidator

     */
    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserRepository userRepository, ProductRepository productRepository, OrderValidator orderValidator) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderValidator = orderValidator;

    }

    /**
     * Retrieves all orders from the database.
     *
     * @return A list of OrderDTO objects representing the orders.
     */
    public List<OrderDTO> findOrders() {
        LOGGER.error(OrderLogger.ALL_ORDERS_RETRIEVED);
        List<Order> orderList = orderRepository.findAll();
        return orderList.stream()
                .map(this::calculateOrderTotals) // Calculate totals for each order
                .collect(Collectors.toList());
    }

    private OrderDTO calculateOrderTotals(Order order) {
        List<Product> products = order.getProducts(); // Assuming you have a method to retrieve products from the order

        long totalQuantity = 0;
        double totalPrice = 0;

        // Iterate through the products associated with the order
        for (Product product : products) {
            // Calculate total quantity and total price based on the products
            totalQuantity += 1;// Assuming there's a method to get quantity from Product entity
            totalPrice += product.getPrice(); // Assuming there's a method to get price from Product entity
        }

        // Update order totals
        order.setTotal_quantity(totalQuantity);
        order.setTotal_price(totalPrice);

        // Return OrderDTO
        return OrderMapper.toOrderDTO(order);
    }




    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order to retrieve.
     * @return The OrderDTO object representing the retrieved order.
     * @throws ResourceNotFoundException if the order with the specified ID is not found.
     */
    public OrderDTO findOrderById(String id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent()) {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id);
        }
        return OrderMapper.toOrderDTO(orderOptional.get());
    }


    /**
     * Inserts a new order into the database.
     *
     * @param orderDTO The OrderDTO object representing the order to insert.
     * @return The ID of the newly inserted order.
     */


    @Transactional
    public String insert(OrderDTO orderDTO) {
        Logger logger = LoggerFactory.getLogger(getClass());

        User user = userRepository.findById(orderDTO.getId_user())
                .orElseThrow(() -> {
                    String message = String.format(OrderLogger.ORDER_NOT_FOUND_BY_ID, orderDTO.getId_user());
                    logger.error(message);
                    return new ResourceNotFoundException(message);
                });

        logger.info(OrderLogger.ORDER_RETRIEVED_BY_ID, user.getId_user());

        Order order = new Order();
        order.setOrder_date(orderDTO.getOrder_date());
        order.setUser(user);

        // Initialize the list of products if null
        if (order.getProducts() == null) {
            order.setProducts(new ArrayList<>());
        }

        double totalPrice = 0.0;

        for (String productId : orderDTO.getId_products()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        String message = String.format(OrderLogger.ORDER_NOT_FOUND_BY_ID, productId);
                        logger.error(message);
                        return new ResourceNotFoundException(message);
                    });

            if (product.getStock() <= 0) {
                String message = "Insufficient stock for product with ID: " + productId;
                logger.error(message);
                throw new RuntimeException(message);
            }

            // Choose the minimum price among original, discounted, and promotional price
            double priceToUse = Double.MAX_VALUE; // Initialize with maximum value
            if (product.getPrice() < priceToUse) {
                priceToUse = product.getPrice();
            }
            if (product.getPrice_discount() > 0 && product.getPrice_discount() < priceToUse) {
                priceToUse = product.getPrice_discount();
            }
            if (product.getPrice_promotion() > 0 && product.getPrice_promotion() < priceToUse) {
                priceToUse = product.getPrice_promotion();
            }

            order.getProducts().add(product);
            totalPrice += priceToUse;

            // Decrease the stock of the product
            product.setStock(product.getStock() - 1);
            productRepository.save(product);

            logger.info("Product with ID {} added to order. Price used: {}", product.getId_product(), priceToUse);
        }

        order.setTotal_price(totalPrice);
        logger.info("Total price calculated for order: {}", totalPrice);

        try {
            order = orderRepository.save(order);
            logger.info(OrderLogger.ORDER_INSERTED, order.getId_order());
            return order.getId_order();
        } catch (Exception e) {
            logger.error("Failed to save order: {}", e.getMessage());
            throw new RuntimeException("Failed to save order: " + e.getMessage());
        }
    }


    /**
     * Deletes an order from the database by its ID.
     *
     * @param id_order The ID of the order to delete.
     * @throws ResourceNotFoundException if the order with the specified ID is not found.
     */
    @Transactional
    public void deleteOrderById(String id_order) {
        Optional<Order> orderOptional = orderRepository.findById(id_order);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            // Remove association with user without cascading deletion
            order.setUser(null);

            for (Product product : order.getProducts()) {
                // Increase stock by 1 for each product in the order
                product.setStock(product.getStock() + 1);
                product.setOrders(null); // Remove association with the order
                productRepository.save(product); // Save the updated product
            }

            orderRepository.delete(order);

            LOGGER.debug(OrderLogger.ORDER_DELETED, id_order);
        } else {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id_order);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id_order);
        }
    }



    /**
     * Updates an existing order in the database.
     *
     * @param id The ID of the order to update.
     * @param orderDTO The updated OrderDTO object representing the new state of the order.
     * @return The updated OrderDTO object.
     * @throws ResourceNotFoundException if the order with the specified ID is not found.
     */
    public OrderDTO updateOrder(String id, OrderDTO orderDTO) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent()) {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id);
        }

        Order existingOrder = orderOptional.get();
        existingOrder.setOrder_date(orderDTO.getOrder_date());
        existingOrder.setTotal_quantity(orderDTO.getTotal_quantity());
        existingOrder.setTotal_price(orderDTO.getTotal_price());

        Order updatedOrder = orderRepository.save(existingOrder);
        LOGGER.debug(OrderLogger.ORDER_UPDATED, updatedOrder.getId_order());

        return OrderMapper.toOrderDTO(updatedOrder);
    }

    public List<OrderDTO> findOrdersByUserId(String userId) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Retrieve orders associated with the user
        List<Order> orders = orderRepository.findByUser(user);

        // Map orders to OrderDTOs and calculate totals for each order
        return orders.stream()
                .map(this::calculateOrderTotals)
                .collect(Collectors.toList());
    }


    private void updateOrderTotal(Order order, OrderItem orderItem) {
        order.setTotal_quantity(order.getTotal_quantity() + orderItem.getQuantity());
        order.setTotal_price(order.getTotal_price() + (orderItem.getPrice_per_unit() * orderItem.getQuantity()));
    }

}

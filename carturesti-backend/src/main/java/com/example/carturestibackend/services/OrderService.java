package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.OrderLogger;
import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.dtos.mappers.OrderMapper;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.repositories.OrderRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Constructs a new OrderService with the specified OrderRepository.
     *
     * @param orderRepository   The OrderRepository used to interact with order data in the database.
     * @param userRepository
     * @param productRepository
     */
    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
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
                .map(OrderMapper::toOrderDTO)
                .collect(Collectors.toList());
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
    public String insert(OrderDTO orderDTO) {
        Order order = OrderMapper.fromOrderDTO(orderDTO);
        order = orderRepository.save(order);
        LOGGER.debug(OrderLogger.ORDER_INSERTED, order.getId_order());
        return order.getId_order();
    }


    /**
     * Deletes an order from the database by its ID.
     *
     * @param id The ID of the order to delete.
     * @throws ResourceNotFoundException if the order with the specified ID is not found.
     */
    public void deleteOrderById(String id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            orderRepository.delete(orderOptional.get());
            LOGGER.debug(OrderLogger.ORDER_DELETED, id);
        } else {
            LOGGER.error(OrderLogger.ORDER_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Order.class.getSimpleName() + " with id: " + id);
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
        existingOrder.setTotal_price(orderDTO.getTotal_price());

        Order updatedOrder = orderRepository.save(existingOrder);
        LOGGER.debug(OrderLogger.ORDER_UPDATED, updatedOrder.getId_order());

        return OrderMapper.toOrderDTO(updatedOrder);
    }
}

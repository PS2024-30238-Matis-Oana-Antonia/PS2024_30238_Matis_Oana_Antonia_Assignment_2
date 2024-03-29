package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.OrderLogger;
import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.services.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to handle HTTP requests related to orders.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/order")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    /**
     * Constructs a new OrderController with the specified OrderService.
     *
     * @param orderService The OrderService used to handle order-related business logic.
     */
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retrieves all orders.
     *
     * @return A ResponseEntity containing a list of OrderDTO objects representing the orders.
     */
    @GetMapping()
    public ResponseEntity<List<OrderDTO>> getOrders() {
        LOGGER.info(OrderLogger.ALL_ORDERS_RETRIEVED);
        List<OrderDTO> dtos = orderService.findOrders();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Inserts a new order.
     *
     * @param orderDTO The OrderDTO object representing the order to insert.
     * @return A ResponseEntity containing the ID of the newly inserted order.
     */
    @PostMapping()
    public ResponseEntity<String> insertOrder(@Valid @RequestBody OrderDTO orderDTO) {
        String orderID = orderService.insert(orderDTO);
        LOGGER.debug(OrderLogger.ORDER_INSERTED, orderID);
        return new ResponseEntity<>(orderID, HttpStatus.CREATED);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderID The ID of the order to retrieve.
     * @return A ResponseEntity containing the OrderDTO object representing the retrieved order.
     */
    @GetMapping(value = "/{id_order}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("id_order") String orderID) {
        LOGGER.info(OrderLogger.ORDER_RETRIEVED_BY_ID, orderID);
        OrderDTO dto = orderService.findOrderById(orderID);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param orderID The ID of the order to delete.
     * @return A ResponseEntity indicating the success of the operation.
     */
    @DeleteMapping(value = "/{id_order}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id_order") String orderID) {
        LOGGER.debug(OrderLogger.ORDER_DELETED, orderID);
        orderService.deleteOrderById(orderID);
        return new ResponseEntity<>("Order with ID " + orderID + " deleted successfully", HttpStatus.OK);
    }

    /**
     * Updates an order by its ID.
     *
     * @param orderID    The ID of the order to update.
     * @param orderDTO   The updated OrderDTO object representing the new state of the order.
     * @return A ResponseEntity containing the updated OrderDTO object.
     */
    @PutMapping(value = "/{id_order}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id_order") String orderID, @Valid @RequestBody OrderDTO orderDTO) {
        LOGGER.debug(OrderLogger.ORDER_UPDATED, orderID);
        OrderDTO updatedOrder = orderService.updateOrder(orderID, orderDTO);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }
}

package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.OrderLogger;
import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller class to handle HTTP requests related to orders.
 */
@Controller
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
     * @return A ModelAndView containing a list of OrderDTO objects representing the orders.
     */
    @GetMapping()
    public ModelAndView getOrders() {
        LOGGER.info(OrderLogger.ALL_ORDERS_RETRIEVED);
        List<OrderDTO> dtos = orderService.findOrders();
        ModelAndView modelAndView = new ModelAndView("/orders");
        modelAndView.addObject("orders", dtos);
        return modelAndView;
    }

    /**
     * Inserts a new order.
     *
     * @param orderDTO The OrderDTO object representing the order to insert.
     * @return A ModelAndView containing the ID of the newly inserted order.
     */
    @PostMapping()
    public ModelAndView insertOrder(@Valid @RequestBody OrderDTO orderDTO) {
        String orderID = orderService.insert(orderDTO);
        LOGGER.debug(OrderLogger.ORDER_INSERTED, orderID);
        ModelAndView modelAndView = new ModelAndView("/orders");
        modelAndView.addObject("orderID", orderID);
        return modelAndView;
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderID The ID of the order to retrieve.
     * @return A ModelAndView containing the OrderDTO object representing the retrieved order.
     */
    @GetMapping(value = "/{id_order}")
    public ModelAndView getOrder(@PathVariable("id_order") String orderID) {
        LOGGER.info(OrderLogger.ORDER_RETRIEVED_BY_ID, orderID);
        OrderDTO dto = orderService.findOrderById(orderID);
        ModelAndView modelAndView = new ModelAndView("/orders");
        modelAndView.addObject("order", dto);
        return modelAndView;
    }

    /**
     * Deletes an order by its ID.
     *
     * @param orderID The ID of the order to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @DeleteMapping(value = "/{id_order}")
    public ModelAndView deleteOrder(@PathVariable("id_order") String orderID) {
        LOGGER.debug(OrderLogger.ORDER_DELETED, orderID);
        orderService.deleteOrderById(orderID);
        ModelAndView modelAndView = new ModelAndView("/orders");
        modelAndView.addObject("message", "Order with ID " + orderID + " deleted successfully");
        return modelAndView;
    }

    /**
     * Updates an order by its ID.
     *
     * @param orderID    The ID of the order to update.
     * @param orderDTO   The updated OrderDTO object representing the new state of the order.
     * @return A ModelAndView containing the updated OrderDTO object.
     */
    @PutMapping(value = "/{id_order}")
    public ModelAndView updateOrder(@PathVariable("id_order") String orderID, @Valid @RequestBody OrderDTO orderDTO) {
        LOGGER.debug(OrderLogger.ORDER_UPDATED, orderID);
        OrderDTO updatedOrder = orderService.updateOrder(orderID, orderDTO);
        ModelAndView modelAndView = new ModelAndView("/orders");
        modelAndView.addObject("order", updatedOrder);
        return modelAndView;
    }
}

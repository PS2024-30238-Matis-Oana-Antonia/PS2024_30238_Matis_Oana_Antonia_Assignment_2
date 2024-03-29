package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.OrderItemLogger;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.services.OrderItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to handle HTTP requests related to order items.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/order-items")
public class OrderItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderItemController.class);

    private final OrderItemService orderItemService;

    /**
     * Constructs a new OrderItemController with the specified OrderItemService.
     *
     * @param orderItemService The OrderItemService used to handle order item-related business logic.
     */
    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    /**
     * Retrieves all order items.
     *
     * @return A ResponseEntity containing a list of OrderItemDTO objects representing the order items.
     */
    @GetMapping()
    public ResponseEntity<List<OrderItemDTO>> getOrderItems() {
        LOGGER.info(OrderItemLogger.ALL_ORDER_ITEMS_RETRIEVED);
        List<OrderItemDTO> dtos = orderItemService.findOrderItems();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Inserts a new order item.
     *
     * @param orderItemDTO The OrderItemDTO object representing the order item to insert.
     * @return A ResponseEntity containing the ID of the newly inserted order item.
     */
    @PostMapping()
    public ResponseEntity<String> insertOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO) {
        String orderItemID = orderItemService.insert(orderItemDTO);
        LOGGER.debug(OrderItemLogger.ORDER_ITEM_INSERTED, orderItemID);
        return new ResponseEntity<>(orderItemID, HttpStatus.CREATED);
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param orderItemID The ID of the order item to retrieve.
     * @return A ResponseEntity containing the OrderItemDTO object representing the retrieved order item.
     */
    @GetMapping(value = "/{id_order_item}")
    public ResponseEntity<OrderItemDTO> getOrderItem(@PathVariable("id_order_item") String orderItemID) {
        LOGGER.info(OrderItemLogger.ORDER_ITEM_RETRIEVED_BY_ID, orderItemID);
        OrderItemDTO dto = orderItemService.findOrderItemById(orderItemID);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Deletes an order item by its ID.
     *
     * @param orderItemID The ID of the order item to delete.
     * @return A ResponseEntity indicating the success of the operation.
     */
    @DeleteMapping(value = "/{id_order_item}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable("id_order_item") String orderItemID) {
        LOGGER.debug(OrderItemLogger.ORDER_ITEM_DELETED, orderItemID);
        orderItemService.deleteOrderItemById(orderItemID);
        return new ResponseEntity<>("Order item with ID " + orderItemID + " deleted successfully", HttpStatus.OK);
    }

    /**
     * Updates an order item by its ID.
     *
     * @param orderItemID  The ID of the order item to update.
     * @param orderItemDTO The updated OrderItemDTO object representing the new state of the order item.
     * @return A ResponseEntity containing the updated OrderItemDTO object.
     */
    @PutMapping(value = "/{id_order_item}")
    public ResponseEntity<OrderItemDTO> updateOrderItem(@PathVariable("id_order_item") String orderItemID, @Valid @RequestBody OrderItemDTO orderItemDTO) {
        LOGGER.debug(OrderItemLogger.ORDER_ITEM_UPDATED, orderItemID);
        OrderItemDTO updatedOrderItem = orderItemService.updateOrderItem(orderItemID, orderItemDTO);
        return new ResponseEntity<>(updatedOrderItem, HttpStatus.OK);
    }
}

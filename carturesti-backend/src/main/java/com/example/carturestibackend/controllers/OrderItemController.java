package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.OrderItemLogger;
import com.example.carturestibackend.constants.UserLogger;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.OrderItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller class to handle HTTP requests related to order items.
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/orderitem")
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
     * @return A ModelAndView containing a list of OrderItemDTO objects representing the order items.
     */
    @GetMapping()
    public ModelAndView getOrderItems() {
        LOGGER.info(OrderItemLogger.ALL_ORDER_ITEMS_RETRIEVED);
        List<OrderItemDTO> dtos = orderItemService.findOrderItems();
        ModelAndView modelAndView = new ModelAndView("/orderitem");
        modelAndView.addObject("orderItems", dtos);
        return modelAndView;
    }

    /**
     * Inserts a new order item.
     *
     * @param orderItemDTO The OrderItemDTO object representing the order item to insert.
     * @return A ModelAndView containing the ID of the newly inserted order item.
     */
    @PostMapping("/insert")
    public ModelAndView insertOrderItem(@Valid @ModelAttribute OrderItemDTO orderItemDTO) {
        String orderItemID = orderItemService.insert(orderItemDTO);
        LOGGER.debug(OrderItemLogger.ORDER_ITEM_INSERTED, orderItemID);
        ModelAndView modelAndView = new ModelAndView("/orderitem");
        modelAndView.addObject("orderItemID", orderItemID);
        return modelAndView;
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param orderItemID The ID of the order item to retrieve.
     * @return A ModelAndView containing the OrderItemDTO object representing the retrieved order item.
     */
    @GetMapping(value = "/{id_order_item}")
    public ModelAndView getOrderItem(@PathVariable("id_order_item") String orderItemID) {
        LOGGER.info(OrderItemLogger.ORDER_ITEM_RETRIEVED_BY_ID, orderItemID);
        OrderItemDTO dto = orderItemService.findOrderItemById(orderItemID);
        ModelAndView modelAndView = new ModelAndView("/orderitem");
        modelAndView.addObject("orderItem", dto);
        return modelAndView;
    }

    /**
     * Deletes an order item by its ID.
     *
     * @param orderItemID The ID of the order item to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @PostMapping(value = "/delete")
    public ModelAndView deleteOrderItem(@RequestParam("id_order_item") String orderItemID, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/orderitem");
        try {
            orderItemService.deleteOrderItemById(orderItemID);
            LOGGER.debug(OrderItemLogger.ORDER_ITEM_DELETED, orderItemID);
            redirectAttributes.addFlashAttribute("successMessage", "Order item with ID " + orderItemID + " deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete order item with ID " + orderItemID + ". Please try again.");
        }
        return mav;
    }

    /**
     * Updates an order item by its ID.
     *
     * @param orderItemID  The ID of the order item to update.
     * @param orderItemDTO The updated OrderItemDTO object representing the new state of the order item.
     * @return A ModelAndView containing the updated OrderItemDTO object.
     */
    @PostMapping("/update")
    public ModelAndView updateOrderItem(@RequestParam("id_order_item") String orderItemID, @Valid @ModelAttribute OrderItemDTO orderItemDTO, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/orderitem");
        try {
            OrderItemDTO updatedOrderItem = orderItemService.updateOrderItem(orderItemID, orderItemDTO);
            mav.addObject("successMessage", "Order item updated successfully!");
        } catch (Exception e) {
            mav.addObject("errorMessage", "Failed to update order item. Please try again.");
        }
        return mav;
    }

}

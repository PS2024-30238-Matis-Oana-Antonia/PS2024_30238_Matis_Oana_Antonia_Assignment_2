package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.OrderItemLogger;
import com.example.carturestibackend.constants.OrderLogger;
import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.services.OrderService;
import com.example.carturestibackend.strategy.CsvFileGenerationStrategy;
import com.example.carturestibackend.strategy.FileGenerator;
import com.example.carturestibackend.strategy.PdfFileGenerationStrategy;
import com.example.carturestibackend.strategy.TxtFileGenerationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
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
        ModelAndView modelAndView = new ModelAndView("/order");
        modelAndView.addObject("orders", dtos);
        return modelAndView;
    }

    /**
     * Inserts a new order.
     *
     * @param orderDTO The OrderDTO object representing the order to insert.
     * @return A ModelAndView containing the ID of the newly inserted order.
     */
    @PostMapping("/insert")
    public ModelAndView insertOrder(@Valid @ModelAttribute OrderDTO orderDTO) {
        String orderID = orderService.insert(orderDTO);
        LOGGER.debug(OrderLogger.ORDER_INSERTED, orderID);
        ModelAndView modelAndView = new ModelAndView("/order");
        modelAndView.addObject("orderID", orderID);
        return new ModelAndView("redirect:/order");
    }

    @PostMapping("/place")
    public ModelAndView placeOrder(@RequestParam("userId") String userId, @Validated OrderDTO orderDTO) {
        String orderID = orderService.placeOrder(userId, (orderDTO)); // Asumând că metoda placeOrder acceptă și user ID-ul
        LOGGER.debug(OrderLogger.ORDER_PLACED, orderID);
        ModelAndView modelAndView = new ModelAndView("/order");
        modelAndView.addObject("orderID", orderID);
        return modelAndView;
    }



    @PostMapping("/generateAndSendPdf")
    public ResponseEntity<String> generateAndSendPdf(@RequestParam String orderId) {
        orderService.generateAndSendPdf(orderId);
        return ResponseEntity.ok("PDF generated and sent successfully");
    }

    @PostMapping("/generateAndSendTxt")
    public ResponseEntity<String> generateAndSendTxt(@RequestParam String orderId) {
        orderService.generateAndSendTxt(orderId);
        return ResponseEntity.ok("TXT generated and sent successfully");
    }

    @PostMapping("/generateAndSendCsv")
    public ResponseEntity<String> generateAndSendCsv(@RequestParam String orderId) {
        orderService.generateAndSendCsv(orderId);
        return ResponseEntity.ok("CSV generated and sent successfully");
    }

    @PostMapping("/insert2")
    public ModelAndView insertOrderClient(@Valid @ModelAttribute OrderDTO orderDTO) {
        String orderID = orderService.insert(orderDTO);
        LOGGER.debug(OrderLogger.ORDER_INSERTED, orderID);
        ModelAndView modelAndView = new ModelAndView("/order");
        modelAndView.addObject("orderID", orderID);
        return new ModelAndView("redirect:/orderclient");
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
        ModelAndView modelAndView = new ModelAndView("/order");
        modelAndView.addObject("order", dto);
        return modelAndView;
    }

    /**
     * Deletes an order by its ID.
     *
     * @param orderID The ID of the order to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @PostMapping(value = "/delete")
    public ModelAndView deleteOrder(@RequestParam("id_order") String orderID, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/order");
        try {
            orderService.deleteOrderById(orderID);
            LOGGER.debug(OrderLogger.ORDER_DELETED, orderID);
            redirectAttributes.addFlashAttribute("successMessage", "Order with ID " + orderID + " deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete order with ID " + orderID + ". Please try again.");
        }
        return mav;
    }
    /**
     * Updates an order by its ID.
     *
     * @param orderID    The ID of the order to update.
     * @param orderDTO   The updated OrderDTO object representing the new state of the order.
     * @return A ModelAndView containing the updated OrderDTO object.
     */
    @PostMapping("/update")
    public ModelAndView updateOrder(@RequestParam("id_order") String orderID, @Valid @ModelAttribute OrderDTO orderDTO, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/order");
        try {
            OrderDTO updatedOrder = orderService.updateOrder(orderID, orderDTO);
            mav.addObject("successMessage", "Order updated successfully!");
        } catch (Exception e) {
            mav.addObject("errorMessage", "Failed to update order. Please try again.");
        }
        return mav;
    }

    @GetMapping(value = "/byUserId")
    public ModelAndView getOrdersByUserId(@RequestParam("userId") String userId) {
        List<OrderDTO> dtos = orderService.findOrdersByUserId(userId);
        ModelAndView modelAndView = new ModelAndView("/order"); // Assuming there's a view named "order"
        modelAndView.addObject("orders", dtos);
        return modelAndView;
    }

}

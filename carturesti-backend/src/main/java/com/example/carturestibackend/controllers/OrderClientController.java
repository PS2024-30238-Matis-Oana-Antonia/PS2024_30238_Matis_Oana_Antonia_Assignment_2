package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.services.OrderService;
import com.example.carturestibackend.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controller class for handling requests related to the client's order page.
 */
@Controller
public class OrderClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderClientController.class);
    private final ProductService productService;
    private final OrderService orderService;

    /**
     * Constructor for OrderClientController.
     * @param productService ProductService instance
     * @param orderService OrderService instance
     */
    @Autowired
    public OrderClientController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    /**
     * Handler method for GET requests to "/orderclient".
     * Displays the client's order page.
     * @return ModelAndView containing the view name for the order client page and a list of products
     */
    @GetMapping("/orderclient")
    public ModelAndView orderclientPage() {
        ModelAndView modelAndView = new ModelAndView();

        List<ProductDTO> products = productService.findProducts();

        modelAndView.addObject("products", products);

        modelAndView.setViewName("orderclient");

        return modelAndView;
    }
}

package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.OrderLogger;
import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.services.OrderService;
import com.example.carturestibackend.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class OrderClientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderClientController.class);
    private final ProductService productService;
    private final OrderService orderService;
    @Autowired
    public OrderClientController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/orderclient")
    public ModelAndView orderclientPage() {
        ModelAndView modelAndView = new ModelAndView();

        // Fetch products from the ProductService
        List<ProductDTO> products = productService.findProducts();

        // Add products to the ModelAndView
        modelAndView.addObject("products", products);


        // Set the view name
        modelAndView.setViewName("orderclient");

        return modelAndView;
    }


}

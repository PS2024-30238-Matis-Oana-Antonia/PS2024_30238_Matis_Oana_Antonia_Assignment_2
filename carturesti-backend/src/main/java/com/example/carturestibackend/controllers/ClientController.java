package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ClientController {

    private final ProductService productService;

    @Autowired
    public ClientController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/client")
    public ModelAndView clientPage() {
        ModelAndView modelAndView = new ModelAndView();

        // Fetch products from the ProductService
        List<ProductDTO> products = productService.findProducts();

        // Add products to the ModelAndView
        modelAndView.addObject("products", products);


        // Set the view name
        modelAndView.setViewName("client");

        return modelAndView;
    }
}

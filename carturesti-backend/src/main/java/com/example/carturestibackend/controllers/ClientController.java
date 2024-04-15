package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controller class to handle HTTP requests related to the client page.
 */
@Controller
public class ClientController {

    private final ProductService productService;

    /**
     * Constructor for ClientController.
     * @param productService The ProductService instance to be injected.
     */
    @Autowired
    public ClientController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Handler method for GET requests to "/client".
     * Retrieves products and displays the client page.
     * @return ModelAndView containing the view name and list of products.
     */
    @GetMapping("/client")
    public ModelAndView clientPage() {
        ModelAndView modelAndView = new ModelAndView();
        List<ProductDTO> products = productService.findProducts();
        modelAndView.addObject("products", products);
        modelAndView.setViewName("client");
        return modelAndView;
    }
}

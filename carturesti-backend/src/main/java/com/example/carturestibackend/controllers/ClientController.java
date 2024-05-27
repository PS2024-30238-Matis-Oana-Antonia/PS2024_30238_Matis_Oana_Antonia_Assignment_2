package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.services.AuthService;
import com.example.carturestibackend.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controller class for handling client-related requests and rendering client pages.
 */
@Controller
public class ClientController {

    private final AuthService authService;
    private final ProductService productService;

    @Autowired
    public ClientController(AuthService authService, ProductService productService) {
        this.authService = authService;
        this.productService = productService;
    }
    /**
     * Renders the client page with user-specific information and products.
     *
     * @param userId  The ID of the logged-in user.
     * @param request The HttpServletRequest object representing the client request.
     * @return ModelAndView object representing the view and model for the client page.
     */

    @GetMapping("/client/{userId}")
    public ModelAndView clientPage(@PathVariable String userId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            return new ModelAndView("redirect:/login");
        }

        String username = (String) session.getAttribute("username");
        String role = authService.getRole(username);

        if (!"client".equals(role)) {
            return new ModelAndView("redirect:/admin");
        }

        String cartId = (String) session.getAttribute("cartId");
        modelAndView.addObject("cartId", cartId);

        List<ProductDTO> products = productService.findProducts();
        modelAndView.addObject("products", products);

        // Add user ID to the model
        modelAndView.addObject("userId", userId);

        modelAndView.setViewName("client");
        return modelAndView;
    }


}

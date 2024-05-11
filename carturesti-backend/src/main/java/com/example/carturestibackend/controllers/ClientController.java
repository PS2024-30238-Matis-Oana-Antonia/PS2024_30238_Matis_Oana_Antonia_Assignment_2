package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class to handle HTTP requests related to the client page.
 */
@Controller
public class ClientController {

    private final AuthService authService;

    @Autowired
    public ClientController( AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/client")
    public ModelAndView adminPage(HttpServletRequest request) {
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

        modelAndView.setViewName("client");
        return modelAndView;
    }
}


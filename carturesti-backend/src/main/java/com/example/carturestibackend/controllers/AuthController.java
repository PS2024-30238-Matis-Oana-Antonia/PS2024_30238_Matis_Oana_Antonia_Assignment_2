package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    // Suppose you have a service class where you implement the findUserByRole method
    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("name") String name,
                        @RequestParam("password") String password) {

        String role = authenticateUser(name, password);

        if (role != null) {
            if (role.equals("admin")) {
                return "redirect:/admin";
            } else if (role.equals("client")) {
                return "redirect:/client";
            }
        }

        return "redirect:/login?error=true";
    }


    private String authenticateUser(String name, String password) {

        UserDTO user = userService.findUserByNameAndPassword(name, password);

        // Check if user exists and has a valid role
        if (user != null && user.getRole() != null) {
            return user.getRole();
        }

        return null;
    }

}

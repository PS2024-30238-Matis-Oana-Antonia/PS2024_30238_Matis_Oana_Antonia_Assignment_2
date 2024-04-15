package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for handling authentication-related requests and operations.
 */
@Controller
public class AuthController {

    private UserService userService;

    /**
     * Constructor for AuthController.
     * @param userService The UserService instance to be injected.
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handler method for GET requests to "/login".
     * Displays the login form.
     * @param error Error message indicating invalid credentials (optional).
     * @param model Model to be populated with data.
     * @return The name of the login view template.
     */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        return "login";
    }

    /**
     * Handler method for POST requests to "/login".
     * Authenticates the user and redirects based on role.
     * @param name Username entered in the login form.
     * @param password Password entered in the login form.
     * @return Redirect URL based on user role or back to login page with error.
     */
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

    /**
     * Authenticate the user based on username and password.
     * @param name Username entered in the login form.
     * @param password Password entered in the login form.
     * @return Role of the authenticated user (admin or client), or null if authentication fails.
     */
    private String authenticateUser(String name, String password) {

        UserDTO user = userService.findUserByNameAndPassword(name, password);

        // Check if user exists and has a valid role
        if (user != null && user.getRole() != null) {
            return user.getRole();
        }

        return null;
    }

}

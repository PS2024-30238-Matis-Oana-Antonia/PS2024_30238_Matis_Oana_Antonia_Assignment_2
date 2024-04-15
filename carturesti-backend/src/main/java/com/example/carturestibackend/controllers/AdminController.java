package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controller class for handling admin-related requests and operations.
 */
@Controller
public class AdminController {

    private final UserService userService;

    /**
     * Constructor for AdminController.
     * @param userService The UserService instance to be injected.
     */
    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handler method for GET requests to "/admin".
     * Retrieves a list of users and returns the admin page.
     * @return ModelAndView representing the admin page.
     */
    @GetMapping("/admin")
    public ModelAndView adminPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");

        // Retrieve list of users
        List<UserDTO> users = userService.findUsers();

        // Add users to the model
        modelAndView.addObject("_embedded", users);

        return modelAndView;
    }
}

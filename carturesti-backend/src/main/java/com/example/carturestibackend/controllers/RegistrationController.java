package com.example.carturestibackend.controllers;

import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/**
 * Controller class for handling user registration-related operations.
 */

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    /**
     * Displays the registration form.
     *
     * @return The name of the HTML template to render.
     */

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    /**
     * Registers a new user.
     *
     * @param name     The name of the user.
     * @param address  The address of the user.
     * @param email    The email of the user.
     * @param password The password of the user.
     * @param age      The age of the user.
     * @param model    The Model object to add attributes.
     * @return The name of the HTML template to render.
     */

    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("address") String address,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("age") int age,
                               Model model) {
        User registeredUser = registrationService.registerUser(name, address, email, password, age);
        if (registeredUser != null) {
            model.addAttribute("registeredUser", registeredUser);
            return "redirect:/login";
        } else {

            model.addAttribute("errorMessage", "User already exists");
            return "register";
        }
    }

    /**
     * Checks if a user exists by email.
     *
     * @param request The request body containing the email.
     * @return ResponseEntity with status CONFLICT if user exists, otherwise OK.
     */

    @PostMapping("/checkuser2")
    @ResponseBody
    public ResponseEntity<?> checkUserExists(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean userExists = registrationService.isUserExistsByEmail(email);
        if (userExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

}


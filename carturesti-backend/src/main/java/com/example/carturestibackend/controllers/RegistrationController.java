package com.example.carturestibackend.controllers;

import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

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


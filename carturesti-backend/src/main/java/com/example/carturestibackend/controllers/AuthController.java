package com.example.carturestibackend.controllers;

import com.example.carturestibackend.services.AuthService;
import com.example.carturestibackend.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
                        @RequestParam("password") String password,
                        RedirectAttributes attributes) {
        User user = authService.checkUser(name, password);

        if (user != null) {
            if ("admin".equals(user.getRole())) {
                return "redirect:/admin";
            } else if ("client".equals(user.getRole())) {
                return "redirect:/client";
            }
        }

        attributes.addFlashAttribute("error", "true");
        return "redirect:/login";
    }

    @PostMapping("/checkuser")
    public ResponseEntity<String> checkUser(@RequestParam String name, @RequestParam String password) {
        User user = authService.checkUser(name, password);
        if (user != null) {
            return ResponseEntity.ok().body(user.getRole());
        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

}

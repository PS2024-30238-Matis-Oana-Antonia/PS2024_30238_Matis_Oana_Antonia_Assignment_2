package com.example.carturestibackend.controllers;

import com.example.carturestibackend.services.AuthService;
import com.example.carturestibackend.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

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
                        HttpServletRequest request,
                        RedirectAttributes attributes) {
        User user = authService.checkUser(name, password);

        if (user != null) {
            String role = user.getRole();
            if ("admin".equals(role)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", name);
                return "redirect:/admin";
            } else if ("client".equals(role)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", name);
                return "redirect:/client";
            }
        }

        attributes.addFlashAttribute("error", "Invalid username or password");
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


package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.AuthService;
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

    @PostMapping("/checkuser")
    public ResponseEntity<UserDTO> checkUser(@RequestParam String name, @RequestParam String password) {
        UserDTO userDTO = authService.checkUser(name, password);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam("name") String name,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        RedirectAttributes attributes) {
        UserDTO userDTO = authService.checkUser(name, password);

        if (userDTO != null) {
            String role = userDTO.getRole();
            HttpSession session = request.getSession();
            session.setAttribute("username", name);
            session.setAttribute("userId", userDTO.getId_user()); // Store userID in session
            session.setAttribute("cartId", userDTO.getId_cart()); // Store cartId in session

            if ("admin".equals(role)) {
                return "redirect:/admin";
            } else if ("client".equals(role)) {
                return "redirect:/client";
            }
        }

        attributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/login";
    }
}


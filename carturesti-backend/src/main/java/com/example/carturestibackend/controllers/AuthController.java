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

/**
 * Controller class for handling authentication-related requests and rendering login/logout pages.
 */
@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Renders the login form page.
     *
     * @param error Indicates if there was an error during login attempt (optional).
     * @param model The Model object to which attributes can be added for rendering the view.
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
     * Checks if a user with the given credentials exists.
     *
     * @param name     The username entered by the user.
     * @param password The password entered by the user.
     * @return ResponseEntity containing the UserDTO object if authentication is successful,
     *         or an unauthorized status if authentication fails.
     */
    @PostMapping("/checkuser")
    public ResponseEntity<UserDTO> checkUser(@RequestParam String name, @RequestParam String password) {
        UserDTO userDTO = authService.checkUser(name, password);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Processes the login form submission and authenticates the user.
     *
     * @param name        The username entered by the user.
     * @param password    The password entered by the user.
     * @param request     The HttpServletRequest object representing the client request.
     * @param attributes  The RedirectAttributes object for adding flash attributes.
     * @return The redirect URL based on the user's role after successful login, or redirects
     *         back to the login page with an error message if authentication fails.
     */

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

            String redirectUrl = "/client/" + userDTO.getId_user(); // Build redirect URL with user ID

            if ("admin".equals(role)) {
                redirectUrl = "/admin";
            }

            return "redirect:" + redirectUrl;
        }

        attributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/login";
    }

}


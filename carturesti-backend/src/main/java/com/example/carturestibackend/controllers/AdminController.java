package com.example.carturestibackend.controllers;

import com.example.carturestibackend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class for handling admin-related requests and rendering admin pages.
 */

@Controller
public class AdminController {

    private final AuthService authService;

    @Autowired
    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Renders the admin page if the user is authenticated as an admin.
     *
     * @param request The HttpServletRequest object representing the client request.
     * @return A ModelAndView object representing the admin page view if authentication is successful,
     *         or redirects to the login page if the user is not authenticated or not an admin.
     */

    @GetMapping("/admin")
    public ModelAndView adminPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            return new ModelAndView("redirect:/login");
        }

        String username = (String) session.getAttribute("username");
        String role = authService.getRole(username);

        if (!"admin".equals(role)) {
            return new ModelAndView("redirect:/client");
        }

        modelAndView.setViewName("admin");
        return modelAndView;
    }


}

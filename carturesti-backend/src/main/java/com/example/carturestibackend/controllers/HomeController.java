package com.example.carturestibackend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class for handling requests related to the home page.
 */
@Controller
public class HomeController {

    /**
     * Handler method for GET requests to "/".
     * Displays the home page.
     * @return ModelAndView containing the view name for the home page.
     */
    @GetMapping("/")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }
}

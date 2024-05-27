package com.example.carturestibackend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class for displaying the order placed confirmation page.
 */
@Controller
public class OrderPlacedController {

    /**
     * Displays the order placed confirmation page.
     *
     * @return ModelAndView object representing the view and model for the page.
     */
    @GetMapping("/orderplaced")
    public ModelAndView orderPlacedPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("orderplaced");
        modelAndView.addObject("message", "Order placed successfully!");
        return modelAndView;
    }
}

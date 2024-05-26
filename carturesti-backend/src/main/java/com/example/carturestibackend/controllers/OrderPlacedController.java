package com.example.carturestibackend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OrderPlacedController {

    @GetMapping("/orderplaced")
    public ModelAndView orderPlacedPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("orderplaced");
        modelAndView.addObject("message", "Order placed successfully!");
        return modelAndView;
    }
}

package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.CartLogger;
import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.services.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller class to handle HTTP requests related to carts.
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/cart")
public class CartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping()
    public ModelAndView getCarts() {
        LOGGER.info(CartLogger.ALL_CARTS_RETRIEVED);
        List<CartDTO> dtos = cartService.findAllCarts();
        ModelAndView modelAndView = new ModelAndView("/carts");
        modelAndView.addObject("carts", dtos);
        return modelAndView;
    }

    @PostMapping()
    public ModelAndView insertCart(@Valid @RequestBody CartDTO cartDTO) {
        String cartID = cartService.insertCart(cartDTO);
        LOGGER.debug(CartLogger.CART_INSERTED, cartID);
        ModelAndView modelAndView = new ModelAndView("/carts");
        modelAndView.addObject("cartID", cartID);
        return modelAndView;
    }

    @GetMapping(value = "/{id_cart}")
    public ModelAndView getCart(@PathVariable("id_cart") String cartID) {
        LOGGER.info(CartLogger.CART_RETRIEVED_BY_ID, cartID);
        CartDTO dto = cartService.findCartById(cartID);
        ModelAndView modelAndView = new ModelAndView("/carts");
        modelAndView.addObject("cart", dto);
        return modelAndView;
    }

    @DeleteMapping(value = "/{id_cart}")
    public ModelAndView deleteCart(@PathVariable("id_cart") String cartID) {
        LOGGER.debug(CartLogger.CART_DELETED, cartID);
        cartService.deleteCartById(cartID);
        ModelAndView modelAndView = new ModelAndView("/carts");
        modelAndView.addObject("message", "Cart with ID " + cartID + " deleted successfully");
        return modelAndView;
    }

    @PutMapping(value = "/{id_cart}")
    public ModelAndView updateCart(@PathVariable("id_cart") String cartID, @Valid @RequestBody CartDTO cartDTO) {
        LOGGER.debug(CartLogger.CART_UPDATED, cartID);
        CartDTO updatedCart = cartService.updateCart(cartID, cartDTO);
        ModelAndView modelAndView = new ModelAndView("/carts");
        modelAndView.addObject("cart", updatedCart);
        return modelAndView;
    }
}

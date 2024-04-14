package com.example.carturestibackend.controllers;

import ch.qos.logback.core.model.Model;
import com.example.carturestibackend.constants.CartLogger;
import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.services.CartService;
import com.example.carturestibackend.services.ProductService;
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
    private final ProductService productService;

    @Autowired
    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping()
    public ModelAndView getCarts() {
        LOGGER.info(CartLogger.ALL_CARTS_RETRIEVED);
        List<CartDTO> dtos = cartService.findAllCarts();
        ModelAndView modelAndView = new ModelAndView("/cart");
        modelAndView.addObject("carts", dtos);
        return modelAndView;
    }

    @PostMapping()
    public ModelAndView insertCart(@Valid @RequestBody CartDTO cartDTO) {
        String cartID = cartService.insertCart(cartDTO);
        LOGGER.debug(CartLogger.CART_INSERTED, cartID);
        ModelAndView modelAndView = new ModelAndView("/cart");
        modelAndView.addObject("cartID", cartID);
        return modelAndView;
    }

    @GetMapping(value = "/{id_cart}")
    public ModelAndView getCart(@PathVariable("id_cart") String cartID) {
        LOGGER.info(CartLogger.CART_RETRIEVED_BY_ID, cartID);
        CartDTO dto = cartService.findCartById(cartID);
        ModelAndView modelAndView = new ModelAndView("/cart");
        modelAndView.addObject("cart", dto);
        return modelAndView;
    }

    @DeleteMapping(value = "/{id_cart}")
    public ModelAndView deleteCart(@PathVariable("id_cart") String cartID) {
        LOGGER.debug(CartLogger.CART_DELETED, cartID);
        cartService.deleteCartById(cartID);
        ModelAndView modelAndView = new ModelAndView("/cart");
        modelAndView.addObject("message", "Cart with ID " + cartID + " deleted successfully");
        return modelAndView;
    }

    @PutMapping(value = "/{id_cart}")
    public ModelAndView updateCart(@PathVariable("id_cart") String cartID, @Valid @RequestBody CartDTO cartDTO) {
        LOGGER.debug(CartLogger.CART_UPDATED, cartID);
        CartDTO updatedCart = cartService.updateCart(cartID, cartDTO);
        ModelAndView modelAndView = new ModelAndView("/cart");
        modelAndView.addObject("cart", updatedCart);
        return modelAndView;
    }

    @PostMapping("/addProduct/{id_product}")
    public ModelAndView addProductToCart(@PathVariable("id_product") String id_product) {
        ProductDTO product = productService.findProductById(id_product); // Assuming productService has a method to get product by ID
        if (product == null) {

            return new ModelAndView("error"); // Redirect to an error page
        }

        cartService.addProductToCart(String.valueOf(product));

        ModelAndView modelAndView = new ModelAndView("/cart");
        modelAndView.addObject("product", product); // Add product details to the model
        modelAndView.addObject("message", "Product added to cart successfully");
        return modelAndView;
    }

    @PostMapping("/shoppingCart/removeProduct/{id_product}")
    public ModelAndView removeProductFromCart(@PathVariable("id_product") String id_product) {
        cartService.removeProductFromCart(id_product);
        ModelAndView modelAndView = new ModelAndView("/cart");
        modelAndView.addObject("message", "Product removed from cart successfully");
        return modelAndView;
    }

    @GetMapping("/list")
    public ModelAndView shoppingCart() {
        ModelAndView modelAndView = new ModelAndView("/cart");
        modelAndView.addObject("productIds", cartService.getProductsInCartIds());
        return modelAndView;
    }


}
package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.services.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping()
    public ResponseEntity<List<CartDTO>> getCarts() {
        List<CartDTO> dtos = cartService.findAllCarts();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<String> insertCart(@Valid @RequestBody CartDTO cartDTO) {
        String cartID = cartService.insertCart(cartDTO);
        return new ResponseEntity<>(cartID, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id_cart}")
    public ResponseEntity<CartDTO> getCart(@PathVariable("id_cart") String cartID) {
        CartDTO dto = cartService.findCartById(cartID);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id_cart}")
    public ResponseEntity<String> deleteCart(@PathVariable("id_cart") String cartID) {
        cartService.deleteCartById(cartID);
        return new ResponseEntity<>("Cart with ID " + cartID + " deleted successfully", HttpStatus.OK);
    }

    @PutMapping(value = "/{id_cart}")
    public ResponseEntity<CartDTO> updateCart(@PathVariable("id_cart") String cartID, @Valid @RequestBody CartDTO cartDTO) {
        CartDTO updatedCart = cartService.updateCart(cartID, cartDTO);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }
}

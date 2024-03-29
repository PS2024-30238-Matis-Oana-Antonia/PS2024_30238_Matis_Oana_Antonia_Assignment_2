package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.CartLogger;
import com.example.carturestibackend.constants.CategoryLogger;
import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.dtos.mappers.CartMapper;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.repositories.CartRepository;
import com.example.carturestibackend.validators.CartValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final CartValidator cartValidator;

    @Autowired
    public CartService(CartRepository cartRepository, CartValidator cartValidator) {
        this.cartRepository = cartRepository;
        this.cartValidator = cartValidator;
    }

    public List<CartDTO> findAllCarts() {
        LOGGER.error(CartLogger.ALL_CARTS_RETRIEVED);
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(CartMapper::toCartDTO)
                .collect(Collectors.toList());
    }

    public CartDTO findCartById(String id_cart) {
        Optional<Cart> cartOptional = cartRepository.findById(id_cart);
        if (cartOptional.isPresent()) {
            return CartMapper.toCartDTO(cartOptional.get());
        } else {
            LOGGER.error(CartLogger.CART_NOT_FOUND_BY_ID, id_cart);
            throw new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + id_cart);
        }
    }

    public String insertCart(CartDTO cartDTO) {
        Cart cart = CartMapper.fromCartDTO(cartDTO);
        CartValidator.validateCart(cart);
        cart = cartRepository.save(cart);
        LOGGER.debug(CategoryLogger.CATEGORY_INSERTED, cart.getId_cart());
        return cart.getId_cart();
    }

    public void deleteCartById(String id_cart) {
        Optional<Cart> cartOptional = cartRepository.findById(id_cart);
        if (cartOptional.isPresent()) {
            cartRepository.delete(cartOptional.get());
            LOGGER.debug(CategoryLogger.CATEGORY_DELETED, id_cart);
        } else {
            LOGGER.error(CategoryLogger.CATEGORY_NOT_FOUND_BY_ID, id_cart);
            throw new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + id_cart);
        }
    }

    public CartDTO updateCart(String id_cart, CartDTO cartDTO) {
        Optional<Cart> cartOptional = cartRepository.findById(id_cart);
        if (cartOptional.isPresent()) {
            Cart existingCart = cartOptional.get();

            Cart updatedCart = cartRepository.save(existingCart);
            LOGGER.debug(CategoryLogger.CATEGORY_UPDATED, id_cart);

            return CartMapper.toCartDTO(updatedCart);
        } else {
            LOGGER.error(CategoryLogger.CATEGORY_NOT_FOUND_BY_ID, id_cart);
            throw new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + id_cart);
        }
    }
}

package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.CartLogger;
import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.dtos.mappers.CartMapper;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.repositories.CartRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.validators.CartValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to carts.
 */
@Service
public class CartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartValidator cartValidator;

    /**
     * Constructs a new CartService with the specified CartRepository and CartValidator.
     *
     * @param cartRepository    The CartRepository used to access cart data.
     * @param productRepository
     * @param cartValidator     The CartValidator used to validate cart data.
     */
    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, CartValidator cartValidator) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartValidator = cartValidator;
    }

    /**
     * Retrieves all carts.
     *
     * @return A list of CartDTO objects representing the carts.
     */
    public List<CartDTO> findAllCarts() {
        LOGGER.error(CartLogger.ALL_CARTS_RETRIEVED);
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(CartMapper::toCartDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a cart by its ID.
     *
     * @param id_cart The ID of the cart to retrieve.
     * @return The CartDTO object representing the retrieved cart.
     * @throws ResourceNotFoundException If the cart with the specified ID is not found.
     */
    public CartDTO findCartById(String id_cart) {
        Optional<Cart> cartOptional = cartRepository.findById(id_cart);
        if (cartOptional.isPresent()) {
            return CartMapper.toCartDTO(cartOptional.get());
        } else {
            LOGGER.error(CartLogger.CART_NOT_FOUND_BY_ID, id_cart);
            throw new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + id_cart);
        }
    }

    /**
     * Inserts a new cart.
     *
     * @param cartDTO The CartDTO object representing the cart to insert.
     * @return The ID of the newly inserted cart.
     */
    public String insertCart(CartDTO cartDTO) {
        Cart cart = CartMapper.fromCartDTO(cartDTO);
        CartValidator.validateCart(cart);
        cart = cartRepository.save(cart);
        LOGGER.debug(CartLogger.CART_INSERTED, cart.getId_cart());
        return cart.getId_cart();
    }

    /**
     * Deletes a cart by its ID.
     *
     * @param id_cart The ID of the cart to delete.
     * @throws ResourceNotFoundException If the cart with the specified ID is not found.
     */
    public void deleteCartById(String id_cart) {
        Optional<Cart> cartOptional = cartRepository.findById(id_cart);
        if (cartOptional.isPresent()) {
            cartRepository.delete(cartOptional.get());
            LOGGER.debug(CartLogger.CART_DELETED, id_cart);
        } else {
            LOGGER.error(CartLogger.CART_NOT_FOUND_BY_ID, id_cart);
            throw new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + id_cart);
        }
    }

    /**
     * Updates a cart.
     *
     * @param id_cart  The ID of the cart to update.
     * @param cartDTO The updated CartDTO object representing the new state of the cart.
     * @return The updated CartDTO object.
     * @throws ResourceNotFoundException If the cart with the specified ID is not found.
     */
    public CartDTO updateCart(String id_cart, CartDTO cartDTO) {
        Optional<Cart> cartOptional = cartRepository.findById(id_cart);
        if (cartOptional.isPresent()) {
            Cart existingCart = cartOptional.get();
            // Update the cart properties here as needed
            Cart updatedCart = cartRepository.save(existingCart);
            LOGGER.debug(CartLogger.CART_UPDATED, id_cart);
            return CartMapper.toCartDTO(updatedCart);
        } else {
            LOGGER.error(CartLogger.CART_NOT_FOUND_BY_ID, id_cart);
            throw new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + id_cart);
        }
    }

    /**
     * Adds a product to the cart.
     *
     * @param id_product The ID of the product to add to the cart.
     */
    @Transactional
    public void addProductToCart(String id_product) {

    }

    /**
     * Removes a product from the cart.
     *
     * @param id_product The ID of the product to remove from the cart.
     */
    @Transactional
    public void removeProductFromCart(String id_product) {

    }

    private List<String> id_products;

    /**
     * Retrieves the IDs of all products in the cart.
     *
     * @return A list of product IDs.
     */
    public List<String> getProductsInCartIds() {
        return Collections.unmodifiableList(id_products);
    }
}

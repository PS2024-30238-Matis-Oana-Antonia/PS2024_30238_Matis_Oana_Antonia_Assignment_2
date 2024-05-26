package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.CartLogger;
import com.example.carturestibackend.constants.ProductLogger;
import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.dtos.mappers.CartMapper;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.repositories.CartRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.validators.CartValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * Retrieves the cart ID associated with a given user.
     *
     * @param user The user whose cart ID needs to be found.
     * @return The ID of the cart associated with the user.
     * @throws ResourceNotFoundException If no cart is found for the given user.
     */
    public String findCartIdByUser(User user) {
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isPresent()) {
            return cartOptional.get().getId_cart();
        } else {
            LOGGER.error("No cart found for user with ID: {}", user.getId_user());
            throw new ResourceNotFoundException("Cart for user " + user.getId_user() + " not found.");
        }
    }

    /**
     * Adds a product to the cart.
     *
     * @param productId The ID of the product to add to the cart.
     */
    @Transactional
    public void addProductToCart(String cartId, String productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(Product.class.getSimpleName() + " with id: " + productId));

        Optional<OrderItem> existingOrderItem = cart.getOrderItems().stream()
                .filter(item -> item.getProduct().getId_product().equals(productId))
                .findFirst();

        if (existingOrderItem.isPresent()) {
            OrderItem orderItem = existingOrderItem.get();
            orderItem.setQuantity(orderItem.getQuantity() + 1);
        } else {
            OrderItem orderItem = OrderItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(1)
                    .price_per_unit(product.getPrice())
                    .build();
            cart.getOrderItems().add(orderItem);
        }

        // Recalculate total price
        double totalPrice = calculateTotalPrice(cart);
        cart.setTotal_price(totalPrice);

        cartRepository.save(cart);
        LOGGER.debug(CartLogger.PRODUCT_ADDED_TO_CART, productId, cartId);
    }

    @Transactional
    public void removeProductFromCart(String cartId, String productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + cartId));

        Optional<OrderItem> orderItemOptional = cart.getOrderItems().stream()
                .filter(item -> item.getProduct().getId_product().equals(productId))
                .findFirst();

        if (orderItemOptional.isPresent()) {
            OrderItem orderItem = orderItemOptional.get();
            if (orderItem.getQuantity() > 1) {
                orderItem.setQuantity(orderItem.getQuantity() - 1);
            } else {
                cart.getOrderItems().remove(orderItem);
            }

            // Recalculate total price
            double totalPrice = calculateTotalPrice(cart);
            cart.setTotal_price(totalPrice);

            cartRepository.save(cart);
            LOGGER.debug(CartLogger.PRODUCT_REMOVED_FROM_CART, productId, cartId);
        } else {
            LOGGER.error("Order item with productId {} not found in cart {}", productId, cartId);
            throw new ResourceNotFoundException("Order item not found in cart");
        }
    }

    private double calculateTotalPrice(Cart cart) {
        double totalPrice = cart.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice_per_unit() * item.getQuantity())
                .sum();
        cart.setTotal_price(totalPrice); // Set total_price in the Cart object
        return totalPrice;
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
        LOGGER.debug("Updating cart with ID: {}", id_cart);

        // Retrieve the existing cart from the database
        Cart existingCart = cartRepository.findById(id_cart)
                .orElseThrow(() -> {
                    LOGGER.error("Cart with ID {} not found in the database", id_cart);
                    return new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + id_cart);
                });

        // Update the cart properties based on the provided CartDTO
        if (cartDTO.getOrderItems() != null) {
            for (OrderItem orderItemDTO : cartDTO.getOrderItems()) {
                // Find the corresponding orderItem in the existing cart
                Optional<OrderItem> optionalOrderItem = existingCart.getOrderItems().stream()
                        .filter(orderItem -> orderItem.getId_order_item().equals(orderItemDTO.getId_order_item()))
                        .findFirst();

                // Update the quantity if the orderItem exists
                optionalOrderItem.ifPresent(orderItem -> {
                    LOGGER.debug("Updating quantity for order item with ID: {}", orderItem.getId_order_item());

                    orderItem.setQuantity(orderItemDTO.getQuantity());

                    // Recalculate the total price for the updated orderItem
                    double pricePerUnit = orderItem.getProduct().getPrice();
                    double totalPrice = pricePerUnit * orderItem.getQuantity();
                    orderItem.setPrice_per_unit(totalPrice);

                    LOGGER.debug("Quantity updated to {} for order item with ID: {}", orderItem.getQuantity(), orderItem.getId_order_item());
                });
            }
        }

        // Recalculate and update the total price for the cart
        double totalPrice = calculateTotalPrice(existingCart);
        existingCart.setTotal_price(totalPrice);

        // Save the updated cart to the database
        existingCart = cartRepository.save(existingCart);

        LOGGER.debug("Cart updated successfully for cart ID: {}", id_cart);

        // Return the updated CartDTO
        return CartMapper.toCartDTO(existingCart);
    }





    /**
     * Removes a product from the cart.
     *
     * @param productId The ID of the product to remove from the cart.
     */


    private List<String> id_products;

    /**
     * Retrieves the IDs of all products in the cart.
     *
     * @return A list of product IDs.
     */
    /**
     * Retrieves the IDs of all products in the cart.
     *
     * @return A list of product IDs.
     */
    public List<ProductDTO> getProductsInCart(String cartId) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            List<OrderItem> orderItems = cart.getOrderItems();
            List<ProductDTO> productDTOs = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                Product product = orderItem.getProduct();
                ProductDTO productDTO = new ProductDTO();
                productDTO.setId_product(product.getId_product());
                productDTO.setName(product.getName());
                productDTO.setPrice(product.getPrice());
                // Set other properties if needed
                // Set quantity for the product
                productDTO.setStock(orderItem.getQuantity());
                productDTOs.add(productDTO);
            }
            return productDTOs;
        } else {
            // Handle case when cart with given ID is not found
            return new ArrayList<>();
        }
    }
    /**
     * Removes all products from the cart.
     *
     * @param cartId The ID of the cart from which to remove all products.
     */
    @Transactional
    public void removeAllProductsFromCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(Cart.class.getSimpleName() + " with id: " + cartId));

        // Clear the list of order items in the cart
        cart.getOrderItems().clear();

        // Recalculate and update the total price for the cart
        double totalPrice = calculateTotalPrice(cart);
        cart.setTotal_price(totalPrice);

        // Save the updated cart to the database
        cartRepository.save(cart);

        LOGGER.debug("All products removed from cart with ID: {}", cartId);
    }

}




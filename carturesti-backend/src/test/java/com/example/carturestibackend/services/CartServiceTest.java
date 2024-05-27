package com.example.carturestibackend.services;

import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.repositories.CartRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.validators.CartValidator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartValidator cartValidator;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    public void testAddProductToCart() {
        // Arrange
        String cartId = "cartId";
        String productId = "productId";

        Cart cart = new Cart();
        cart.setId_cart(cartId);
        cart.setOrderItems(new ArrayList<>());  // Initializează cu o listă modificabilă

        Product product = new Product();
        product.setId_product(productId);
        product.setPrice(100.0);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        cartService.addProductToCart(cartId, productId);

        // Assert
        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).save(cart);

        assertEquals(1, cart.getOrderItems().size());
        OrderItem orderItem = cart.getOrderItems().get(0);
        assertEquals(productId, orderItem.getProduct().getId_product());
        assertEquals(1, orderItem.getQuantity());
        assertEquals(100.0, orderItem.getPrice_per_unit());
        assertEquals(100.0, cart.getTotal_price());
    }


    @Test
    @Transactional
    public void testAddExistingProductToCart() {

        String cartId = "cartId";
        String productId = "productId";

        Product product = new Product();
        product.setId_product(productId);
        product.setPrice(100.0);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(1);
        orderItem.setPrice_per_unit(100.0);

        Cart cart = new Cart();
        cart.setId_cart(cartId);
        cart.setOrderItems(Collections.singletonList(orderItem));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.addProductToCart(cartId, productId);

        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).save(cart);

        assertEquals(1, cart.getOrderItems().size());
        OrderItem updatedOrderItem = cart.getOrderItems().get(0);
        assertEquals(productId, updatedOrderItem.getProduct().getId_product());
        assertEquals(2, updatedOrderItem.getQuantity());
        assertEquals(100.0, updatedOrderItem.getPrice_per_unit());
        assertEquals(200.0, cart.getTotal_price());
    }

    @Test
    public void testAddProductToNonExistingCart() {
        // Arrange
        String cartId = "nonExistingCartId";
        String productId = "productId";

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.addProductToCart(cartId, productId));
        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, never()).findById(anyString());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddNonExistingProductToCart() {
        String cartId = "cartId";
        String productId = "nonExistingProductId";

        Cart cart = new Cart();
        cart.setId_cart(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addProductToCart(cartId, productId));
        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, never()).save(any(Cart.class));
    }
}

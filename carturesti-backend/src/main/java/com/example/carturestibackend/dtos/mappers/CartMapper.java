package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.User;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class CartMapper {
    private CartMapper() {

    }

    public static CartDTO toCartDTO(Cart cart) {
        return CartDTO.builder()
                .id_cart(cart.getId_cart())
                .user(cart.getUser())
                .orderItems(cart.getOrderItems())
                .total_price(Optional.ofNullable(cart.getTotal_price()).orElse(0.0))
                .build();
    }
    public static Cart fromCartDTO(CartDTO cartDTO) {

        return Cart.builder()
                .id_cart(cartDTO.getId_cart())
                .user(cartDTO.getUser())
                .orderItems(cartDTO.getOrderItems())
                .total_price(cartDTO.getTotal_price())
                .build();
    }
}
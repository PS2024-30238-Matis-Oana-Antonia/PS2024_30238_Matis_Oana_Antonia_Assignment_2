package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.User;

public class CartMapper {
    private CartMapper() {

    }

    public static CartDTO toCartDTO(Cart cart) {

        return CartDTO.builder()
                .id_cart(cart.getId_cart())
                .user(cart.getUser())
                .orderItems(cart.getOrderItems())
                .build();
    }

    public static Cart fromCartDTO(CartDTO cartDTO) {

        return Cart.builder()
                .user(cartDTO.getUser())
                .orderItems(cartDTO.getOrderItems())
                .build();
    }
}


package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.CartDTO;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.OrderItem;
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
                .id_user(Optional.ofNullable(cart.getUser()).map(User::getId_user).orElse(null))
                .id_orderItems(Optional.ofNullable(cart.getOrderItems())
                        .map(items -> items.stream().map(OrderItem::getId_order_item).collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }

    public static Cart fromCartDTO(CartDTO cartDTO) {

        return Cart.builder()
                .user(User.builder()
                        .id_user(cartDTO.getId_user())
                        .build())
                .orderItems(Optional.ofNullable(cartDTO.getId_orderItems())
                        .map(ids -> ids.stream().map(id -> OrderItem.builder().id_order_item(id).build()).collect(Collectors.toList()))
                        .orElse(Collections.emptyList())) // Default to empty list if null
                .build();
    }
}
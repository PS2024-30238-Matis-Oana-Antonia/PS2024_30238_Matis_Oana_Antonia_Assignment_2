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
                .id_user(Optional.ofNullable(cart.getUser()).map(User::getId_user).orElse(null))
                .id_products(Optional.ofNullable(cart.getProducts())
                        .map(items -> items.stream().map(Product::getId_product).collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }

    public static Cart fromCartDTO(CartDTO cartDTO) {

        return Cart.builder()
                .user(User.builder()
                        .id_user(cartDTO.getId_user())
                        .build())
                .products(Optional.ofNullable(cartDTO.getId_products())
                        .map(ids -> ids.stream().map(id -> Product.builder().id_product(id).build()).collect(Collectors.toList()))
                        .orElse(Collections.emptyList())) // Default to empty list if null
                .build();
    }
}
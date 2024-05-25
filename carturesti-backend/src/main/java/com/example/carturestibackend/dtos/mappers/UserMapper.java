package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.Review;
import com.example.carturestibackend.entities.User;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id_user(user.getId_user())
                .name(user.getName())
                .address(user.getAddress())
                .email(user.getEmail())
                .password(user.getPassword())
                .age(user.getAge())
                .role(user.getRole())
                .id_reviews(Optional.ofNullable(user.getReviews())
                        .orElse(Collections.emptyList()).stream()
                        .map(Review::getId).collect(Collectors.toList()))
                .id_orders(Optional.ofNullable(user.getOrders())
                        .orElse(Collections.emptyList()).stream()
                        .map(Order::getId_order).collect(Collectors.toList()))
                .id_cart(user.getCart() != null ? user.getCart().getId_cart() : null)
                .build();
    }

    public static User fromUserDTO(UserDTO userDto) {
        User.UserBuilder userBuilder = User.builder()
                .name(userDto.getName())
                .address(userDto.getAddress())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .age(userDto.getAge())
                .role(userDto.getRole())
                .reviews(Optional.ofNullable(userDto.getId_reviews())
                        .orElse(Collections.emptyList()).stream()
                        .map(id -> {
                            Review review = new Review();
                            review.setId(id);
                            return review;
                        })
                        .collect(Collectors.toList()))
                .orders(Optional.ofNullable(userDto.getId_orders())
                        .orElse(Collections.emptyList()).stream()
                        .map(id -> {
                            Order order = new Order();
                            order.setId_order(id);
                            return order;
                        })
                        .collect(Collectors.toList()));

        if (userDto.getId_cart() != null) {
            userBuilder.cart(Cart.builder().id_cart(userDto.getId_cart()).build());
        }

        return userBuilder.build();
    }
}

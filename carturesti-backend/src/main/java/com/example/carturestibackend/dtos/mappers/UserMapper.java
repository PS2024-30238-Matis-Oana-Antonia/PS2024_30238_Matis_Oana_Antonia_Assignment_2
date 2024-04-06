package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.entities.*;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.Optional;

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
                .id_reviews(Optional.ofNullable(user.getReviews()).orElse(Collections.emptyList()).stream().map(Review::getId).collect(Collectors.toList()))
                .id_orders(Optional.ofNullable(user.getOrders()).orElse(Collections.emptyList()).stream().map(Order::getId_order).collect(Collectors.toList()))
                .id_cart(Optional.ofNullable(user.getCart()).map(Cart::getId_cart).orElse(null))
                .build();
    }

    public static User fromUserDTO(UserDTO userDto) {

        return User.builder()
                .name(userDto.getName())
                .address(userDto.getAddress())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .age(userDto.getAge())
                .role(userDto.getRole())
                .reviews(Optional.ofNullable(userDto.getId_reviews()).orElse(Collections.emptyList()).stream().map(id -> Review.builder().id(id).build()).collect(Collectors.toList()))
                .orders(Optional.ofNullable(userDto.getId_orders()).orElse(Collections.emptyList()).stream().map(id -> Order.builder().id_order(id).build()).collect(Collectors.toList()))
                .build();
    }
}


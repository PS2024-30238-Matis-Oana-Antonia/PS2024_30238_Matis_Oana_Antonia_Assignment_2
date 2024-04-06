package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.Review;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private String id_user;
    private String name;
    private String address;
    private String email;
    private String password;
    private int age;
    private String role;
    private List<String> id_reviews;
    private List<String> id_orders;
    private String id_cart;

}

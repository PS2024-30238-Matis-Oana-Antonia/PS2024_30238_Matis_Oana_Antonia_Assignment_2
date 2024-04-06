package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private String id_cart;
    private String id_user;
    private List<String> id_orderItems;
}

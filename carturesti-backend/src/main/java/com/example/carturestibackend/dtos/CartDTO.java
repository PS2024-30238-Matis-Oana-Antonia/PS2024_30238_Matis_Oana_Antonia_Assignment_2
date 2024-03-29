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
    private User user;
    private List<OrderItem> orderItems;
}

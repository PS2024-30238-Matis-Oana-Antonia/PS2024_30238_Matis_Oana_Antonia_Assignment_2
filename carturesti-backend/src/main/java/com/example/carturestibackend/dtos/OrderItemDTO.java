package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.Product;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

    private String id_order_item;
    private long quantity;
    private double price_per_unit;
    private String id_order;
    private List<String> id_products;
    private Cart cart;

}

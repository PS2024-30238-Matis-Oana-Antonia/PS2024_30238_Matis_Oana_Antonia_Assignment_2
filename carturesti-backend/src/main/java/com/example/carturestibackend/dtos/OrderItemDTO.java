package com.example.carturestibackend.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

    private String id_order_item;
    private long quantity;
    private double price_per_unit;
    private List<String> id_products;
    private String id_user;
    private String id_order;

}

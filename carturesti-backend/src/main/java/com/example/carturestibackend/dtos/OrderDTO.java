package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.OrderItem;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private String id_order;
    private LocalDate order_date;
    private double total_price;
    private long total_quantity;
    private String status;
    private String id_user;
    private List<String> id_products;
    private List<OrderItem> orderItems;

}

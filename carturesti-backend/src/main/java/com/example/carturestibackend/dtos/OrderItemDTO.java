package com.example.carturestibackend.dtos;

import io.micrometer.common.lang.Nullable;
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
    private String id_product;
    private String id_cart;
    @Nullable
    private Long orderId;

}

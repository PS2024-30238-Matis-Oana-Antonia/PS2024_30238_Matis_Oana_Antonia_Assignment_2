package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.Product;

import java.util.Optional;
import java.util.stream.Collectors;

public class OrderItemMapper {

    private OrderItemMapper() {
    }

    public static OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id_order_item(orderItem.getId_order_item())
                .quantity(orderItem.getQuantity())
                .price_per_unit(orderItem.getPrice_per_unit())
                .id_products(Optional.ofNullable(orderItem.getProducts())
                        .map(products -> products.stream().map(Product::getId_product).collect(Collectors.toList()))
                        .orElse(null))
                .build();


    }

    public static OrderItem fromOrderItemDTO(OrderItemDTO orderItemDTO) {
        return OrderItem.builder()
                .quantity(orderItemDTO.getQuantity())
                .price_per_unit(orderItemDTO.getPrice_per_unit())
                .products(Optional.ofNullable(orderItemDTO.getId_products())
                        .map(ids -> ids.stream().map(id -> Product.builder().id_product(id).build()).collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }
}

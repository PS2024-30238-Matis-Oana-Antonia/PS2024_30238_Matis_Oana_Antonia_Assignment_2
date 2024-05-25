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
                .id_product(orderItem.getProduct().getId_product())
                .id_cart(orderItem.getCart().getId_cart())
                .build();


    }

    public static OrderItem fromOrderItemDTO(OrderItemDTO orderItemDTO) {
        return OrderItem.builder()
                .id_order_item(orderItemDTO.getId_order_item())
                .quantity(orderItemDTO.getQuantity())
                .price_per_unit(orderItemDTO.getPrice_per_unit())
                .build();
    }
}

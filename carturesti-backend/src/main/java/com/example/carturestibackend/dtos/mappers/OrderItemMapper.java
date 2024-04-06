package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.Product;

import java.util.stream.Collectors;

public class OrderItemMapper {

    private OrderItemMapper() {
    }

    public static OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id_order_item(orderItem.getId_order_item())
                .quantity(orderItem.getQuantity())
                .price_per_unit(orderItem.getPrice_per_unit())
                .id_order(orderItem.getOrder().getId_order())
                .id_products(orderItem.getProducts().stream().map(Product::getId_product).collect(Collectors.toList()))
                .cart(orderItem.getCart())
                .build();
    }

    public static OrderItem fromOrderItemDTO(OrderItemDTO orderItemDTO) {
        return OrderItem.builder()
                .quantity(orderItemDTO.getQuantity())
                .price_per_unit(orderItemDTO.getPrice_per_unit())
                .order(Order.builder()
                        .id_order(orderItemDTO.getId_order())
                        .build())
                .products(orderItemDTO.getId_products().stream().map(productId -> Product.builder().id_product(productId).build()).collect(Collectors.toList()))
                .cart(orderItemDTO.getCart())
                .build();
    }
}

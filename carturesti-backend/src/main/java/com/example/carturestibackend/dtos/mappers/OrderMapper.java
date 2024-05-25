package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderMapper {
    private OrderMapper() {
    }
    public static OrderDTO toOrderDTO(Order order) {
        return OrderDTO.builder()
                .id_order(order.getId_order())
                .order_date(order.getOrder_date())
                .total_quantity(order.getTotal_quantity())
                .total_price(order.getTotal_price())
                .id_user(Optional.ofNullable(order.getUser()).map(User::getId_user).orElse(null))
                .id_products(Optional.ofNullable(order.getProducts())
                        .map(items -> items.stream().map(Product::getId_product).collect(Collectors.toList()))
                        .orElse(null)) // Map only IDs if order items not null
                .orderItems(order.getOrderItems())
                .build();
    }

    public static Order fromOrderDTO(OrderDTO orderDTO) {
        return Order.builder()
                .order_date(orderDTO.getOrder_date())
                .total_price(orderDTO.getTotal_price())
                .total_quantity(orderDTO.getTotal_quantity())
                .user(Optional.ofNullable(orderDTO.getId_user()).map(id -> User.builder().id_user(id).build()).orElse(null))
                .products(Optional.ofNullable(orderDTO.getId_products())
                        .map(ids -> ids.stream().map(id -> Product.builder().id_product(id).build()).collect(Collectors.toList()))
                        .orElse(null))
                .orderItems(orderDTO.getOrderItems())
                .build();
    }


}
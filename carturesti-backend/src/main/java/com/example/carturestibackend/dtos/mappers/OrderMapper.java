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
                .id_orderItems(Optional.ofNullable(order.getOrderItems())
                        .map(items -> items.stream().map(OrderItem::getId_order_item).collect(Collectors.toList()))
                        .orElse(null)) // Map only IDs if order items not null
                .build();
    }

    public static Order fromOrderDTO(OrderDTO orderDTO) {
        return Order.builder()
                .order_date(orderDTO.getOrder_date())
                .total_price(orderDTO.getTotal_price())
                .total_quantity(orderDTO.getTotal_quantity())
                .user(Optional.ofNullable(orderDTO.getId_user()).map(id -> User.builder().id_user(id).build()).orElse(null))
                .orderItems(Optional.ofNullable(orderDTO.getId_orderItems())
                        .map(ids -> ids.stream().map(id -> OrderItem.builder().id_order_item(id).build()).collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }


}
package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class OrderValidator {

    public static boolean validateOrder(Order order) {
        return validateOrderDate(order.getOrder_date()) && validateTotalPrice(order.getTotal_price());
    }

    private static boolean validateOrderDate(LocalDate orderDate) {
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date must not be null");
        }
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        if (orderDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("Order date must be current or future date");
        }
        return true;
    }

    private static boolean validateTotalPrice(double totalPrice) {
        if (totalPrice <= 0) {
            throw new IllegalArgumentException("Total price must be greater than 0");
        }
        return true;
    }
}

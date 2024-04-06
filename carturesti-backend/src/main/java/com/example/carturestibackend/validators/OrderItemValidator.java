package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemValidator {

    public static void validateOrderItem(OrderItem orderItem) {
        validateQuantity(orderItem.getQuantity());
        validatePricePerUnit(orderItem.getPrice_per_unit());

    }

    private static void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    private static void validatePricePerUnit(double pricePerUnit) {
        if (pricePerUnit <= 0) {
            throw new IllegalArgumentException("Price per unit must be greater than 0");
        }
    }
}

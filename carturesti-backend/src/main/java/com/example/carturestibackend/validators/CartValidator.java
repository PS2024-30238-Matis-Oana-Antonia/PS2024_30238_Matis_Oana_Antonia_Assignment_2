
package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Cart;
import org.springframework.stereotype.Component;

@Component
public class CartValidator {

    public static boolean validateCart(Cart cart) {
        if (cart == null || cart.getUser() == null) {
            throw new IllegalArgumentException("Cart object or associated user cannot be null");
        }
        return true;
    }
}

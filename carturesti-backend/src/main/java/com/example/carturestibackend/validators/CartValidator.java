
package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Cart;
import org.springframework.stereotype.Component;

@Component
public class CartValidator {

    public static void validateCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart object cannot be null");
        }

        if (cart.getUser() == null) {
            throw new IllegalArgumentException("Cart must be associated with a valid user");
        }

        // Puteți adăuga alte verificări pentru a valida lista de produse comandate sau alte atribute ale coșului
    }
}

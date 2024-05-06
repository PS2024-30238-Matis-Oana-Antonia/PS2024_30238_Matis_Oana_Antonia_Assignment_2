package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Payment;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PaymentValidator {

    // Regular expression for validating card numbers
    private static final String CARD_NUMBER_REGEX = "^[0-9]{16}$";

    public static boolean validate(Payment payment) {
        if (payment == null) {
            return false;
        }

        if (payment.getCard_owner() == null || payment.getCard_owner().trim().isEmpty()) {
            return false;
        }

        if (payment.getCard_number() == null || payment.getCard_number().trim().isEmpty() ||
                !Pattern.matches(CARD_NUMBER_REGEX, payment.getCard_number())) {
            return false;
        }

        return true;
    }
}

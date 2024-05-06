package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionValidator {

    public boolean validatePromotion(Promotion promotion) {
        if (promotion.getPercentage() < 0 || promotion.getPercentage() > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        if (promotion.getName() == null || promotion.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (promotion.getDescription() == null || promotion.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        return true;
    }
}

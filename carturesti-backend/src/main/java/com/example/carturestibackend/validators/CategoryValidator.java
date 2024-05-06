package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

    public static boolean validateCategory(Category category) {
        return validateName(category.getName()) && validateDescription(category.getDescription());
    }

    private static boolean validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        return true;
    }

    private static boolean validateDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description must not be null or empty");
        }
        return true;
    }
}

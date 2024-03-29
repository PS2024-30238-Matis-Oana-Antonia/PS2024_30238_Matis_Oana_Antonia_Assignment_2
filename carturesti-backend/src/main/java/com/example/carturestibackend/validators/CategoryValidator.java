package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

    public static void validateCategory(Category category) {
        validateName(category.getName());
        validateDescription(category.getDescription());

    }
    private static void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }

    }
    private static void validateDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description must not be null or empty");
        }

    }
}

package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    public static void validateProduct(Product product) {
        validateName(product.getName());
        validatePrice(product.getPrice());
        validateDescription(product.getDescription());
        validateAuthor(product.getAuthor());
        validateStock(product.getStock());

    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    private static void validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
    }

    private static void validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
    }

    private static void validateStock(long stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }

}

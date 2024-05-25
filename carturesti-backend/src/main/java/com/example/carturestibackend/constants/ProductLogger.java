package com.example.carturestibackend.constants;

public class ProductLogger {
    public static final String PRODUCT_NOT_FOUND_BY_ID = "Product with id {} was not found in the database";
    public static final String PRODUCT_NOT_FOUND_BY_NAME = "Product with name {} was not found in the database";
    public static final String PRODUCT_RETRIEVED_BY_ID = "Product with id {} retrieved from the database";
    public static final String PRODUCT_INSERTED = "Product with id {} was inserted into the database";
    public static final String PRODUCT_DELETED = "Product with id {} was deleted from the database";
    public static final String PRODUCT_UPDATED = "Product with id {} was updated in the database";
    public static final String ALL_PRODUCTS_RETRIEVED = "All products retrieved successfully";
    public static final String STOCK_DECREASED = "Stock decreased for product {} by {} units. New stock: {}";
    public static final String INSUFFICIENT_STOCK_TO_DECREASE = "Insufficient stock for product {} to decrease by {}. Stock: {}";
    public static final String STOCK_INCREASED = "Stock increased for product {} by {} units. New stock: {}";
    public static final String PRODUCT_NOT_FOUND_IN_CART = "Product not found in cart!";
}

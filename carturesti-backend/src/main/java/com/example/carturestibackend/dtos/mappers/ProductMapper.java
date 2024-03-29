package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Category;
import com.example.carturestibackend.entities.Review;

import java.util.ArrayList;
import java.util.List;

public class ProductMapper {

    private ProductMapper() {
    }

    public static ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id_product(product.getId_product())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .author(product.getAuthor())
                .stock(product.getStock())
                .reviews(product.getReviews())
                .category(product.getCategory())
                .orderItems(product.getOrderItems())
                .promotion(product.getPromotion())
                .sale(product.getSale())
                .build();
    }

    public static Product fromProductDTO(ProductDTO productDTO) {
        return Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .author(productDTO.getAuthor())
                .stock(productDTO.getStock())
                .reviews(productDTO.getReviews())
                .category(productDTO.getCategory())
                .orderItems(productDTO.getOrderItems())
                .promotion(productDTO.getPromotion())
                .sale(productDTO.getSale())
                .build();
    }
}

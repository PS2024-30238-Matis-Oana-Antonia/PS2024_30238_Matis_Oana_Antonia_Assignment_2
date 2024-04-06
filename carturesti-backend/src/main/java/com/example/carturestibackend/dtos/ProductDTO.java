package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private String id_product;
    private String name;
    private double price;
    private String description;
    private String author;
    private long stock;
    private List<String> id_reviews;
    private String id_category;
    private List<String> id_orderItems; // Changed to hold OrderItemDTO instead of just IDs
    private String id_promotion;
    private String id_sale;
}

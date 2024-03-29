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
    private List<Review> reviews;
    private Category category;
    private List<OrderItem> orderItems;
    private Promotion promotion;
    private Sale sale;
}

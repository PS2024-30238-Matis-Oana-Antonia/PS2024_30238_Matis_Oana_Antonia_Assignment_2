package com.example.carturestibackend.dtos;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private double price_promotion;
    private String description;
    private String author;
    private long stock;
    private List<String> id_reviews;
    private String id_category;
    private List<String> id_orders;
    private List<String> id_order_items;
    private String id_promotion;
    private String id_sale;

}
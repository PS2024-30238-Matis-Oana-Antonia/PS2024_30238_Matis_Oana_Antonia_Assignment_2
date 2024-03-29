package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.Product;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleDTO {
    private String id_sale;
    private double discount_percentage;
    private double price_after_discount;
    private List<Product> products;
}

package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {

    private String id_promotion;
    private String name;
    private String description;
    private double percentage;
    private List<String> id_products;
}

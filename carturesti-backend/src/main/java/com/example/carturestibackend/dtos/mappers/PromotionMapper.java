package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.PromotionDTO;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Promotion;
import com.example.carturestibackend.entities.Review;

import java.util.Optional;
import java.util.stream.Collectors;

public class PromotionMapper {

    private PromotionMapper() {
    }

    public static PromotionDTO toPromotionDTO(Promotion promotion) {
        return PromotionDTO.builder()
                .id_promotion(promotion.getId_promotion())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .percentage(promotion.getPercentage())
                .id_products(promotion.getProducts().stream().map(Product::getId_product).collect(Collectors.toList()))
                .build();
    }

    public static Promotion fromPromotionDTO(PromotionDTO promotionDTO) {
        return Promotion.builder()
                .name(promotionDTO.getName())
                .description(promotionDTO.getDescription())
                .percentage(promotionDTO.getPercentage())
                .products(promotionDTO.getId_products().stream().map(id -> Product.builder().id_product(id).build()).collect(Collectors.toList()))
                .build();
    }
}


package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.CategoryDTO;
import com.example.carturestibackend.entities.Category;
import com.example.carturestibackend.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDTO toCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id_category(category.getId_category())
                .name(category.getName())
                .description(category.getDescription())
                .id_products(Optional.ofNullable(category.getProducts())
                        .map(products -> products.stream().map(Product::getId_product).collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }

    public static Category fromCategoryDTO(CategoryDTO categoryDTO) {
        return Category.builder()
                .id_category(categoryDTO.getId_category())
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .products(Optional.ofNullable(categoryDTO.getId_products())
                        .map(ids -> ids.stream().map(id -> Product.builder().id_product(id).build()).collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }
}

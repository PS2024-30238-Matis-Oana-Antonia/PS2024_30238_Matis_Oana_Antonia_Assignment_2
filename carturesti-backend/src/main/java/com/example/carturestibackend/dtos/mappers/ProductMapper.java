package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .id_reviews(Optional.ofNullable(product.getReviews())
                        .map(items -> items.stream().map(Review::getId).collect(Collectors.toList()))
                        .orElse(null))
                .id_category(Optional.ofNullable(product.getCategory())
                        .map(Category::getId_category)
                        .orElse(null))
                .id_orderItems(Optional.ofNullable(product.getOrderItems())
                        .map(items -> items.stream().map(OrderItem::getId_order_item).collect(Collectors.toList()))
                        .orElse(null))
                .id_promotion(Optional.ofNullable(product.getPromotion())
                        .map(Promotion::getId_promotion)
                        .orElse(null))
                .id_sale(Optional.ofNullable(product.getSale())
                        .map(Sale::getId_sale)
                        .orElse(null))
                .id_order(Optional.ofNullable(product.getOrder())
                .map(Order::getId_order)
                .orElse(null))

                .build();
    }

    public static Product fromProductDTO(ProductDTO productDTO) {
        return Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .author(productDTO.getAuthor())
                .stock(productDTO.getStock())
                .reviews(Optional.ofNullable(productDTO.getId_reviews())
                        .map(ids -> ids.stream().map(id -> Review.builder().id(id).build()).collect(Collectors.toList()))
                        .orElse(null))
                .category(Optional.ofNullable(productDTO.getId_category())
                        .map(id -> Category.builder().id_category(id).build())
                        .orElse(null))
                .orderItems(Optional.ofNullable(productDTO.getId_orderItems())
                        .map(ids -> ids.stream().map(id -> OrderItem.builder().id_order_item(id).build()).collect(Collectors.toList()))
                        .orElse(null))
                .promotion(Optional.ofNullable(productDTO.getId_promotion())
                        .map(id -> Promotion.builder().id_promotion(id).build())
                        .orElse(null))
                .sale(Optional.ofNullable(productDTO.getId_sale())
                        .map(id -> Sale.builder().id_sale(id).build())
                        .orElse(null))
                .order(Optional.ofNullable(productDTO.getId_order())
                        .map(id -> Order.builder().id_order(id).build())
                        .orElse(null))
                .build();
    }
}

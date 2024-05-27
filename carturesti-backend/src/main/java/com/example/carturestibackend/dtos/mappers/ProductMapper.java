
package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.entities.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

public class ProductMapper {

    private ProductMapper() {
    }

    public static ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id_product(product.getId_product())
                .name(product.getName())
                .price(product.getPrice())
                .price_promotion(product.getPrice_promotion())
                .description(product.getDescription())
                .author(product.getAuthor())
                .stock(product.getStock())
                .id_reviews(Optional.ofNullable(product.getReviews())
                        .map(items -> items.stream().map(Review::getId).collect(Collectors.toList()))
                        .orElse(null))
                .id_category(Optional.ofNullable(product.getCategory())
                        .map(Category::getId_category)
                        .orElse(null))
                .id_orders(Optional.ofNullable(product.getOrders())
                        .map(items -> items.stream().map(Order::getId_order).collect(Collectors.toList()))
                        .orElse(null))
                .id_order_items(Optional.ofNullable(product.getOrderItems())
                        .map(items -> items.stream().map(OrderItem::getId_order_item).collect(Collectors.toList()))
                        .orElse(null))
                .id_promotion(Optional.ofNullable(product.getPromotion())
                        .map(Promotion::getId_promotion)
                        .orElse(null))
                .build();
    }

    public static Product fromProductDTO(ProductDTO productDTO) {
        return Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .price_promotion(productDTO.getPrice_promotion())
                .description(productDTO.getDescription())
                .author(productDTO.getAuthor())
                .stock(productDTO.getStock())
                .reviews(Optional.ofNullable(productDTO.getId_reviews())
                        .orElse(Collections.emptyList()).stream()
                        .map(id -> {
                            Review review = new Review();
                            review.setId(id);
                            return review;
                        })
                        .collect(Collectors.toList()))
                .category(Optional.ofNullable(productDTO.getId_category())
                        .map(id -> {
                            Category category = new Category();
                            category.setId_category(id);
                            return category;
                        })
                        .orElse(null))
                .orders(Optional.ofNullable(productDTO.getId_orders())
                        .orElse(Collections.emptyList()).stream()
                        .map(id -> {
                            Order order = new Order();
                            order.setId_order(id);
                            return order;
                        })
                        .collect(Collectors.toList()))
                .orderItems(Optional.ofNullable(productDTO.getId_order_items())
                        .orElse(Collections.emptyList()).stream()
                        .map(id -> {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setId_order_item(id);
                            return orderItem;
                        })
                        .collect(Collectors.toList()))
                .promotion(Optional.ofNullable(productDTO.getId_promotion())
                        .map(id -> {
                            Promotion promotion = new Promotion();
                            promotion.setId_promotion(id);
                            return promotion;
                        })
                        .orElse(null))

                .build();
    }
}

package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.ReviewDTO;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Review;
import com.example.carturestibackend.entities.User;

import java.util.Optional;

import static com.example.carturestibackend.dtos.mappers.UserMapper.toUserDTO;
import static com.example.carturestibackend.dtos.mappers.UserMapper.fromUserDTO;

public class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewDTO toReviewDTO(Review review) {
        if (review == null) {
            return null;
        }

        return ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .id_user(Optional.ofNullable(review.getUser()).map(User::getId_user).orElse(null))
                .id_product(Optional.ofNullable(review.getProduct()).map(Product::getId_product).orElse(null))
                .build();
    }

    public static Review fromReviewDTO(ReviewDTO reviewDTO) {
        if (reviewDTO == null) {
            return null;
        }

        return Review.builder()
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .user(Optional.ofNullable(reviewDTO.getId_user()).map(id -> User.builder().id_user(id).build()).orElse(null))
                .product(Optional.ofNullable(reviewDTO.getId_product()).map(id -> Product.builder().id_product(id).build()).orElse(null))
                .build();
    }
}
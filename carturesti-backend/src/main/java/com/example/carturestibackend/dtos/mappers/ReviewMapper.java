package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.ReviewDTO;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Review;
import com.example.carturestibackend.entities.User;

import static com.example.carturestibackend.dtos.mappers.UserMapper.toUserDTO;
import static com.example.carturestibackend.dtos.mappers.UserMapper.fromUserDTO;

public class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewDTO toReviewDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .id_user(review.getUser().getId_user())
                .id_product(review.getProduct().getId_product())
                .build();
    }

    public static Review fromReviewDTO(ReviewDTO reviewDTO) {
        return Review.builder()
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .user(User.builder()
                        .id_user(reviewDTO.getId_user())
                        .build())
                .product(Product.builder()
                        .id_product(reviewDTO.getId_product())
                        .build())
                .build();
    }
}

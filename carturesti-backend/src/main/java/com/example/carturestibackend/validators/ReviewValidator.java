package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidator {

    public static boolean validateReview(Review review) {
        if (review == null) {
            return false;
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (review.getComment() == null || review.getComment().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        return true;
    }
}

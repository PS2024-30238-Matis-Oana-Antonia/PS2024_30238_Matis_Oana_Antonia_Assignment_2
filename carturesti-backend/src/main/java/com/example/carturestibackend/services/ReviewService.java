package com.example.carturestibackend.services;

import com.example.carturestibackend.config.RabbitSender;
import com.example.carturestibackend.constants.ReviewLogger;
import com.example.carturestibackend.dtos.NotificationRequestDTO;
import com.example.carturestibackend.dtos.ReviewDTO;
import com.example.carturestibackend.dtos.mappers.ReviewMapper;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Review;
import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.repositories.ReviewRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.repositories.UserRepository;
import com.example.carturestibackend.validators.ReviewValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to reviews.
 */
@Service
@Transactional
public class ReviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewValidator reviewValidator;
    /**
     * Constructs a new ReviewService with the specified ReviewRepository.
     *
     * @param reviewRepository  The ReviewRepository used to interact with review data in the database.
     * @param userRepository
     * @param productRepository
     * @param reviewValidator
     */
    @Autowired
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, ProductRepository productRepository, ReviewValidator reviewValidator) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.reviewValidator = reviewValidator;
    }

    /**
     * Retrieves all reviews from the database.
     *
     * @return A list of ReviewDTO objects representing the reviews.
     */
    public List<ReviewDTO> findReviews() {
        LOGGER.error(ReviewLogger.ALL_REVIEWS_RETRIEVED);
        List<Review> reviewList = reviewRepository.findAll();
        return reviewList.stream()
                .map(ReviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param id The ID of the review to retrieve.
     * @return The ReviewDTO object representing the retrieved review.
     * @throws ResourceNotFoundException if the review with the specified ID is not found.
     */
    public ReviewDTO findReviewById(String id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (!reviewOptional.isPresent()) {
            LOGGER.error(ReviewLogger.REVIEW_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Review.class.getSimpleName() + " with id: " + id);
        }
        return ReviewMapper.toReviewDTO(reviewOptional.get());
    }

    @Autowired
    private RabbitSender rabbitSender;

    public String buildEmailMessage(User user, String productName, ReviewDTO reviewDTO) {
        StringBuilder body = new StringBuilder();
        body.append("Hello, ").append(user.getName()).append(",<br><br>")
                .append("Your review has been added for the product '").append(productName).append("'.<br><br>")
                .append("Review Details:<br>")
                .append("&emsp;Rating: ").append(reviewDTO.getRating()).append("/5<br>")
                .append("&emsp;Comment: ").append(reviewDTO.getComment()).append("<br><br>")
                .append("Thank you for your dedication!<br>")
                .append("The Cărturești Team.");

        return body.toString();
    }


    /**
     * Inserts a new review into the database.
     *
     * @param reviewDTO The ReviewDTO object representing the review to insert.
     * @return The ID of the newly inserted review.
     */
    public String insert(ReviewDTO reviewDTO) {
        Review review = ReviewMapper.fromReviewDTO(reviewDTO);
        ReviewValidator.validateReview(review);
        review = reviewRepository.save(review);
        LOGGER.debug(ReviewLogger.REVIEW_INSERTED, review.getId());

        Optional<User> userOptional = userRepository.findById(reviewDTO.getId_user());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Optional<Product> productOptional = productRepository.findById(reviewDTO.getId_product());
            String productName = productOptional.map(Product::getName).orElse("Unknown Product");

            NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
            notificationRequestDTO.setSubject("New Review Added!");
            notificationRequestDTO.setBody(buildEmailMessage(user, productName, reviewDTO));
            notificationRequestDTO.setEmail(user.getEmail()); // Set email based on user details
            rabbitSender.send(notificationRequestDTO);

            LOGGER.debug("Email sent successfully to: {}", user.getEmail());
        } else {
            // Handle case where user is not found
            LOGGER.error("User not found with ID: {}", reviewDTO.getId_user());
            // You may want to throw an exception or handle this case appropriately
        }

        return review.getId();
    }


    /**
     * Deletes a review from the database by its ID.
     *
     * @param id The ID of the review to delete.
     * @throws ResourceNotFoundException if the review with the specified ID is not found.
     */
    @Transactional
    public void deleteReviewById(String id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();

            User user = review.getUser();
            if (user != null) {
                user.getReviews().remove(review);
            }

            Product product = review.getProduct();
            if (product != null) {
                product.getReviews().remove(review);
            }

            reviewRepository.deleteById(id); // Delete the review
            LOGGER.debug(ReviewLogger.REVIEW_DELETED, id);
        } else {
            LOGGER.error(ReviewLogger.REVIEW_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Review.class.getSimpleName() + " with id: " + id);
        }
    }



    /**
     * Updates an existing review in the database.
     *
     * @param id        The ID of the review to update.
     * @param reviewDTO The updated ReviewDTO object representing the new state of the review.
     * @return The updated ReviewDTO object.
     * @throws ResourceNotFoundException if the review with the specified ID is not found.
     */
    public ReviewDTO updateReview(String id, ReviewDTO reviewDTO) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (!reviewOptional.isPresent()) {
            LOGGER.error(ReviewLogger.REVIEW_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Review.class.getSimpleName() + " with id: " + id);
        }
        Review existingReview = reviewOptional.get();
        existingReview.setRating(reviewDTO.getRating());
        existingReview.setComment(reviewDTO.getComment());
        Review updatedReview = reviewRepository.save(existingReview);
        LOGGER.debug(ReviewLogger.REVIEW_UPDATED, updatedReview.getId());
        return ReviewMapper.toReviewDTO(updatedReview);
    }
}
package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.ReviewLogger;
import com.example.carturestibackend.dtos.ReviewDTO;
import com.example.carturestibackend.services.ProductService;
import com.example.carturestibackend.services.ReviewService;
import com.example.carturestibackend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller class to handle HTTP requests related to reviews.
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/review")
public class ReviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;

    /**
     * Constructs a new ReviewController with the specified ReviewService.
     *
     * @param reviewService  The ReviewService used to handle review-related business logic.
     * @param userService
     * @param productService
     */
    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService, ProductService productService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.productService = productService;
    }

    /**
     * Retrieves all reviews.
     *
     * @return A ModelAndView containing a list of ReviewDTO objects representing the reviews.
     */
    @GetMapping()
    public ModelAndView getReviews() {
        LOGGER.info(ReviewLogger.ALL_REVIEWS_RETRIEVED);
        List<ReviewDTO> dtos = reviewService.findReviews();
        ModelAndView modelAndView = new ModelAndView("/review");
        modelAndView.addObject("reviews", dtos);
        return modelAndView;
    }

    /**
     * Inserts a new review.
     *
     * @param reviewDTO The ReviewDTO object representing the review to insert.
     * @return A ModelAndView containing the ID of the newly inserted review.
     */
    @PostMapping()
    public ModelAndView insert(@Valid @RequestBody ReviewDTO reviewDTO) {
        String reviewID = reviewService.insert(reviewDTO);
        LOGGER.debug(ReviewLogger.REVIEW_INSERTED, reviewID);
        ModelAndView modelAndView = new ModelAndView("/review");
        modelAndView.addObject("reviewID", reviewID);
        return modelAndView;
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param reviewID The ID of the review to retrieve.
     * @return A ModelAndView containing the ReviewDTO object representing the retrieved review.
     */
    @GetMapping(value = "/{id_review}")
    public ModelAndView getReview(@PathVariable("id_review") String reviewID) {
        LOGGER.info(ReviewLogger.REVIEW_RETRIEVED_BY_ID, reviewID);
        ReviewDTO dto = reviewService.findReviewById(reviewID);
        ModelAndView modelAndView = new ModelAndView("/review");
        modelAndView.addObject("review", dto);
        return modelAndView;
    }

    /**
     * Deletes a review by its ID.
     *
     * @param reviewID The ID of the review to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @DeleteMapping(value = "/{id_review}")
    public ModelAndView deleteReview(@PathVariable("id_review") String reviewID) {
        LOGGER.debug(ReviewLogger.REVIEW_DELETED, reviewID);
        reviewService.deleteReviewById(reviewID);
        ModelAndView modelAndView = new ModelAndView("/review");
        modelAndView.addObject("message", "Review with ID " + reviewID + " deleted successfully");
        return modelAndView;
    }

    /**
     * Updates a review by its ID.
     *
     * @param reviewID   The ID of the review to update.
     * @param reviewDTO  The updated ReviewDTO object representing the new state of the review.
     * @return A ModelAndView containing the updated ReviewDTO object.
     */
    @PutMapping(value = "/{id_review}")
    public ModelAndView updateReview(@PathVariable("id_review") String reviewID, @Valid @RequestBody ReviewDTO reviewDTO) {
        LOGGER.debug(ReviewLogger.REVIEW_UPDATED, reviewID);
        ReviewDTO updatedReview = reviewService.updateReview(reviewID, reviewDTO);
        ModelAndView modelAndView = new ModelAndView("/review");
        modelAndView.addObject("review", updatedReview);
        return modelAndView;
    }
}

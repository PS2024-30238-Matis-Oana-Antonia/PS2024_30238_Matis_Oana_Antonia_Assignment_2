package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.ReviewLogger;
import com.example.carturestibackend.constants.UserLogger;
import com.example.carturestibackend.dtos.ReviewDTO;
import com.example.carturestibackend.dtos.UserDTO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @PostMapping("/insertReview")
    public ModelAndView insert(@Valid @ModelAttribute ReviewDTO reviewDTO) {
        String reviewID = reviewService.insert(reviewDTO);
        LOGGER.debug(ReviewLogger.REVIEW_INSERTED, reviewID);
        ModelAndView modelAndView = new ModelAndView("/review");
        modelAndView.addObject("reviewID", reviewID);
        return new ModelAndView("redirect:/review");
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
     * @param ID The ID of the review to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @PostMapping(value = "/deleteReview")
    public ModelAndView deleteReview(@RequestParam("id") String ID, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/review");
        try {
            reviewService.deleteReviewById(ID);
            LOGGER.debug(ReviewLogger.REVIEW_DELETED, ID);
            redirectAttributes.addFlashAttribute("successMessage", "Review with ID " + ID + " deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete review with ID " + ID + ". Please try again.");
        }
        return mav;
    }

    /**
     * Updates a review by its ID.
     *
     * @param id   The ID of the review to update.
     * @param reviewDTO  The updated ReviewDTO object representing the new state of the review.
     * @return A ModelAndView containing the updated ReviewDTO object.
     */
    @PostMapping("/reviewUpdate")
    public ModelAndView updateReview(@RequestParam("id") String id, @Valid @ModelAttribute ReviewDTO reviewDTO) {
        ModelAndView mav = new ModelAndView("redirect:/review");
        try {
            ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
            // You can optionally add a success message to be displayed on the redirected page
            mav.addObject("successMessage", "Review updated successfully!");
        } catch (Exception e) {
            // If an error occurs during the update, you can add an error message to be displayed on the redirected page
            mav.addObject("errorMessage", "Failed to update review. Please try again.");
        }
        return mav;
    }
}

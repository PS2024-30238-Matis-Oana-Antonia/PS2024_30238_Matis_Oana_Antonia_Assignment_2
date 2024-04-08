package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.PromotionLogger;
import com.example.carturestibackend.dtos.PromotionDTO;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.PromotionService;
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
 * Controller class to handle HTTP requests related to promotions.
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/promotion")
public class PromotionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionController.class);

    private final PromotionService promotionService;

    /**
     * Constructs a new PromotionController with the specified PromotionService.
     *
     * @param promotionService The PromotionService used to handle promotion-related business logic.
     */
    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Retrieves all promotions.
     *
     * @return A ModelAndView containing a list of PromotionDTO objects representing the promotions.
     */
    @GetMapping()
    public ModelAndView getPromotions() {
        LOGGER.info(PromotionLogger.ALL_PROMOTIONS_RETRIEVED);
        List<PromotionDTO> dtos = promotionService.findPromotions();
        ModelAndView modelAndView = new ModelAndView("/promotion");
        modelAndView.addObject("promotions", dtos);
        return modelAndView;
    }

    /**
     * Inserts a new promotion.
     *
     * @param promotionDTO The PromotionDTO object representing the promotion to insert.
     * @return A ModelAndView containing the ID of the newly inserted promotion.
     */
    @PostMapping("/insert")
    public ModelAndView insert(@Valid @ModelAttribute PromotionDTO promotionDTO) {
        String promotionID = promotionService.insert(promotionDTO);
        LOGGER.debug(PromotionLogger.PROMOTION_INSERTED, promotionID);
        ModelAndView modelAndView = new ModelAndView("/promotion");
        modelAndView.addObject("promotionID", promotionID);
        return new ModelAndView("redirect:/promotion");
    }

    /**
     * Retrieves a promotion by its ID.
     *
     * @param promotionID The ID of the promotion to retrieve.
     * @return A ModelAndView containing the PromotionDTO object representing the retrieved promotion.
     */
    @GetMapping(value = "/{id_promotion}")
    public ModelAndView getPromotion(@PathVariable("id_promotion") String promotionID) {
        LOGGER.info(PromotionLogger.PROMOTION_RETRIEVED_BY_ID, promotionID);
        PromotionDTO dto = promotionService.findPromotionById(promotionID);
        ModelAndView modelAndView = new ModelAndView("/promotion");
        modelAndView.addObject("promotion", dto);
        return modelAndView;
    }

    /**
     * Deletes a promotion by its ID.
     *
     * @param promotionID The ID of the promotion to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @PostMapping(value = "/delete")
    public ModelAndView deletePromotion(@RequestParam("id_promotion") String promotionID, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/promotion"); // Redirecting back to the promotion page
        try {
            promotionService.deletePromotionById(promotionID);
            LOGGER.debug(PromotionLogger.PROMOTION_DELETED, promotionID);
            redirectAttributes.addFlashAttribute("successMessage", "Promotion with ID " + promotionID + " deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete promotion with ID " + promotionID + ". Please try again.");
        }
        return mav;
    }


    /**
     * Updates a promotion by its ID.
     *
     * @param id_promotion    The ID of the promotion to update.
     * @param promotionDTO   The updated PromotionDTO object representing the new state of the promotion.
     * @return A ModelAndView containing the updated PromotionDTO object.
     */
    @PostMapping("/promotionUpdate")
    public ModelAndView updatePromotion(@RequestParam("id_promotion") String id_promotion, @Valid @ModelAttribute PromotionDTO promotionDTO) {
        ModelAndView mav = new ModelAndView("redirect:/promotion"); // Redirecting back to the promotion page
        try {
            PromotionDTO updatedPromotion = promotionService.updatePromotion(id_promotion, promotionDTO);
            // You can optionally add a success message to be displayed on the redirected page
            mav.addObject("successMessage", "Promotion updated successfully!");
        } catch (Exception e) {
            // If an error occurs during the update, you can add an error message to be displayed on the redirected page
            mav.addObject("errorMessage", "Failed to update promotion. Please try again.");
        }
        return mav;
    }

}

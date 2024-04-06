package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.PromotionLogger;
import com.example.carturestibackend.dtos.PromotionDTO;
import com.example.carturestibackend.services.PromotionService;
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
    @PostMapping()
    public ModelAndView insert(@Valid @RequestBody PromotionDTO promotionDTO) {
        String promotionID = promotionService.insert(promotionDTO);
        LOGGER.debug(PromotionLogger.PROMOTION_INSERTED, promotionID);
        ModelAndView modelAndView = new ModelAndView("/promotion");
        modelAndView.addObject("promotionID", promotionID);
        return modelAndView;
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
    @DeleteMapping(value = "/{id_promotion}")
    public ModelAndView deletePromotion(@PathVariable("id_promotion") String promotionID) {
        LOGGER.debug(PromotionLogger.PROMOTION_DELETED, promotionID);
        promotionService.deletePromotionById(promotionID);
        ModelAndView modelAndView = new ModelAndView("/promotion");
        modelAndView.addObject("message", "Promotion with ID " + promotionID + " deleted successfully");
        return modelAndView;
    }

    /**
     * Updates a promotion by its ID.
     *
     * @param promotionID    The ID of the promotion to update.
     * @param promotionDTO   The updated PromotionDTO object representing the new state of the promotion.
     * @return A ModelAndView containing the updated PromotionDTO object.
     */
    @PutMapping(value = "/{id_promotion}")
    public ModelAndView updatePromotion(@PathVariable("id_promotion") String promotionID, @Valid @RequestBody PromotionDTO promotionDTO) {
        LOGGER.debug(PromotionLogger.PROMOTION_UPDATED, promotionID);
        PromotionDTO updatedPromotion = promotionService.updatePromotion(promotionID, promotionDTO);
        ModelAndView modelAndView = new ModelAndView("/promotion");
        modelAndView.addObject("promotion", updatedPromotion);
        return modelAndView;
    }
}

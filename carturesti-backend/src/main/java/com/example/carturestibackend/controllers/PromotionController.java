package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.PromotionLogger;
import com.example.carturestibackend.dtos.PromotionDTO;
import com.example.carturestibackend.services.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller class to handle HTTP requests related to promotions.
 */
@RestController
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
     * @return A ResponseEntity containing a list of PromotionDTO objects representing the promotions.
     */
    @GetMapping()
    public ResponseEntity<List<PromotionDTO>> getPromotions() {
        LOGGER.info(PromotionLogger.ALL_PROMOTIONS_RETRIEVED);
        List<PromotionDTO> dtos = promotionService.findPromotions();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Inserts a new promotion.
     *
     * @param promotionDTO The PromotionDTO object representing the promotion to insert.
     * @return A ResponseEntity containing the ID of the newly inserted promotion.
     */
    @PostMapping()
    public ResponseEntity<String> insert(@Valid @RequestBody PromotionDTO promotionDTO) {
        String promotionID = promotionService.insert(promotionDTO);
        LOGGER.debug(PromotionLogger.PROMOTION_INSERTED, promotionID);
        return new ResponseEntity<>(promotionID, HttpStatus.CREATED);
    }

    /**
     * Retrieves a promotion by its ID.
     *
     * @param promotionID The ID of the promotion to retrieve.
     * @return A ResponseEntity containing the PromotionDTO object representing the retrieved promotion.
     */
    @GetMapping(value = "/{id_promotion}")
    public ResponseEntity<PromotionDTO> getPromotion(@PathVariable("id_promotion") String promotionID) {
        LOGGER.info(PromotionLogger.PROMOTION_RETRIEVED_BY_ID, promotionID);
        PromotionDTO dto = promotionService.findPromotionById(promotionID);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Deletes a promotion by its ID.
     *
     * @param promotionID The ID of the promotion to delete.
     * @return A ResponseEntity indicating the success of the operation.
     */
    @DeleteMapping(value = "/{id_promotion}")
    public ResponseEntity<String> deletePromotion(@PathVariable("id_promotion") String promotionID) {
        LOGGER.debug(PromotionLogger.PROMOTION_DELETED, promotionID);
        promotionService.deletePromotionById(promotionID);
        return new ResponseEntity<>("Promotion with ID " + promotionID + " deleted successfully", HttpStatus.OK);
    }

    /**
     * Updates a promotion by its ID.
     *
     * @param promotionID    The ID of the promotion to update.
     * @param promotionDTO   The updated PromotionDTO object representing the new state of the promotion.
     * @return A ResponseEntity containing the updated PromotionDTO object.
     */
    @PutMapping(value = "/{id_promotion}")
    public ResponseEntity<PromotionDTO> updatePromotion(@PathVariable("id_promotion") String promotionID, @Valid @RequestBody PromotionDTO promotionDTO) {
        LOGGER.debug(PromotionLogger.PROMOTION_UPDATED, promotionID);
        PromotionDTO updatedPromotion = promotionService.updatePromotion(promotionID, promotionDTO);
        return new ResponseEntity<>(updatedPromotion, HttpStatus.OK);
    }
}

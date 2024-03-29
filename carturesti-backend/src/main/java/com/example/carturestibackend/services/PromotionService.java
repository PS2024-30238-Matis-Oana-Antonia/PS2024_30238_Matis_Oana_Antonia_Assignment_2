package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.PromotionLogger;
import com.example.carturestibackend.dtos.PromotionDTO;
import com.example.carturestibackend.dtos.mappers.PromotionMapper;
import com.example.carturestibackend.entities.Promotion;
import com.example.carturestibackend.repositories.PromotionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to promotions.
 */
@Service
public class PromotionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionService.class);
    private final PromotionRepository promotionRepository;

    /**
     * Constructs a new PromotionService with the specified PromotionRepository.
     *
     * @param promotionRepository The PromotionRepository used to interact with promotion data in the database.
     */
    @Autowired
    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    /**
     * Retrieves all promotions from the database.
     *
     * @return A list of PromotionDTO objects representing the promotions.
     */
    public List<PromotionDTO> findPromotions() {
        LOGGER.error(PromotionLogger.ALL_PROMOTIONS_RETRIEVED);
        List<Promotion> promotionList = promotionRepository.findAll();
        return promotionList.stream()
                .map(PromotionMapper::toPromotionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a promotion by its ID.
     *
     * @param id The ID of the promotion to retrieve.
     * @return The PromotionDTO object representing the retrieved promotion.
     * @throws ResourceNotFoundException if the promotion with the specified ID is not found.
     */
    public PromotionDTO findPromotionById(String id) {
        Optional<Promotion> promotionOptional = promotionRepository.findById(id);
        if (!promotionOptional.isPresent()) {
            LOGGER.error(PromotionLogger.PROMOTION_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Promotion.class.getSimpleName() + " with id: " + id);
        }
        return PromotionMapper.toPromotionDTO(promotionOptional.get());
    }

    /**
     * Inserts a new promotion into the database.
     *
     * @param promotionDTO The PromotionDTO object representing the promotion to insert.
     * @return The ID of the newly inserted promotion.
     */
    public String insert(PromotionDTO promotionDTO) {
        Promotion promotion = PromotionMapper.fromPromotionDTO(promotionDTO);
        promotion = (Promotion) promotionRepository.save(promotion);
        LOGGER.debug(PromotionLogger.PROMOTION_INSERTED, promotion.getId_promotion());
        return promotion.getId_promotion();
    }

    /**
     * Deletes a promotion from the database by its ID.
     *
     * @param id The ID of the promotion to delete.
     * @throws ResourceNotFoundException if the promotion with the specified ID is not found.
     */
    public void deletePromotionById(String id) {
        Optional<Promotion> promotionOptional = promotionRepository.findById(id);
        if (promotionOptional.isPresent()) {
            promotionRepository.delete(promotionOptional.get());
            LOGGER.debug(PromotionLogger.PROMOTION_DELETED, id);
        } else {
            LOGGER.error(PromotionLogger.PROMOTION_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Promotion.class.getSimpleName() + " with id: " + id);
        }
    }

    /**
     * Updates an existing promotion in the database.
     *
     * @param id          The ID of the promotion to update.
     * @param promotionDTO The updated PromotionDTO object representing the new state of the promotion.
     * @return The updated PromotionDTO object.
     * @throws ResourceNotFoundException if the promotion with the specified ID is not found.
     */
    public PromotionDTO updatePromotion(String id, PromotionDTO promotionDTO) {
        Optional<Promotion> promotionOptional = promotionRepository.findById(id);
        if (!promotionOptional.isPresent()) {
            LOGGER.error(PromotionLogger.PROMOTION_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Promotion.class.getSimpleName() + " with id: " + id);
        }

        Promotion existingPromotion = promotionOptional.get();
        existingPromotion.setName(promotionDTO.getName());
        existingPromotion.setDescription(promotionDTO.getDescription());
        existingPromotion.setPercentage(promotionDTO.getPercentage());

        Promotion updatedPromotion = (Promotion) promotionRepository.save(existingPromotion);
        LOGGER.debug(PromotionLogger.PROMOTION_UPDATED, updatedPromotion.getId_promotion());

        return PromotionMapper.toPromotionDTO(updatedPromotion);
    }
}

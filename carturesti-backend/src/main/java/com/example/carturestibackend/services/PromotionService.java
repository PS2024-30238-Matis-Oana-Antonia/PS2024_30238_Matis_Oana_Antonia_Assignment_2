package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.PromotionLogger;
import com.example.carturestibackend.dtos.PromotionDTO;
import com.example.carturestibackend.dtos.mappers.PromotionMapper;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Promotion;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.repositories.PromotionRepository;
import com.example.carturestibackend.validators.PromotionValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to promotions.
 */
@Service
@Transactional
public class PromotionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionService.class);
    private final PromotionRepository promotionRepository;
    private final PromotionValidator promotionValidator;
    private final ProductRepository productRepository;

    /**
     * Constructs a new PromotionService with the specified PromotionRepository.
     *
     * @param promotionRepository The PromotionRepository used to interact with promotion data in the database.
     * @param promotionValidator
     * @param productRepository
     */
    @Autowired
    public PromotionService(PromotionRepository promotionRepository, PromotionValidator promotionValidator, ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.promotionValidator = promotionValidator;
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all promotions from the database.
     *
     * @return A list of PromotionDTO objects representing the promotions.
     */
    public List<PromotionDTO> findPromotions() {
        LOGGER.info(PromotionLogger.ALL_PROMOTIONS_RETRIEVED);
        List<Promotion> promotions = promotionRepository.findAll();
        List<PromotionDTO> promotionDTOs = new ArrayList<>();

        for (Promotion promotion : promotions) {
            PromotionDTO promotionDTO = PromotionMapper.toPromotionDTO(promotion);
            List<String> productIdsWithPromotion = new ArrayList<>();
            double totalPromotionPrice = 0.0; // Initialize total promotion price

            for (Product product : promotion.getProducts()) {
                String productId = product.getId_product();
                double promotionPercentage = promotion.getPercentage();
                double originalPrice = product.getPrice();
                double promotionPrice = originalPrice;

                if (promotionPercentage > 0) {
                    promotionPrice = originalPrice * (1 - promotionPercentage / 100);
                }

                totalPromotionPrice += promotionPrice; // Add promotion price to total promotion price

                // Update the product price in the database
                product.setPrice_promotion(promotionPrice);
                productRepository.save(product); // Save the updated product

                productIdsWithPromotion.add(productId);
            }

            // Set the list of product IDs with promotion prices in the PromotionDTO
            promotionDTO.setId_products(productIdsWithPromotion);
            promotionDTOs.add(promotionDTO);
        }

        return promotionDTOs;
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

    @Transactional
    public String insert(PromotionDTO promotionDTO) {
        // Convertim DTO-ul în entitate
        Promotion promotion = PromotionMapper.fromPromotionDTO(promotionDTO);

        // Validăm promoția
        boolean isValidPromotion = promotionValidator.validatePromotion(promotion);
        if (isValidPromotion) {
            // Salvăm promoția
            promotion = promotionRepository.save(promotion);
            LOGGER.debug(PromotionLogger.PROMOTION_INSERTED, promotion.getId_promotion());

            // Verificăm dacă lista de ID-uri de produse nu este null
            List<String> productIds = promotionDTO.getId_products();
            if (productIds != null && !productIds.isEmpty()) {
                // Iterăm prin fiecare ID de produs și îl asociem cu promoția
                for (String productId : productIds) {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

                    // Aplicăm procentul promoției la prețul produsului
                    double promotionPercentage = promotion.getPercentage();
                    double originalPrice = product.getPrice();
                    double discountedPrice = originalPrice;

                    if (promotionPercentage > 0) {
                        discountedPrice = originalPrice * (1 - promotionPercentage / 100);
                    }

                    // Actualizăm prețul produsului cu prețul redus
                    product.setPrice(discountedPrice);

                    // Asociem produsul cu promoția
                    product.setPromotion(promotion);

                    // Salvăm produsul cu asocierea și prețul actualizat
                    productRepository.save(product);
                }
            }

            return promotion.getId_promotion();
        } else {
            // Logăm erorile de validare și aruncăm o excepție cu mesajele de eroare
            LOGGER.error(PromotionLogger.PROMOTION_INSERT_FAILED_INVALID_DATA);
            throw new IllegalArgumentException("Invalid promotion data");
        }
    }


    /**
     * Deletes a promotion from the database by its ID.
     *
     * @param id The ID of the promotion to delete.
     * @throws ResourceNotFoundException if the promotion with the specified ID is not found.
     */
    /**
     * Deletes a promotion from the database by its ID.
     *
     * @param id The ID of the promotion to delete.
     * @throws ResourceNotFoundException if the promotion with the specified ID is not found.
     */
    @Transactional
    public void deletePromotionById(String id) {
        Optional<Promotion> promotionOptional = promotionRepository.findById(id);
        if (promotionOptional.isPresent()) {
            Promotion promotion = promotionOptional.get();

            List<Product> products = promotion.getProducts();
            for (Product product : products) {
                // Calculate the original price using the formula
                double promotionPercentage = promotion.getPercentage();
                double discountedPrice = product.getPrice();
                double originalPrice = discountedPrice / (1 - promotionPercentage / 100);

                // Set the product's price to the original price
                product.setPromotion(null);
                product.setPrice(originalPrice);
                product.setPrice_promotion(0.0);
                productRepository.save(product);
            }
            promotionRepository.delete(promotion);
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
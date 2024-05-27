package com.example.carturestibackend.services;

import com.example.carturestibackend.dtos.PromotionDTO;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Promotion;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.repositories.PromotionRepository;
import com.example.carturestibackend.validators.PromotionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionValidator promotionValidator;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PromotionService promotionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindPromotionById_ValidId() {
        String id = "1";
        Promotion promotion = new Promotion();
        promotion.setId_promotion(id);

        when(promotionRepository.findById(id)).thenReturn(Optional.of(promotion));

        PromotionDTO promotionDTO = promotionService.findPromotionById(id);

        assertNotNull(promotionDTO);
        assertEquals(id, promotionDTO.getId_promotion());
    }

    @Test
    public void testFindPromotionById_InvalidId() {
        String id = "100";

        when(promotionRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> promotionService.findPromotionById(id));
    }

    @Test
    public void testInsert_ValidPromotion() {
        PromotionDTO promotionDTO = new PromotionDTO();
        promotionDTO.setName("Test Promotion");

        Promotion promotion = new Promotion();
        promotion.setName("Test Promotion");

        when(promotionValidator.validatePromotion(any())).thenReturn(true);
        when(promotionRepository.save(any())).thenReturn(promotion);

        String id = promotionService.insert(promotionDTO);
        assertNotNull(id); // Verificăm că ID-ul promoției returnat nu este null

        assertEquals(promotion.getName(), promotionDTO.getName());
    }

    @Test
    public void testInsert_InvalidPromotion() {
        PromotionDTO promotionDTO = new PromotionDTO();
        promotionDTO.setName("Invalid Promotion");

        when(promotionValidator.validatePromotion(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> promotionService.insert(promotionDTO));
    }

    @Test
    public void testDeletePromotionById_ValidId() {
        String id = "1";
        Promotion promotion = new Promotion();
        promotion.setId_promotion(id);

        Product product = new Product();
        product.setPromotion(promotion);

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        promotion.setProducts(productList);

        when(promotionRepository.findById(id)).thenReturn(Optional.of(promotion));

        promotionService.deletePromotionById(id);

        verify(productRepository, times(1)).save(any());
        verify(promotionRepository, times(1)).delete(promotion);
    }

    @Test
    public void testDeletePromotionById_InvalidId() {
        String id = "100";

        when(promotionRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> promotionService.deletePromotionById(id));
    }
}

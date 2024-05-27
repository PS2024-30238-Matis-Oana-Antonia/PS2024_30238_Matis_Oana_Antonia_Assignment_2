package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.ProductLogger;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.dtos.mappers.*;
import com.example.carturestibackend.entities.*;
import com.example.carturestibackend.repositories.*;
import com.example.carturestibackend.validators.ProductValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Base64;

/**
 * Service class to handle business logic related to products.
 */
@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductValidator productValidator;
    private final PromotionRepository promotionRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository ;

    /**
     * Constructs a new ProductService with the specified ProductRepository.
     *
     * @param productRepository   The ProductRepository used to interact with product data in the database.
     * @param categoryRepository
     * @param productValidator
     * @param promotionRepository
     * @param reviewRepository
     * @param orderItemRepository
     */
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductValidator productValidator, PromotionRepository promotionRepository, ReviewRepository reviewRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productValidator = productValidator;
        this.promotionRepository = promotionRepository;
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of ProductDTO objects representing the products.
     */
    public List<ProductDTO> findProducts() {
        LOGGER.info(ProductLogger.ALL_PRODUCTS_RETRIEVED);
        List<Product> productList = productRepository.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();

        for (Product product : productList) {
            ProductDTO productDTO = ProductMapper.toProductDTO(product);

            double originalPrice = product.getPrice();

            Promotion promotion = product.getPromotion();
            if (promotion != null) {
                double promotionPercentage = promotion.getPercentage();
                double promotionPrice = originalPrice;

                if (promotionPercentage > 0) {
                    promotionPrice = originalPrice * (1 - promotionPercentage / 100);
                }

                productDTO.setPrice_promotion(promotionPrice); // Set promotion price in DTO
            }

            productDTOs.add(productDTO);
        }

        return productDTOs;
    }


    /**
     * Retrieves a product by its ID.
     *
     * @param id_product The ID of the product to retrieve.
     * @return The ProductDTO object representing the retrieved product.
     * @throws ResourceNotFoundException if the product with the specified ID is not found.
     */
    public ProductDTO findProductById(String id_product) {
        Optional<Product> productOptional = productRepository.findById(id_product);
        if (!productOptional.isPresent()) {
            LOGGER.error(ProductLogger.PRODUCT_NOT_FOUND_BY_ID, id_product);
            throw new ResourceNotFoundException(Product.class.getSimpleName() + " with id: " + id_product);
        }
        return ProductMapper.toProductDTO(productOptional.get());
    }
    /**
     * Retrieves products from the database by category name.
     *
     * @param categoryName The name of the category to filter products.
     * @return A list of Product objects belonging to the specified category.
     */
    public List<Product> getProductsByCategoryName(String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category != null) {
            return productRepository.findProductByCategory(category);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Searches products by keyword.
     *
     * @param keyword The keyword to search for in product names and descriptions.
     * @return A list of Product objects matching the search keyword.
     */
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }
    /**
     * Retrieves products from the database sorted by price.
     *
     * @param ascending True for ascending order, false for descending order.
     * @return A list of Product objects sorted by price.
     */
    public List<Product> getProductsSortedByPrice(boolean ascending) {
        return ascending ? productRepository.findAllByOrderByPriceAsc() : productRepository.findAllByOrderByPriceDesc();
    }

    /**
     * Retrieves products from the database sorted by name.
     *
     * @param ascending True for ascending order, false for descending order.
     * @return A list of Product objects sorted by name.
     */
    public List<Product> getProductsSortedByName(boolean ascending) {
        return ascending ? productRepository.findAllByOrderByNameAsc() : productRepository.findAllByOrderByNameDesc();
    }


    /**
     * Inserts a new product into the database.
     *
     * @param productDTO The ProductDTO object representing the product to insert.
     * @return The ID of the newly inserted product.
     */

    @Transactional
    public String insert(ProductDTO productDTO) {
        Product product = ProductMapper.fromProductDTO(productDTO);

        if (productDTO.getId_promotion() == null || productDTO.getId_promotion().isEmpty()) {
            product.setPromotion(null);
        }

        if (productDTO.getId_reviews() == null || productDTO.getId_reviews().isEmpty()) {
            product.setReviews(null);
        }

        ProductValidator.validateProduct(product);

        // Save the image file to the server and set the image path in the product
        product = productRepository.save(product);
        LOGGER.debug(ProductLogger.PRODUCT_INSERTED, product.getId_product());

        return product.getId_product();
    }

    /**
     * Deletes a product from the database by its ID.
     *
     * @param id_product The ID of the product to delete.
     * @throws ResourceNotFoundException if the product with the specified ID is not found.
     */

    @Transactional
    public void deleteProductById(String id_product) {
        Optional<Product> productOptional = productRepository.findById(id_product);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Remove the product from its category
            Category category = product.getCategory();
            if (category != null) {
                category.getProducts().remove(product);
                categoryRepository.save(category);
            }

            // Handle reviews associated with the product
            List<Review> reviews = product.getReviews();
            if (reviews != null && !reviews.isEmpty()) {
                for (Review review : reviews) {
                    review.setUser(null);
                    review.setProduct(null);
                    reviewRepository.delete(review);
                }
            }

            // Clear the reviews list in the product
            product.setReviews(new ArrayList<>());

            // Delete the product itself
            productRepository.deleteById(id_product);
            LOGGER.debug(ProductLogger.PRODUCT_DELETED, id_product);
        } else {
            LOGGER.error(ProductLogger.PRODUCT_NOT_FOUND_BY_ID, id_product);
            throw new ResourceNotFoundException(Product.class.getSimpleName() + " with id: " + id_product);
        }
    }

    /**
     * Updates an existing product in the database.
     *
     * @param id_product The ID of the product to update.
     * @param productDTO The updated ProductDTO object representing the new state of the product.
     * @return The updated ProductDTO object.
     * @throws ResourceNotFoundException if the product with the specified ID is not found.
     */
    @Transactional
    public ProductDTO updateProduct(String id_product, ProductDTO productDTO) {
        Optional<Product> productOptional = productRepository.findById(id_product);
        if (!productOptional.isPresent()) {
            LOGGER.error(ProductLogger.PRODUCT_NOT_FOUND_BY_ID, id_product);
            throw new ResourceNotFoundException(Product.class.getSimpleName() + " with id: " + id_product);
        }

        Product existingProduct = productOptional.get();
        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setAuthor(productDTO.getAuthor());
        existingProduct.setStock(productDTO.getStock());

        if (productDTO.getId_promotion() != null) {
            Promotion promotion = promotionRepository.findById(productDTO.getId_promotion())
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with ID: " + productDTO.getId_promotion()));
            existingProduct.setPromotion(promotion);
        } else {
            existingProduct.setPromotion(null);
        }



        Product updatedProduct = productRepository.save(existingProduct);
        LOGGER.debug(ProductLogger.PRODUCT_UPDATED, updatedProduct.getId_product());

        return ProductMapper.toProductDTO(updatedProduct);
    }


    /**
     * Adds a review to a product.
     *
     * @param productId The ID of the product.
     * @param review    The review to add.
     */
    public void addReviewToProduct(String productId, Review review) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        review.setProduct(product);

        reviewRepository.save(review);
    }

    /**
     * Retrieves products from the database by name.
     *
     * @param name The name of the product to retrieve.
     * @return A list of ProductDTO objects representing the products with matching names.
     */



}
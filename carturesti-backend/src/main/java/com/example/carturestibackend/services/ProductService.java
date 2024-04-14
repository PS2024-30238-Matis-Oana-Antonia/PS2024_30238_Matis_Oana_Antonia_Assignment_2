package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.ProductLogger;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.dtos.mappers.*;
import com.example.carturestibackend.entities.*;
import com.example.carturestibackend.repositories.CategoryRepository;
import com.example.carturestibackend.repositories.OrderItemRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.repositories.ReviewRepository;
import com.example.carturestibackend.validators.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to products.
 */
@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductValidator productValidator;
    private SaleService saleService;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository ;
    /**
     * Constructs a new ProductService with the specified ProductRepository.
     *
     * @param productRepository  The ProductRepository used to interact with product data in the database.
     * @param categoryRepository
     * @param productValidator
     * @param saleService
     * @param reviewRepository
     * @param orderItemRepository
     */
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductValidator productValidator, SaleService saleService, ReviewRepository reviewRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productValidator = productValidator;
        this.saleService = saleService;
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of ProductDTO objects representing the products.
     */
    public List<ProductDTO> findProducts() {
        LOGGER.error(ProductLogger.ALL_PRODUCTS_RETRIEVED);
        List<Product> productList = productRepository.findAll();
        return productList.stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
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
     * Inserts a new product into the database.
     *
     * @param productDTO The ProductDTO object representing the product to insert.
     * @return The ID of the newly inserted product.
     */
    public String insert(ProductDTO productDTO) {
        // Create a new product entity from the DTO
        Product product = ProductMapper.fromProductDTO(productDTO);

        // Set promotion, sale, and reviews to null if their IDs are not provided
        if (productDTO.getId_promotion() == null || productDTO.getId_promotion().isEmpty()) {
            product.setPromotion(null);
        }
        if (productDTO.getId_sale() == null || productDTO.getId_sale().isEmpty()) {
            product.setSale(null);
        }
        if (productDTO.getId_reviews() == null || productDTO.getId_reviews().isEmpty()) {
            product.setReviews(null);
        }

        // Validate and save the product entity
        ProductValidator.validateProduct(product);
        product = productRepository.save(product);
        LOGGER.debug(ProductLogger.PRODUCT_INSERTED, product.getId_product());


        // Create a list containing the product and associate it with the order item
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        // Return the ID of the inserted product
        return product.getId_product();
    }


    /**
     * Deletes a product from the database by its ID.
     *
     * @param id_product The ID of the product to delete.
     * @throws ResourceNotFoundException if the product with the specified ID is not found.
     */
    public void deleteProductById(String id_product) {
        Optional<Product> productOptional = productRepository.findById(id_product);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Get the category associated with the product
            Category category = product.getCategory();

            if(product.getCategory()!=null) {
                product.setCategory(null);
            }
            // Disassociate the product from its other associations
            product.setPromotion(null);
            product.setSale(null);
            product.setReviews(null);
            product.setOrderItems(null);

            // Save the product without its associations
            productRepository.save(product);
            // Delete the product
            productRepository.delete(product);

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

        Product updatedProduct = productRepository.save(existingProduct);
        LOGGER.debug(ProductLogger.PRODUCT_UPDATED, updatedProduct.getId_product());

        return ProductMapper.toProductDTO(updatedProduct);
    }

    public void decreaseProductStock(String productId, long quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            long newStock = product.getStock() - quantity;
            if (newStock >= 0) {
                product.setStock(newStock);
                productRepository.save(product);
                LOGGER.info(ProductLogger.STOCK_DECREASED, product.getName(), quantity, newStock);
            } else {
                LOGGER.error(ProductLogger.INSUFFICIENT_STOCK_TO_DECREASE, product.getName(), quantity, product.getStock());
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
        } else {
            LOGGER.error(ProductLogger.PRODUCT_NOT_FOUND_BY_ID, productId);
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
    }

    public void increaseProductStock(String productId, long quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            long newStock = product.getStock() + quantity;
            product.setStock(newStock);
            productRepository.save(product);
            LOGGER.info(ProductLogger.STOCK_INCREASED, product.getName(), quantity, newStock);
        } else {
            LOGGER.error(ProductLogger.PRODUCT_NOT_FOUND_BY_ID, productId);
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
    }



    public void addReviewToProduct(String productId, Review review) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        review.setProduct(product);

        reviewRepository.save(review);
    }



}

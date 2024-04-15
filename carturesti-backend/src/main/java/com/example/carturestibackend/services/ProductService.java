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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private final PromotionRepository promotionRepository;
    private final SaleRepository saleRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository ;
    /**
     * Constructs a new ProductService with the specified ProductRepository.
     *
     * @param productRepository   The ProductRepository used to interact with product data in the database.
     * @param categoryRepository
     * @param productValidator
     * @param promotionRepository
     * @param saleRepository
     * @param reviewRepository
     * @param orderItemRepository
     */
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductValidator productValidator, PromotionRepository promotionRepository, SaleRepository saleRepository, ReviewRepository reviewRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productValidator = productValidator;
        this.promotionRepository = promotionRepository;
        this.saleRepository = saleRepository;
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

            Sale sale = product.getSale();
            if(sale != null){
                double discountPercentage = sale.getDiscount_percentage();
                double discountPrice = originalPrice;
                if (discountPercentage > 0) {
                    discountPrice = originalPrice * (1 - discountPercentage / 100);
                }

                productDTO.setPrice_discount(discountPrice);

            }
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


    public List<ProductDTO> findProductsByName(String name) {

        List<Product> products = productRepository.findByName(name);

        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            ProductDTO dto = new ProductDTO();
            dto.setId_product(product.getId_product());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setId_category(product.getCategory() != null ? product.getCategory().getId_category() : null);
            dto.setAuthor(product.getAuthor());
            dto.setPrice(product.getPrice());
            dto.setPrice_discount(product.getPrice_discount());
            dto.setPrice_promotion(product.getPrice_promotion());
            dto.setId_promotion(product.getPromotion() != null ? product.getPromotion().getId_promotion() : null);
            dto.setId_sale(product.getSale() != null ? product.getSale().getId_sale() : null);
            List<String> reviewIds = new ArrayList<>();
            if (product.getReviews() != null) {
                for (Review review : product.getReviews()) {
                    reviewIds.add(review.getId());
                }
            }
            dto.setId_reviews(reviewIds);

            dtos.add(dto);
        }
        return dtos;
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
        if (productDTO.getId_promotion() == null || productDTO.getId_promotion().isEmpty()) {
            product.setPromotion(null);
        }
        if (productDTO.getId_sale() == null || productDTO.getId_sale().isEmpty()) {
            product.setSale(null);
        }
        if (productDTO.getId_reviews() == null || productDTO.getId_reviews().isEmpty()) {
            product.setReviews(null);
        }

        ProductValidator.validateProduct(product);
        product = productRepository.save(product);
        LOGGER.debug(ProductLogger.PRODUCT_INSERTED, product.getId_product());

        List<Product> productList = new ArrayList<>();
        productList.add(product);

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
            Category category = product.getCategory();

            if (category != null) {

                category.getProducts().remove(product);
                categoryRepository.save(category);
            }

            product.setPromotion(null);
            product.setSale(null);
            product.setReviews(null);
            productRepository.save(product);
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

        if (productDTO.getId_sale() != null) {
            Sale sale = saleRepository.findById(productDTO.getId_sale())
                    .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + productDTO.getId_sale()));
            existingProduct.setSale(sale);
        } else {
            existingProduct.setSale(null);
        }

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

    /**
     * Retrieves products from the database by name.
     *
     * @param name The name of the product to retrieve.
     * @return A list of ProductDTO objects representing the products with matching names.
     */



}

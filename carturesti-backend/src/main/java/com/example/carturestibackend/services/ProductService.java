package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.ProductLogger;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.dtos.mappers.ProductMapper;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.repositories.CategoryRepository;
import com.example.carturestibackend.repositories.ProductRepository;
import com.example.carturestibackend.validators.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

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

    /**
     * Constructs a new ProductService with the specified ProductRepository.
     *
     * @param productRepository  The ProductRepository used to interact with product data in the database.
     * @param categoryRepository
     * @param productValidator
     */
    @Autowired
    public ProductService(ProductRepository productRepository, SaleService saleService, CategoryRepository categoryRepository, ProductValidator productValidator) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productValidator = productValidator;
        this.saleService =  saleService;
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
        Product product = ProductMapper.fromProductDTO(productDTO);
        ProductValidator.validateProduct(product);
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
    public void deleteProductById(String id_product) {
        Optional<Product> productOptional = productRepository.findById(id_product);
        if (productOptional.isPresent()) {
            productRepository.delete(productOptional.get());
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

    public void applyDiscountToProduct(String productId, double discountPercentage) {
        ProductDTO productDTO = findProductById(productId);
        Product product = ProductMapper.fromProductDTO(productDTO);

        // Apply the discount to the product
        saleService.applyDiscount(product, discountPercentage);

        // Update the product in the database
        updateProduct(productId, ProductMapper.toProductDTO(product));

        LOGGER.debug("Discount of {}% applied to product with ID: {}", discountPercentage, productId);
    }

}

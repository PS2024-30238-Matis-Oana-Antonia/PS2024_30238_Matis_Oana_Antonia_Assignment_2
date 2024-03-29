package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.ProductLogger;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller class to handle HTTP requests related to products.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/product")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    /**
     * Constructs a new ProductController with the specified ProductService.
     *
     * @param productService The ProductService used to handle product-related business logic.
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     *
     * @return A ResponseEntity containing a list of ProductDTO objects representing the products.
     */
    @GetMapping()
    public ResponseEntity<List<ProductDTO>> getProducts() {
        LOGGER.info(ProductLogger.ALL_PRODUCTS_RETRIEVED);
        List<ProductDTO> dtos = productService.findProducts();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Inserts a new product.
     *
     * @param productDTO The ProductDTO object representing the product to insert.
     * @return A ResponseEntity containing the ID of the newly inserted product.
     */
    @PostMapping()
    public ResponseEntity<String> insertProduct(@Valid @RequestBody ProductDTO productDTO) {
        String productID = productService.insert(productDTO);
        LOGGER.debug(ProductLogger.PRODUCT_INSERTED, productID);
        return new ResponseEntity<>(productID, HttpStatus.CREATED);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param productID The ID of the product to retrieve.
     * @return A ResponseEntity containing the ProductDTO object representing the retrieved product.
     */
    @GetMapping(value = "/{id_product}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("id_product") String productID) {
        LOGGER.info(ProductLogger.PRODUCT_RETRIEVED_BY_ID, productID);
        ProductDTO dto = productService.findProductById(productID);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productID The ID of the product to delete.
     * @return A ResponseEntity indicating the success of the operation.
     */
    @DeleteMapping(value = "/{id_product}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id_product") String productID) {
        LOGGER.debug(ProductLogger.PRODUCT_DELETED, productID);
        productService.deleteProductById(productID);
        return new ResponseEntity<>("Product with ID " + productID + " deleted successfully", HttpStatus.OK);
    }

    /**
     * Updates a product by its ID.
     *
     * @param productID   The ID of the product to update.
     * @param productDTO  The updated ProductDTO object representing the new state of the product.
     * @return A ResponseEntity containing the updated ProductDTO object.
     */
    @PutMapping(value = "/{id_product}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id_product") String productID, @Valid @RequestBody ProductDTO productDTO) {
        LOGGER.debug(ProductLogger.PRODUCT_UPDATED, productID);
        ProductDTO updatedProduct = productService.updateProduct(productID, productDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}

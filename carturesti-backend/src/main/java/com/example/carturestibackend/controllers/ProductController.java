package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.ProductLogger;
import com.example.carturestibackend.dtos.CategoryDTO;
import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.entities.Review;
import com.example.carturestibackend.services.CategoryService;
import com.example.carturestibackend.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller class to handle HTTP requests related to products.
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/product")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final CategoryService categoryService;

    /**
     * Constructs a new ProductController with the specified ProductService.
     *
     * @param productService  The ProductService used to handle product-related business logic.
     * @param categoryService
     */
    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all products.
     *
     * @return A ModelAndView containing a list of ProductDTO objects representing the products.
     */
    @GetMapping()
    public ModelAndView getProducts() {
        LOGGER.info(ProductLogger.ALL_PRODUCTS_RETRIEVED);
        List<ProductDTO> dtos = productService.findProducts();
        ModelAndView modelAndView = new ModelAndView("/product");
        modelAndView.addObject("products", dtos);
        return modelAndView;
    }


    /**
     * Inserts a new product.
     *
     * @param productDTO The ProductDTO object representing the product to insert.
     * @return A ModelAndView containing the ID of the newly inserted product.
     */
    @PostMapping("/insertProduct")
    public ModelAndView insertProduct(@ModelAttribute ProductDTO productDTO) {
        String categoryId = productDTO.getId_category();

        if (categoryId != null) {

            CategoryDTO categoryDTO = categoryService.findCategoryById(categoryId);
            if (categoryDTO == null) {
                // Handle the case where the provided category ID is invalid
                LOGGER.error("Invalid category ID provided: {}", categoryId);
                ModelAndView errorModelAndView = new ModelAndView("errorPage");
                errorModelAndView.addObject("errorMessage", "Invalid category ID provided");
                return errorModelAndView;
            }
            productDTO.setId_category(categoryId);
        }
        String productID = productService.insert(productDTO);
        LOGGER.debug(ProductLogger.PRODUCT_INSERTED, productID);

        ModelAndView modelAndView = new ModelAndView("/product");
        modelAndView.addObject("productID", productID);
        return new ModelAndView("redirect:/product");
    }




    /**
     * Retrieves a product by its ID.
     *
     * @param id_product The ID of the product to retrieve.
     * @return A ModelAndView containing the ProductDTO object representing the retrieved product.
     */
    @GetMapping(value = "/getByProductId")
    public ModelAndView getProduct(@RequestParam("id_product") String id_product) {
        LOGGER.info(ProductLogger.PRODUCT_RETRIEVED_BY_ID, id_product);
        ProductDTO dto = productService.findProductById(id_product);
        ModelAndView modelAndView = new ModelAndView("/product");
        modelAndView.addObject("product", dto);
        return modelAndView;
    }


    /**
     * Deletes a product by its ID.
     *
     * @param productID The ID of the product to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @PostMapping(value = "/delete")
    public ModelAndView deleteProduct(@RequestParam("id_product") String productID, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/product");
        try {
            productService.deleteProductById(productID);
            LOGGER.debug(ProductLogger.PRODUCT_DELETED, productID);
            redirectAttributes.addFlashAttribute("successMessage", "Product with ID " + productID + " deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete product with ID " + productID + ". Please try again.");
        }
        return mav;
    }



    /**
     * Updates a product by its ID.
     *
     * @param id_product   The ID of the product to update.
     * @param productDTO  The updated ProductDTO object representing the new state of the product.
     * @return A ModelAndView containing the updated ProductDTO object.
     */
    @PostMapping("/productUpdate")
    public ModelAndView updateProduct(@RequestParam("id_product") String id_product, @Valid @ModelAttribute ProductDTO productDTO) {
        ModelAndView mav = new ModelAndView("redirect:/product");
        try {
            ProductDTO updatedProduct = productService.updateProduct(id_product, productDTO);
            mav.addObject("successMessage", "Product updated successfully!");
        } catch (Exception e) {
            mav.addObject("errorMessage", "Failed to update product. Please try again.");
        }
        return mav;
    }

    @PostMapping("/addReview")
    public ModelAndView addReviewToProduct(@PathVariable("id_product") String productId, @ModelAttribute Review review) {
        ModelAndView mav = new ModelAndView("redirect:/product");
        try {
            productService.addReviewToProduct(productId, review);
            mav.addObject("successMessage", "Review added successfully!");
        } catch (Exception e) {
            mav.addObject("errorMessage", "Failed to add review. Please try again.");
        }
        return mav;
    }



}

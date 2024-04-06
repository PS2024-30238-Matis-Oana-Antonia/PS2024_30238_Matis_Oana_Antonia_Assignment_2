package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.CategoryLogger;
import com.example.carturestibackend.dtos.CategoryDTO;
import com.example.carturestibackend.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller class to handle HTTP requests related to categories.
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/category")
public class CategoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    /**
     * Constructs a new CategoryController with the specified CategoryService.
     *
     * @param categoryService The CategoryService used to handle category-related business logic.
     */
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves all categories.
     *
     * @return A ModelAndView containing a list of CategoryDTO objects representing the categories.
     */
    @GetMapping()
    public ModelAndView getCategories() {
        LOGGER.info(CategoryLogger.ALL_CATEGORIES_RETRIEVED);
        List<CategoryDTO> dtos = categoryService.findCategories();
        ModelAndView modelAndView = new ModelAndView("/categories");
        modelAndView.addObject("categories", dtos);
        return modelAndView;
    }

    /**
     * Inserts a new category.
     *
     * @param categoryDTO The CategoryDTO object representing the category to insert.
     * @return A ModelAndView containing the ID of the newly inserted category.
     */
    @PostMapping()
    public ModelAndView insertCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        String categoryID = categoryService.insert(categoryDTO);
        LOGGER.debug(CategoryLogger.CATEGORY_INSERTED, categoryID);
        ModelAndView modelAndView = new ModelAndView("/categories");
        modelAndView.addObject("categoryID", categoryID);
        return modelAndView;
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryID The ID of the category to retrieve.
     * @return A ModelAndView containing the CategoryDTO object representing the retrieved category.
     */
    @GetMapping(value = "/{id_category}")
    public ModelAndView getCategory(@PathVariable("id_category") String categoryID) {
        LOGGER.info(CategoryLogger.CATEGORY_RETRIEVED_BY_ID, categoryID);
        CategoryDTO dto = categoryService.findCategoryById(categoryID);
        ModelAndView modelAndView = new ModelAndView("/categories");
        modelAndView.addObject("category", dto);
        return modelAndView;
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryID The ID of the category to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @DeleteMapping(value = "/{id_category}")
    public ModelAndView deleteCategory(@PathVariable("id_category") String categoryID) {
        LOGGER.debug(CategoryLogger.CATEGORY_DELETED, categoryID);
        categoryService.deleteCategoryById(categoryID);
        ModelAndView modelAndView = new ModelAndView("/categories");
        modelAndView.addObject("message", "Category with ID " + categoryID + " deleted successfully");
        return modelAndView;
    }

    /**
     * Updates a category by its ID.
     *
     * @param categoryID    The ID of the category to update.
     * @param categoryDTO   The updated CategoryDTO object representing the new state of the category.
     * @return A ModelAndView containing the updated CategoryDTO object.
     */
    @PutMapping(value = "/{id_category}")
    public ModelAndView updateCategory(@PathVariable("id_category") String categoryID, @Valid @RequestBody CategoryDTO categoryDTO) {
        LOGGER.debug(CategoryLogger.CATEGORY_UPDATED, categoryID);
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryID, categoryDTO);
        ModelAndView modelAndView = new ModelAndView("/categories");
        modelAndView.addObject("category", updatedCategory);
        return modelAndView;
    }
}

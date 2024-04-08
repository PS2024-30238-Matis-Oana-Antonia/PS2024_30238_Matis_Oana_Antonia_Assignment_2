package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.CategoryLogger;
import com.example.carturestibackend.constants.UserLogger;
import com.example.carturestibackend.dtos.CategoryDTO;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.CategoryService;
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
        ModelAndView modelAndView = new ModelAndView("/category");
        modelAndView.addObject("categories", dtos);
        return modelAndView;
    }

    /**
     * Inserts a new category.
     *
     * @param categoryDTO The CategoryDTO object representing the category to insert.
     * @return A ModelAndView containing the ID of the newly inserted category.
     */
    @PostMapping("/insertCategory")
    public ModelAndView insertCategory(@Valid @ModelAttribute CategoryDTO categoryDTO) {
        String categoryID = categoryService.insert(categoryDTO);
        LOGGER.debug(CategoryLogger.CATEGORY_INSERTED, categoryID);
        ModelAndView modelAndView = new ModelAndView("/category");
        modelAndView.addObject("categoryID", categoryID);
        return new ModelAndView("redirect:/category");
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
        ModelAndView modelAndView = new ModelAndView("/category");
        modelAndView.addObject("category", dto);
        return modelAndView;
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryID The ID of the category to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @PostMapping(value = "/delete")
    public ModelAndView deleteCategory(@RequestParam("id_category") String categoryID, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/category"); // Redirecting back to the user page
        try {
            categoryService.deleteCategoryById(categoryID);;
            LOGGER.debug(CategoryLogger.CATEGORY_DELETED, categoryID);
            redirectAttributes.addFlashAttribute("successMessage", "Category with ID " + categoryID + " deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete category with ID " + categoryID + ". Please try again.");
        }
        return mav;
    }


    /**
     * Updates a category by its ID.
     *
     * @param id_category    The ID of the category to update.
     * @param categoryDTO   The updated CategoryDTO object representing the new state of the category.
     * @return A ModelAndView containing the updated CategoryDTO object.
     */
    @PostMapping("/categoryUpdate")
    public ModelAndView updateCategory(@RequestParam("id_category") String id_category, @Valid @ModelAttribute CategoryDTO categoryDTO) {
        ModelAndView mav = new ModelAndView("redirect:/category"); // Redirecting back to the user page
        try {
            CategoryDTO updatedCategory = categoryService.updateCategory(id_category, categoryDTO);
            // You can optionally add a success message to be displayed on the redirected page
            mav.addObject("successMessage", "Category updated successfully!");
        } catch (Exception e) {
            // If an error occurs during the update, you can add an error message to be displayed on the redirected page
            mav.addObject("errorMessage", "Failed to update category. Please try again.");
        }
        return mav;
    }
}

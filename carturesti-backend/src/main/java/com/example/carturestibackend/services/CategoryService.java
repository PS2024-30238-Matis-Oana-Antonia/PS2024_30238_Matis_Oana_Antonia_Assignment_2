package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.CategoryLogger;
import com.example.carturestibackend.dtos.CategoryDTO;
import com.example.carturestibackend.dtos.mappers.CategoryMapper;
import com.example.carturestibackend.entities.Category;
import com.example.carturestibackend.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to categories.
 */
@Service
public class CategoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    /**
     * Constructs a new CategoryService with the specified CategoryRepository.
     *
     * @param categoryRepository The CategoryRepository used to interact with category data in the database.
     */
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A list of CategoryDTO objects representing the categories.
     */
    public List<CategoryDTO> findCategories() {
        LOGGER.error(CategoryLogger.ALL_CATEGORIES_RETRIEVED);
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream()
                .map(CategoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id_category The ID of the category to retrieve.
     * @return The CategoryDTO object representing the retrieved category.
     * @throws ResourceNotFoundException if the category with the specified ID is not found.
     */
    public CategoryDTO findCategoryById(String id_category) {
        Optional<Category> categoryOptional = categoryRepository.findById(id_category);
        if (!categoryOptional.isPresent()) {
            LOGGER.error(CategoryLogger.CATEGORY_NOT_FOUND_BY_ID, id_category);
            throw new ResourceNotFoundException(Category.class.getSimpleName() + " with id: " + id_category);
        }
        return CategoryMapper.toCategoryDTO(categoryOptional.get());
    }

    /**
     * Inserts a new category into the database.
     *
     * @param categoryDTO The CategoryDTO object representing the category to insert.
     * @return The ID of the newly inserted category.
     */
    public String insert(CategoryDTO categoryDTO) {
        Category category = CategoryMapper.fromCategoryDTO(categoryDTO);
        category = categoryRepository.save(category);
        LOGGER.debug(CategoryLogger.CATEGORY_INSERTED, category.getId_category());
        return category.getId_category();
    }

    /**
     * Deletes a category from the database by its ID.
     *
     * @param id_category The ID of the category to delete.
     * @throws ResourceNotFoundException if the category with the specified ID is not found.
     */
    public void deleteCategoryById(String id_category) {
        Optional<Category> categoryOptional = categoryRepository.findById(id_category);
        if (categoryOptional.isPresent()) {
            categoryRepository.delete(categoryOptional.get());
            LOGGER.debug(CategoryLogger.CATEGORY_DELETED, id_category);
        } else {
            LOGGER.error(CategoryLogger.CATEGORY_NOT_FOUND_BY_ID, id_category);
            throw new ResourceNotFoundException(Category.class.getSimpleName() + " with id: " + id_category);
        }
    }

    /**
     * Updates an existing category in the database.
     *
     * @param id_category The ID of the category to update.
     * @param categoryDTO The updated CategoryDTO object representing the new state of the category.
     * @return The updated CategoryDTO object.
     * @throws ResourceNotFoundException if the category with the specified ID is not found.
     */
    public CategoryDTO updateCategory(String id_category, CategoryDTO categoryDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(id_category);
        if (!categoryOptional.isPresent()) {
            LOGGER.error(CategoryLogger.CATEGORY_NOT_FOUND_BY_ID, id_category);
            throw new ResourceNotFoundException(Category.class.getSimpleName() + " with id: " + id_category);
        }

        Category existingCategory = categoryOptional.get();
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        LOGGER.debug(CategoryLogger.CATEGORY_UPDATED, updatedCategory.getId_category());

        return CategoryMapper.toCategoryDTO(updatedCategory);
    }
}

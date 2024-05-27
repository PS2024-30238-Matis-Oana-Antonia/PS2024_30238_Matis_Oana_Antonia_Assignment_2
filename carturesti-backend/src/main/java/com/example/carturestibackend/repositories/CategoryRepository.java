package com.example.carturestibackend.repositories;

import com.example.carturestibackend.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,String> {
    Category findByName(String categoryName);
}

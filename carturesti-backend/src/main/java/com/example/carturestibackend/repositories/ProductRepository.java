package com.example.carturestibackend.repositories;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.entities.Category;
import com.example.carturestibackend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,String> {

   List<Product> findProductByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.author LIKE %:keyword%")
    List<Product> searchProducts(@Param("keyword") String keyword);

    List<Product> findAllByOrderByPriceAsc();

    List<Product> findAllByOrderByPriceDesc();

    List<Product> findAllByOrderByNameAsc();

    List<Product> findAllByOrderByNameDesc();
}

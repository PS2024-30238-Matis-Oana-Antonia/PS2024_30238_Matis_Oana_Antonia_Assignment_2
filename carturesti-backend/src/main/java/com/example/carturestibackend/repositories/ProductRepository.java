package com.example.carturestibackend.repositories;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,String> {

    List<Product> findByName(String name);
}

package com.example.carturestibackend.repositories;

import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,String> {

}

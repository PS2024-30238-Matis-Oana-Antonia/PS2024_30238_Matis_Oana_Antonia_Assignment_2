package com.example.carturestibackend.repositories;

import com.example.carturestibackend.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, String> {
}

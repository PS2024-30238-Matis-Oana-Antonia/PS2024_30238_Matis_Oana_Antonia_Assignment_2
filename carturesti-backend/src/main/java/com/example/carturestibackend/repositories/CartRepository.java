package com.example.carturestibackend.repositories;

import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser(User user);
}

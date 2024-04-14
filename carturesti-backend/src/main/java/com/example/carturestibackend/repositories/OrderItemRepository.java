package com.example.carturestibackend.repositories;

import com.example.carturestibackend.entities.OrderItem;
import com.example.carturestibackend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

}

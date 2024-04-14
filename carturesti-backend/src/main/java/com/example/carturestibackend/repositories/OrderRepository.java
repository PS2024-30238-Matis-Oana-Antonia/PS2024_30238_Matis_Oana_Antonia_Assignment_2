package com.example.carturestibackend.repositories;

import com.example.carturestibackend.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository <Order,String> {

}

package com.example.carturestibackend.repositories;

import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.Payment;
import com.example.carturestibackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository <Payment,String> {

    List<Payment> findByUser(User user);
}

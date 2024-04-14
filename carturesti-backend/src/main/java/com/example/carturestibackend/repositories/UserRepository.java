package com.example.carturestibackend.repositories;

import com.example.carturestibackend.dtos.ProductDTO;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    User findByName(String name);
    User findByEmail(String email);

    User findByNameAndPassword(String name, String password);

    User findByRole(String role);

    User findUserIdByName(String name);

}

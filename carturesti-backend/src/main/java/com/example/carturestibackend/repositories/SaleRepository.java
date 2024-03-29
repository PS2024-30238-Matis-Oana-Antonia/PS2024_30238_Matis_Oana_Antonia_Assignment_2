package com.example.carturestibackend.repositories;

import com.example.carturestibackend.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, String> {
}

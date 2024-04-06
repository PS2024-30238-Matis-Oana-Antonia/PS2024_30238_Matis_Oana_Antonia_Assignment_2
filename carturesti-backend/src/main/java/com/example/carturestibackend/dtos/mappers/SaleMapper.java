package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.SaleDTO;
import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.Review;
import com.example.carturestibackend.entities.Sale;

import java.util.Optional;
import java.util.stream.Collectors;

public class SaleMapper {

    private SaleMapper() {
    }

    public static SaleDTO toSaleDTO(Sale sale) {
        return SaleDTO.builder()
                .id_sale(sale.getId_sale())
                .discount_percentage(sale.getDiscount_percentage())
                .price_after_discount(sale.getPrice_after_discount())
                .id_products(Optional.ofNullable(sale.getProducts())
                        .map(products -> products.stream().map(Product::getId_product).collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }

    public static Sale fromSaleDTO(SaleDTO saleDTO) {
        return Sale.builder()
                .id_sale(saleDTO.getId_sale())
                .discount_percentage(saleDTO.getDiscount_percentage())
                .price_after_discount(saleDTO.getPrice_after_discount())
                .products(Optional.ofNullable(saleDTO.getId_products())
                        .map(ids -> ids.stream().map(id -> Product.builder().id_product(id).build()).collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }
}

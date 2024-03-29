package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.SaleDTO;
import com.example.carturestibackend.entities.Sale;

public class SaleMapper {

    private SaleMapper() {
    }

    public static SaleDTO toSaleDTO(Sale sale) {
        return SaleDTO.builder()
                .id_sale(sale.getId_sale())
                .discount_percentage(sale.getDiscount_percentage())
                .price_after_discount(sale.getPrice_after_discount())
                .build();
    }

    public static Sale fromSaleDTO(SaleDTO saleDTO) {
        return Sale.builder()
                .id_sale(saleDTO.getId_sale())
                .discount_percentage(saleDTO.getDiscount_percentage())
                .price_after_discount(saleDTO.getPrice_after_discount())
                .build();
    }
}

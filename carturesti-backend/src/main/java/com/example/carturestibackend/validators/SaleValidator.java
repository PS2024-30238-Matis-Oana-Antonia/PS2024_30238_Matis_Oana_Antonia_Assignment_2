package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.Sale;
import org.springframework.stereotype.Component;

@Component
public class SaleValidator {

    public boolean isValid(Sale sale) {
        return isDiscountPercentageValid(sale.getDiscount_percentage()) &&
                isPriceAfterDiscountValid(sale.getPrice_after_discount());
    }

    public boolean validateSale(Sale sale) {
        return isValid(sale);
    }

    private boolean isDiscountPercentageValid(double discountPercentage) {
        return discountPercentage >= 0 && discountPercentage <= 100;
    }

    private boolean isPriceAfterDiscountValid(double priceAfterDiscount) {
        return priceAfterDiscount >= 0;
    }
}

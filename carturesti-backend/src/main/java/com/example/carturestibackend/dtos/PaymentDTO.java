package com.example.carturestibackend.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PaymentDTO {
    private String id_payment;
    private String card_owner;
    private String card_number;
    private String cvv;
    private String id_user;
}

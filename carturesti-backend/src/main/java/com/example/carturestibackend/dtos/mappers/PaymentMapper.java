package com.example.carturestibackend.dtos.mappers;

import com.example.carturestibackend.dtos.OrderDTO;
import com.example.carturestibackend.dtos.OrderItemDTO;
import com.example.carturestibackend.dtos.PaymentDTO;
import com.example.carturestibackend.entities.Order;
import com.example.carturestibackend.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PaymentMapper {
    private PaymentMapper() {
    }
    public static PaymentDTO toPaymentDTO(Payment payment) {
        return PaymentDTO.builder()
                .id_payment(payment.getId_payment())
                .card_owner(payment.getCard_owner())
                .card_number(payment.getCard_number())
                .cvv(payment.getCvv())
                .id_user(payment.getUser().getId_user())
                .build();
    }

    public static Payment fromPaymentDTO(PaymentDTO paymentDTO) {
        return Payment.builder()
                .card_owner(paymentDTO.getCard_owner())
                .card_number(paymentDTO.getCard_number())
                .cvv(paymentDTO.getCvv())
                .user(User.builder().id_user(paymentDTO.getId_user()).build())
                .build();
    }
}
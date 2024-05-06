package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.CategoryLogger;
import com.example.carturestibackend.constants.PaymentLogger;
import com.example.carturestibackend.dtos.CategoryDTO;
import com.example.carturestibackend.dtos.PaymentDTO;
import com.example.carturestibackend.dtos.mappers.CategoryMapper;
import com.example.carturestibackend.dtos.mappers.PaymentMapper;
import com.example.carturestibackend.entities.Category;
import com.example.carturestibackend.entities.Payment;
import com.example.carturestibackend.repositories.PaymentRepository;
import com.example.carturestibackend.repositories.UserRepository;
import com.example.carturestibackend.validators.CategoryValidator;
import com.example.carturestibackend.validators.PaymentValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final PaymentValidator paymentValidator;
    private final UserRepository userRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, PaymentValidator paymentValidator, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentValidator = paymentValidator;
        this.userRepository = userRepository;
    }



    public List<PaymentDTO> findPayments() {
        LOGGER.error(PaymentLogger.ALL_PAYMENTS_RETRIEVED);
        List<Payment> paymentList = paymentRepository.findAll();
        return paymentList.stream()
                .map(PaymentMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public String insert(PaymentDTO paymentDTO) {
        Payment payment = PaymentMapper.fromPaymentDTO(paymentDTO);
        PaymentValidator.validate(payment);
        payment = paymentRepository.save(payment);
        LOGGER.debug(PaymentLogger.PAYMENT_INSERTED, payment.getId_payment());
        return payment.getId_payment();
    }

    public PaymentDTO findPaymentById(String id_payment) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id_payment);
        if (!paymentOptional.isPresent()) {
            LOGGER.error(PaymentLogger.PAYMENT_NOT_FOUND_BY_ID, id_payment);
            throw new ResourceNotFoundException(Category.class.getSimpleName() + " with id: " + id_payment);
        }
        return PaymentMapper.toPaymentDTO(paymentOptional.get());
    }

    @Transactional
    public void deletePaymentById(String id) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            paymentRepository.delete(payment);
            LOGGER.debug(PaymentLogger.PAYMENT_DELETED, id);
        } else {
            LOGGER.error(PaymentLogger.PAYMENT_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Payment.class.getSimpleName() + " with id: " + id);
        }
    }

    public PaymentDTO updatePayment(String id, PaymentDTO paymentDTO) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (!paymentOptional.isPresent()) {
            LOGGER.error(PaymentLogger.PAYMENT_NOT_FOUND_BY_ID, id);
            throw new ResourceNotFoundException(Payment.class.getSimpleName() + " with id: " + id);
        }

        Payment existingPayment = paymentOptional.get();
        existingPayment.setCard_owner(paymentDTO.getCard_owner());
        existingPayment.setCard_number(paymentDTO.getCard_number());
        existingPayment.setCvv(paymentDTO.getCvv());


        Payment updatedPayment = paymentRepository.save(existingPayment);
        LOGGER.debug(PaymentLogger.PAYMENT_UPDATED, updatedPayment.getId_payment());

        return PaymentDTO.builder()
                .id_payment(updatedPayment.getId_payment())
                .card_owner(updatedPayment.getCard_owner())
                .card_number(updatedPayment.getCard_number())
                .cvv(updatedPayment.getCvv())
                .id_user(updatedPayment.getUser().getId_user())
                .build();
    }
}

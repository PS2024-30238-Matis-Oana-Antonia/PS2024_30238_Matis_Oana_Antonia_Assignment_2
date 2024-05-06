package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.PaymentLogger;
import com.example.carturestibackend.dtos.PaymentDTO;
import com.example.carturestibackend.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/insert")
    public ModelAndView insertPayment(@Valid @ModelAttribute PaymentDTO paymentDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            LOGGER.error("Invalid payment details: {}", bindingResult.getAllErrors());
            modelAndView.setViewName("redirect:/payment");
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid payment details. Please check your input.");
            return modelAndView;
        }

        String paymentId = paymentService.insert(paymentDTO);
        LOGGER.debug(PaymentLogger.PAYMENT_INSERTED, paymentId);

        modelAndView.setViewName("redirect:/payment");
        redirectAttributes.addFlashAttribute("successMessage", "Payment with ID " + paymentId + " inserted successfully.");
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getPaymentById(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            PaymentDTO paymentDTO = paymentService.findPaymentById(id);
            modelAndView.addObject("payment", paymentDTO);
            modelAndView.setViewName("payment-details"); // Assuming there's a view named "payment-details"
        } catch (Exception e) {
            LOGGER.error("Error retrieving payment with ID {}: {}", id, e.getMessage());
            modelAndView.setViewName("redirect:/payment");
            modelAndView.addObject("errorMessage", "Error retrieving payment. Please try again.");
        }
        return modelAndView;
    }

    @PostMapping("/delete/{id}")
    public ModelAndView deletePayment(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("redirect:/payment");
        try {
            paymentService.deletePaymentById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Payment with ID " + id + " deleted successfully.");
        } catch (Exception e) {
            LOGGER.error("Error deleting payment with ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting payment. Please try again.");
        }
        return modelAndView;
    }

    @PostMapping("/update/{id}")
    public ModelAndView updatePayment(@PathVariable("id") String id, @Valid @ModelAttribute PaymentDTO paymentDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("redirect:/payment");
        if (bindingResult.hasErrors()) {
            LOGGER.error("Invalid payment details: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid payment details. Please check your input.");
            return modelAndView;
        }
        try {
            PaymentDTO updatedPayment = paymentService.updatePayment(id, paymentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Payment with ID " + id + " updated successfully.");
        } catch (Exception e) {
            LOGGER.error("Error updating payment with ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating payment. Please try again.");
        }
        return modelAndView;
    }

    @GetMapping("/all")
    public ModelAndView getAllPayments() {
        ModelAndView modelAndView = new ModelAndView();
        try {
            List<PaymentDTO> paymentDTOList = paymentService.findPayments();
            modelAndView.addObject("payments", paymentDTOList);
            modelAndView.setViewName("payments"); // Assuming there's a view named "payments"
        } catch (Exception e) {
            LOGGER.error("Error retrieving all payments: {}", e.getMessage());
            modelAndView.setViewName("redirect:/payment");
            modelAndView.addObject("errorMessage", "Error retrieving payments. Please try again.");
        }
        return modelAndView;
    }
}

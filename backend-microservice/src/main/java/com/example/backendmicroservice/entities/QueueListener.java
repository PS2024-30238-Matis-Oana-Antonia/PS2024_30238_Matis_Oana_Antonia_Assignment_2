package com.example.backendmicroservice.entities;

import com.example.backendmicroservice.dtos.NotificationRequestDTO;
import com.example.backendmicroservice.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

    private final EmailService emailService;

    @Autowired
    public QueueListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "super-queue")
    public void listen(NotificationRequestDTO requestDto) {
        try {
            // Aici puteți procesa mesajul și apela serviciul de email
            emailService.sendEmailToQueue(requestDto);
            System.out.println("Email sent successfully!");
        } catch (Exception ex) {
            System.err.println("An error occurred while processing the message: " + ex.getMessage());
        }
    }
}

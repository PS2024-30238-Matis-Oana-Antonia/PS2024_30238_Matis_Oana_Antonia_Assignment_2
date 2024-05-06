package com.example.backendmicroservice.services;

import com.example.backendmicroservice.dtos.NotificationRequestDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final RabbitTemplate rabbitTemplate;
    private final String emailQueueName;

    @Autowired
    public EmailService(RabbitTemplate rabbitTemplate, @Value("${spring.rabbitmq.queue}") String emailQueueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.emailQueueName = emailQueueName;
    }

    public void sendEmailToQueue(NotificationRequestDTO requestDto) {
        rabbitTemplate.convertAndSend(emailQueueName, requestDto);
    }
}

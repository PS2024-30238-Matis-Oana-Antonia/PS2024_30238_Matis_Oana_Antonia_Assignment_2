package com.example.carturestibackend.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitSender {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String exchange, String routingKey, Object payload) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}

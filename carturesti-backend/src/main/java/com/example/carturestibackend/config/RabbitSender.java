package com.example.carturestibackend.config;

import com.example.carturestibackend.config.AMQPConfig;
import com.example.carturestibackend.dtos.UserDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RabbitSender {

    private final RabbitTemplate rabbitTemplate;


    @Autowired
    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(UserDTO payload) {
        rabbitTemplate.convertAndSend(AMQPConfig.QUEUE_NAME, payload);
    }
}
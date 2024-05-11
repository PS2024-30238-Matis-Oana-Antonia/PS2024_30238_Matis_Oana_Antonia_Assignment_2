package com.example.carturestibackend.config;

import com.example.carturestibackend.dtos.NotificationRequestDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.example.carturestibackend.config.AMQPConfig.EXCHANGE_NAME;
import static com.example.carturestibackend.config.AMQPConfig.ROUTING_KEY;


@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void send(NotificationRequestDTO payload) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, payload);
    }
}

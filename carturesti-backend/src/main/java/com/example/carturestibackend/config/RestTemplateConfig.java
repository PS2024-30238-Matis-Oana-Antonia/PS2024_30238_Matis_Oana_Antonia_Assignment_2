package com.example.carturestibackend.config;

import com.example.carturestibackend.entities.NotificationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    public static final String FIRST_TOKEN = "e2a0c9b8-5f11-4b84-a1d2-6f3f12e89e3d";
    public static final String SECOND_TOKEN = "3c7e45a3-86c7-42e7-89c1-5d79fc8957af";
    public static final String authToken = FIRST_TOKEN + SECOND_TOKEN;

    private final ObjectMapper objectMapper;

    public RestTemplateConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public void sendEmail(NotificationRequest notificationRequestDto, RestTemplate restTemplate) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(notificationRequestDto);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authToken);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "http://localhost:8081/notification/email",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Email successfully sent!");
            } else {
                System.out.println("Failed to send email. Response: " + response.getBody());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

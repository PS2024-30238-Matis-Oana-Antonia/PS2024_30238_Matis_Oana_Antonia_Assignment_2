package com.example.carturestibackend.services;

import com.example.carturestibackend.dtos.NotificationRequestDTO;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class NotificationService {
    private RestTemplate restTemplate;

    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(NotificationRequestDTO notificationRequestDto, String token) {
        // Setarea capetelor de antet
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Crearea obiectului HttpEntity cu datele și capetele de antet
        HttpEntity<NotificationRequestDTO> entity = new HttpEntity<>(notificationRequestDto, headers);

        // Trimiterea solicitării HTTP către endpoint-ul corespunzător
        String url = "URL_ENDPOINT"; // înlocuiți cu URL-ul real
       // ResponseEntity<MessageDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, MessageDTO.class);

        // Obținerea răspunsului
       // MessageDTO response = responseEntity.getBody();
        // Procesarea răspunsului dacă este necesar
    }
}

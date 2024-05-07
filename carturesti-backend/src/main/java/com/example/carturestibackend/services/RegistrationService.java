package com.example.carturestibackend.services;

import com.example.carturestibackend.config.RabbitSender;
import com.example.carturestibackend.dtos.MessageDTO;
import com.example.carturestibackend.dtos.NotificationRequestDTO;
import com.example.carturestibackend.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
public class RegistrationService {

    private final RestTemplate restTemplate;
    private final RabbitSender rabbitSender;

    @Value("${authorization.token}") // Injectăm tokenul din fișierul de configurare (application.properties sau application.yml)
    private String authorizationToken;

    public RegistrationService(RestTemplate restTemplate, RabbitSender rabbitSender) {
        this.restTemplate = restTemplate;
        this.rabbitSender = rabbitSender;
    }

    private boolean validateAuthorizationToken(String token) {

        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        return Pattern.matches(regex, token);
    }

    public ResponseEntity<String> register(UserDTO userDto) {
        // Verificăm dacă toți parametrii necesari sunt prezenți în corpul cererii
        if (userDto.getName() == null || userDto.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some required fields are missing in the payload!");
        }

        // Verificăm dacă tokenul de autorizare este valid
        if (!validateAuthorizationToken(authorizationToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization token!");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authorizationToken); // Setăm tokenul de autorizare în antet
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Creăm entitatea HttpEntity cu datele utilizatorului pentru cererea HTTP
        HttpEntity<UserDTO> entity = new HttpEntity<>(userDto, headers);

        // Apelăm endpoint-ul de înregistrare pentru a trimite email-ul și obținem răspunsul
        ResponseEntity<MessageDTO> response = restTemplate.exchange(
                "URL", HttpMethod.POST, entity, MessageDTO.class
        );

        // Verificăm răspunsul și returnăm un răspuns corespunzător
        if (response.getStatusCode() == HttpStatus.OK) {
            // Apelăm metoda pentru trimiterea notificării în coadă
            sendNotification(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user!");
        }
    }

    private void sendNotification(UserDTO userDto) {
        NotificationRequestDTO notificationRequestDto = createNotificationRequestDto(userDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authorizationToken); // Setăm tokenul de autorizare în antet
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NotificationRequestDTO> entity = new HttpEntity<>(notificationRequestDto, headers);

        ResponseEntity<MessageDTO> response = restTemplate.exchange(
                "URL_FOR_NOTIFICATION_SERVICE", HttpMethod.POST, entity, MessageDTO.class
        );

        // Verificăm răspunsul (dacă este necesar)
        if (response.getStatusCode() != HttpStatus.OK) {
            System.out.println("Error sending notification: " + response.getStatusCodeValue());
        }
    }

    private NotificationRequestDTO createNotificationRequestDto(UserDTO userDto) {
        NotificationRequestDTO notificationRequestDto = new NotificationRequestDTO();
        notificationRequestDto.setId(userDto.getId_user());
        notificationRequestDto.setName(userDto.getName());
        notificationRequestDto.setEmail(userDto.getEmail());
        // Alte câmpuri necesare pot fi adăugate aici

        return notificationRequestDto;
    }
}

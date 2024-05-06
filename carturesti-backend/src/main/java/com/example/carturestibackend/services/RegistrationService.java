package com.example.carturestibackend.services;

import com.example.carturestibackend.config.RabbitSender;
import com.example.carturestibackend.dtos.MessageDTO;
import com.example.carturestibackend.dtos.NotificationRequestDTO;
import com.example.carturestibackend.dtos.UserDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
public class RegistrationService {

    private final RestTemplate restTemplate;
    private final RabbitSender rabbitSender;

    public RegistrationService(RestTemplate restTemplate, RabbitSender rabbitSender) {
        this.restTemplate = restTemplate;
        this.rabbitSender = rabbitSender;
    }

    private boolean validateAuthorizationToken(String token) {
        // Verifică formatul tokenului de autorizare
        // În acest exemplu, tokenul este considerat valid dacă este format din 2 UUID-uri concatenate
        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        return Pattern.matches(regex, token);
    }

    public void register(UserDTO userDto, String token) {
        // Verificare dacă tokenul de autorizare este prezent
        if (token == null || token.isEmpty()) {
            System.out.println("Eroare: Header-ul 'Authorization' lipsește din cererea HTTP!");
            return;
        }

        // Verificare token de autorizare
        if (!validateAuthorizationToken(token)) {
            System.out.println("Eroare: Tokenul de autorizare nu este valid!");
            return;
        }

        // Verificare dacă toate câmpurile necesare din payload sunt completate
        if (userDto.getName() == null || userDto.getEmail() == null) {
            System.out.println("Eroare: Unele câmpuri obligatorii din payload lipsesc!");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Setăm corect token-ul de autorizare în header-ul 'Authorization'
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));


        // Crearea entității HttpEntity cu datele userului pentru cererea HTTP
        HttpEntity<UserDTO> entity = new HttpEntity<>(userDto, headers);

        // Apelarea endpoint-ului de înregistrare pentru a trimite email-ul și obținerea răspunsului
        ResponseEntity<MessageDTO> response = restTemplate.exchange(
                "URL", HttpMethod.POST, entity, MessageDTO.class
        );

        // Verificarea răspunsului (dacă este necesar)
        if (response.getStatusCode() == HttpStatus.OK) {
            // Apelarea metodei pentru trimiterea notificării în coadă
            sendNotification(userDto, token);
        } else {
            // Poți trata alte cazuri în funcție de statusul răspunsului
            // De exemplu, poți arunca o excepție sau poți face altă logică de gestionare a erorilor
            System.out.println("Eroare la înregistrare: " + response.getStatusCodeValue());
        }
    }


    // Metoda pentru a crea un NotificationRequestDto dintr-un UserDTO
    private NotificationRequestDTO createNotificationRequestDto(UserDTO userDto) {
        NotificationRequestDTO notificationRequestDto = new NotificationRequestDTO();
        notificationRequestDto.setId(userDto.getId_user());
        notificationRequestDto.setName(userDto.getName());
        notificationRequestDto.setEmail(userDto.getEmail());
        // Alte câmpuri necesare pot fi adăugate aici

        return notificationRequestDto;
    }

    // Metoda pentru trimiterea notificării în coadă
    private void sendNotification(UserDTO userDto, String token) {
        NotificationRequestDTO notificationRequestDto = createNotificationRequestDto(userDto);

        // Configurarea headerelor pentru cererea HTTP către serviciul de coadă
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crearea entității HttpEntity cu datele notificării pentru cererea HTTP către serviciul de coadă
        HttpEntity<NotificationRequestDTO> entity = new HttpEntity<>(notificationRequestDto, headers);

        // Apelarea serviciului de coadă pentru a trimite notificarea
        ResponseEntity<MessageDTO> response = restTemplate.exchange(
                "URL_FOR_NOTIFICATION_SERVICE", HttpMethod.POST, entity, MessageDTO.class
        );

        // Verificarea răspunsului (dacă este necesar)
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Notificare trimisă cu succes!");
        } else {
            System.out.println("Eroare la trimiterea notificării: " + response.getStatusCodeValue());
        }
    }
}

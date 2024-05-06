package com.example.backendmicroservice.controllers;

import com.example.backendmicroservice.dtos.MessageDTO;
import com.example.backendmicroservice.dtos.NotificationRequestDTO;
import com.example.backendmicroservice.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<MessageDTO> sendEmail(@RequestHeader("Authorization") String authToken,
                                                @RequestBody NotificationRequestDTO requestDto) {
        // Validați token-ul și apoi trimiteți e-mailul
        emailService.sendEmailToQueue(requestDto);
        return new ResponseEntity<>(new MessageDTO("Email sent successfully!"), HttpStatus.OK);
    }
}

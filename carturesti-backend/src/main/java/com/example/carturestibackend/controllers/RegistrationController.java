package com.example.carturestibackend.controllers;

import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private RegistrationService registrationService;

    // Setter pentru RegistrationService
    @Autowired
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestHeader("Authorization") String token,
                                               @RequestBody UserDTO userDTO) {
        registrationService.register(userDTO, token);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }
}

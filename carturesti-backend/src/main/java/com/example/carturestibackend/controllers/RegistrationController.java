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

    private final RegistrationService registrationService;
    private final String authorizationToken = "1234carturesti4321";

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO, @RequestHeader("Authorization") String authorizationHeader) {
        if (!isValidAuthorizationHeader(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization token");
        }

        if (userDTO.getName() == null || userDTO.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields in payload");
        }
        registrationService.register(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    private boolean isValidAuthorizationHeader(String authorizationHeader) {
        String expectedToken = "Bearer " + authorizationToken;
        return authorizationHeader != null && authorizationHeader.equals(expectedToken);
    }
}




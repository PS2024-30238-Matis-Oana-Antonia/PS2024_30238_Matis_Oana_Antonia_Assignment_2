package com.example.carturestibackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationRequestDTO implements Serializable {
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String subject;

    @NotNull
    @Email(message = "The format for the email is invalid!!!")
    private String email;

    @NotBlank
    private String body;

    private String attachmentPath;

}
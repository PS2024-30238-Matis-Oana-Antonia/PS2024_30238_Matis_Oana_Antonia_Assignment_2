package com.example.carturestibackend.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class NotificationRequest implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
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

}

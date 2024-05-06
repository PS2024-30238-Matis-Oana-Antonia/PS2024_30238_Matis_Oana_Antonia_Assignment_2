package com.example.carturestibackend.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {
    private String id;
    private String name;
    private String email;

}

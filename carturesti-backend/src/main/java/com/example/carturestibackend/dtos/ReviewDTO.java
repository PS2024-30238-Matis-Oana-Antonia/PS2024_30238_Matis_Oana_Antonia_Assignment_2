package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.Product;
import com.example.carturestibackend.entities.User;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private String id;
    private int rating;
    private String comment;
    private String id_user;
    private String id_product;
}

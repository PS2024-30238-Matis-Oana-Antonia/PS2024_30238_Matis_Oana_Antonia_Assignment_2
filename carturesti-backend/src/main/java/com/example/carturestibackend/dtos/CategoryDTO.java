package com.example.carturestibackend.dtos;

import com.example.carturestibackend.entities.Product;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private String id_category;
    private String name;
    private String description;
    private List<String> id_products;

}

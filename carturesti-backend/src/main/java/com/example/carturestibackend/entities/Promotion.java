package com.example.carturestibackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="promotiondb")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Promotion{

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id_promotion;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "percentage", nullable = false, length = 1000)
    private double percentage;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIgnore
    private List<Product> products;


}
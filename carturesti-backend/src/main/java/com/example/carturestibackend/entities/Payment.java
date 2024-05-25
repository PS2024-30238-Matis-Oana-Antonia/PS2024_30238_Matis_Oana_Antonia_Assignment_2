package com.example.carturestibackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name="paymentdb")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id_payment;

    private String card_owner;
    private String card_number;
    private String cvv;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user")
    private User user;

}

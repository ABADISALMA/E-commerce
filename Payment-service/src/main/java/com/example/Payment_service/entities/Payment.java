package com.example.Payment_service.entities;

import com.example.Payment_service.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    private Long orderId;
    private Double amount;
    private Status status; // SUCCESS, FAILED, PENDING

    private LocalDateTime paymentDate;
}

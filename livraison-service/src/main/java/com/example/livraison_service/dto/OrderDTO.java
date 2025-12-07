package com.example.livraison_service.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private Long id;
    private String trackingNumber;
    private String adresseLivraison;
    private String statut;

    private Long userId;
    private Double totalAmount;
}

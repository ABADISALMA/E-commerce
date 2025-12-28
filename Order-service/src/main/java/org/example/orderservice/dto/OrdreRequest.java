package org.example.orderservice.dto;

import lombok.Data;

@Data
public class OrdreRequest {
    private String trackingNumber;
    private String adresseLivraison;
    private String statut; // EN_PREPARATION, EN_COURS_DE_LIVRAISON, LIVRE, ANNULE
}

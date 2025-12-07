package com.example.livraison_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponseDTO {
    private Long orderId;              // ✅ ID de la commande
    private String trackingNumber;
    private String statut;
    private String adresseLivraison;
    private Double totalAmount;        // ✅ Prix total
    private GeoPositionDTO position;
    private Long requestedByUserId;

    // ✅ Constructeur avec OrderDTO
    public TrackingResponseDTO(OrderDTO order, GeoPositionDTO position) {
        this.orderId = order.getId();
        this.trackingNumber = order.getTrackingNumber();
        this.statut = order.getStatut();
        this.adresseLivraison = order.getAdresseLivraison();
        this.totalAmount = order.getTotalAmount();
        this.position = position;
    }
}
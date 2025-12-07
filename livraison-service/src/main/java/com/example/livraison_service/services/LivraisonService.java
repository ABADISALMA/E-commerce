package com.example.livraison_service.services;

import com.example.livraison_service.Client.OrderServiceClient;
import com.example.livraison_service.dto.GeoPositionDTO;
import com.example.livraison_service.dto.OrderDTO;
import com.example.livraison_service.dto.TrackingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LivraisonService {

    private final OrderServiceClient orderServiceClient;
    private final GeolocService geolocService;

    public OrderDTO suivreColis(String trackingNumber, Long userId) {
        System.out.println("📦 Recherche du colis " + trackingNumber + " pour userId: " + userId);

        OrderDTO order = orderServiceClient.getOrderByTracking(trackingNumber);

        if (order != null) {
            // ✅ Log avec ID et prix total
            System.out.println("✅ Commande trouvée:");
            System.out.println("   - Order ID: " + order.getId());
            System.out.println("   - Tracking: " + order.getTrackingNumber());
            System.out.println("   - Statut: " + order.getStatut());
            System.out.println("   - Prix Total: " + order.getTotalAmount() + " DH");
            System.out.println("   - User ID (propriétaire): " + order.getUserId());
            System.out.println("   - User ID (demandeur): " + userId);
        } else {
            System.out.println("❌ Aucune commande trouvée pour tracking: " + trackingNumber);
        }

        return order;
    }

    public TrackingResponseDTO suivreColisAvecPosition(String trackingNumber, Long userId) {
        System.out.println("📍 Recherche position du colis " + trackingNumber + " pour userId: " + userId);

        OrderDTO colis = orderServiceClient.getOrderByTracking(trackingNumber);

        if (colis == null) {
            System.out.println("❌ Colis non trouvé");
            return null;
        }

        // ✅ Log avec ID et prix total
        System.out.println("✅ Colis trouvé:");
        System.out.println("   - Order ID: " + colis.getId());
        System.out.println("   - Prix Total: " + colis.getTotalAmount() + " DH");

        GeoPositionDTO position = geolocService.geocode(colis.getAdresseLivraison());

        TrackingResponseDTO response = new TrackingResponseDTO(colis, position);
        response.setRequestedByUserId(userId);

        return response;
    }
}
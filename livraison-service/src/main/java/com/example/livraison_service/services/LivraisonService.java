package com.example.livraison_service.services;

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

    public OrderDTO suivreColis(String trackingNumber) {
        return orderServiceClient.getOrdreByTracking(trackingNumber);
    }

    public TrackingResponseDTO suivreColisAvecPosition(String trackingNumber) {

        OrderDTO colis = orderServiceClient.getOrdreByTracking(trackingNumber);
        if (colis == null) return null;

        GeoPositionDTO position = geolocService.geocode(colis.getAdresseLivraison());

        return new TrackingResponseDTO(colis, position);
    }
}

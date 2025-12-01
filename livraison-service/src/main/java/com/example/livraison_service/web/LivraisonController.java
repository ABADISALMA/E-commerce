package com.example.livraison_service.web;

import com.example.livraison_service.dto.OrderDTO;
import com.example.livraison_service.dto.TrackingResponseDTO;
import com.example.livraison_service.services.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;

    @GetMapping("/suivi/{trackingNumber}")
    public OrderDTO suivreColis(@PathVariable String trackingNumber) {
        return livraisonService.suivreColis(trackingNumber);
    }

    @GetMapping("/suivi-position/{trackingNumber}")
    public TrackingResponseDTO suivreColisAvecPosition(@PathVariable String trackingNumber) {
        return livraisonService.suivreColisAvecPosition(trackingNumber);
    }
}

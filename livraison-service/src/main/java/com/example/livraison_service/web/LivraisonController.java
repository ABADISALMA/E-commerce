package com.example.livraison_service.web;

import com.example.livraison_service.dto.OrderDTO;
import com.example.livraison_service.dto.TrackingResponseDTO;
import com.example.livraison_service.dto.UserPrincipal;
import com.example.livraison_service.services.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    @GetMapping("/suivi/{trackingNumber}")
    public OrderDTO suivreColis(
            @PathVariable String trackingNumber,
            @AuthenticationPrincipal UserPrincipal principal) { // ✅ AJOUTÉ

        System.out.println("🔍 Suivi demandé par User ID: " + principal.getUserId() +
                " (" + principal.getUsername() + ")");

        return livraisonService.suivreColis(trackingNumber, principal.getUserId()); // ✅ MODIFIÉ
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    @GetMapping("/suivi-position/{trackingNumber}")
    public TrackingResponseDTO suivreColisAvecPosition(
            @PathVariable String trackingNumber,
            @AuthenticationPrincipal UserPrincipal principal) { // ✅ AJOUTÉ

        System.out.println("🔍 Suivi position demandé par User ID: " + principal.getUserId() +
                " (" + principal.getUsername() + ")");

        return livraisonService.suivreColisAvecPosition(trackingNumber, principal.getUserId()); // ✅ MODIFIÉ
    }

    // ✅ NOUVEAU : Endpoint de test pour vérifier l'extraction de l'ID
    @GetMapping("/mon-profil")
    public UserPrincipal getMonProfil(@AuthenticationPrincipal UserPrincipal principal) {
        System.out.println("✅ User ID: " + principal.getUserId());
        System.out.println("✅ Username: " + principal.getUsername());
        System.out.println("✅ Role: " + principal.getRole());
        return principal;
    }
}
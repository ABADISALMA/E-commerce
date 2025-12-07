package com.example.Payment_service.client;

import com.example.Payment_service.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class OrderClient {

    private final RestTemplate restTemplate;

    /**
     * Récupérer une commande par son ID
     */
    public OrderResponse getOrderById(Long orderId) {
        String url = "http://ORDER-SERVICE/orders/by-id/" + orderId;

        System.out.println("🔍 Appel à: " + url);

        HttpHeaders headers = new HttpHeaders();
        String token = getTokenFromRequest();
        if (token != null) {
            System.out.println("🔑 Token trouvé: " + token.substring(0, Math.min(20, token.length())) + "...");
            headers.set("Authorization", "Bearer " + token);
        } else {
            System.out.println("⚠️ Aucun token trouvé dans la requête");
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<OrderResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    OrderResponse.class
            );

            OrderResponse order = response.getBody();
            System.out.println("✅ Commande récupérée: ID=" + (order != null ? order.getId() : "null"));
            return order;

        } catch (HttpClientErrorException e) {
            System.out.println("❌ Erreur HTTP " + e.getStatusCode() + ": " + e.getMessage());
            throw new RuntimeException("Impossible de récupérer la commande: " + e.getMessage());

        } catch (ResourceAccessException e) {
            System.out.println("❌ ORDER-SERVICE inaccessible: " + e.getMessage());
            throw new RuntimeException("Service de commandes indisponible");

        } catch (Exception e) {
            System.out.println("❌ Erreur inattendue: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération de la commande");
        }
    }

    /**
     * Mettre à jour le statut d'une commande
     */
    public void updateOrderStatus(Long orderId, String status) {
        String url = "http://ORDER-SERVICE/orders/update-status/" + orderId + "?status=" + status;

        System.out.println("🔄 Mise à jour du statut vers: " + status);
        System.out.println("🔍 Appel à: " + url);

        HttpHeaders headers = new HttpHeaders();
        String token = getTokenFromRequest();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            System.out.println("✅ Statut mis à jour avec succès");

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la mise à jour: " + e.getMessage());
            throw new RuntimeException("Impossible de mettre à jour le statut de la commande");
        }
    }

    /**
     * Extraire le token JWT de la requête courante
     */
    private String getTokenFromRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }
}
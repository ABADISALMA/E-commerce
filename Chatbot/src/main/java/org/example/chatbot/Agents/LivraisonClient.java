package org.example.chatbot.Agents;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class LivraisonClient {

    private final WebClient webClient;

    public LivraisonClient(WebClient.Builder lbBuilder) {
        this.webClient = lbBuilder.baseUrl("http://LIVRAISON-SERVICE").build();
    }

    // ✅ USER: suivre colis par trackingNumber
    public Mono<String> trackByNumber(String trackingNumber, String authHeader) {
        return webClient.get()
                .uri("/livraisons/suivi/{trackingNumber}", trackingNumber)
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("{\"error\":\"LIVRAISON_SERVICE_DOWN_OR_NOT_FOUND\"}"));
    }

    // ✅ Endpoint de test : vérifier que le token est bien lu par livraison-service
    public Mono<String> myProfile(String authHeader) {
        return webClient.get()
                .uri("/livraisons/mon-profil")
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("{\"error\":\"LIVRAISON_SERVICE_DOWN_OR_NOT_FOUND\"}"));
    }

}

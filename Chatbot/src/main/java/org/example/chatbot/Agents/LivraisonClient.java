package org.example.chatbot.Agents;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class LivraisonClient {

    private final WebClient webClient;

    // IMPORTANT : ce Builder doit être @LoadBalanced (via WebClientConfig)
    public LivraisonClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://livraison-service").build();
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
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new RuntimeException("LIVRAISON_4XX_NOT_FOUND_OR_FORBIDDEN")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> Mono.error(new RuntimeException("LIVRAISON_5XX_SERVICE_DOWN")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
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
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new RuntimeException("LIVRAISON_4XX_FORBIDDEN_OR_BAD_REQUEST")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> Mono.error(new RuntimeException("LIVRAISON_5XX_SERVICE_DOWN")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
    }
}

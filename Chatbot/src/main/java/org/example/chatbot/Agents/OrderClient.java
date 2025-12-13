package org.example.chatbot.Agents;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class OrderClient {

    private final WebClient webClient;

    public OrderClient(WebClient.Builder lbBuilder) {
        this.webClient = lbBuilder.baseUrl("http://ORDER-SERVICE").build();
    }

    // ✅ GET /orders/by-user/{userId}  (pour "mes commandes")
    public Mono<String> getOrdersByUser(Long userId, String authHeader) {
        return webClient.get()
                .uri("/orders/by-user/{userId}", userId)
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("{\"error\":\"ORDER_SERVICE_DOWN_OR_FORBIDDEN\"}"));
    }

    // ✅ GET /orders/by-id/{id}
    public Mono<String> getOrderById(Long id, String authHeader) {
        return webClient.get()
                .uri("/orders/by-id/{id}", id)
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("{\"error\":\"ORDER_NOT_FOUND_OR_FORBIDDEN\"}"));
    }

    // ✅ GET /orders/tracking/{trackingNumber}
    public Mono<String> getOrderByTracking(String trackingNumber, String authHeader) {
        return webClient.get()
                .uri("/orders/tracking/{trackingNumber}", trackingNumber)
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("{\"error\":\"ORDER_NOT_FOUND_OR_FORBIDDEN\"}"));
    }
}

package org.example.chatbot.Agents;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class OrderClient {

    private final WebClient webClient;

    // IMPORTANT : ce Builder doit être @LoadBalanced (via WebClientConfig)
    public OrderClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://order-service").build();
    }

    // ✅ GET /orders/by-user/{userId}
    public Mono<String> getOrdersByUser(Long userId, String authHeader) {
        return webClient.get()
                .uri("/orders/by-user/{userId}", userId)
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new RuntimeException("ORDER_4XX_FORBIDDEN_OR_BAD_REQUEST")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> Mono.error(new RuntimeException("ORDER_5XX_SERVICE_DOWN")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
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
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new RuntimeException("ORDER_NOT_FOUND_OR_FORBIDDEN")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> Mono.error(new RuntimeException("ORDER_5XX_SERVICE_DOWN")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
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
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new RuntimeException("ORDER_NOT_FOUND_OR_FORBIDDEN")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> Mono.error(new RuntimeException("ORDER_5XX_SERVICE_DOWN")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
    }
}

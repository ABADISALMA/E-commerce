package org.example.chatbot.Agents;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(WebClient.Builder lbBuilder) {
        this.webClient = lbBuilder.baseUrl("http://PRODUCT-SERVICE").build();
    }

    // ✅ GET /products
    public Mono<String> getAllProductsJson(String authHeader) {
        return webClient.get()
                .uri("/products")
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("{\"error\":\"PRODUCT_SERVICE_DOWN_OR_FORBIDDEN\"}"));
    }

    // ✅ GET /products/{id}
    public Mono<String> getProductById(Long id, String authHeader) {
        return webClient.get()
                .uri("/products/{id}", id)
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("{\"error\":\"PRODUCT_NOT_FOUND_OR_FORBIDDEN\"}"));
    }
}

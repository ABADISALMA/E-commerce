package org.example.chatbot.Agents;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ProductClient {

    private final WebClient webClient;

    // IMPORTANT : ce Builder doit être celui @LoadBalanced (via WebClientConfig)
    public ProductClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://product-service").build();
    }

    public Mono<String> getAllProductsJson(String authHeader) {
        return webClient.get()
                .uri("/products")
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new RuntimeException("PRODUCT_4XX_FORBIDDEN_OR_BAD_REQUEST")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> Mono.error(new RuntimeException("PRODUCT_5XX_SERVICE_DOWN")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
    }

    public Mono<String> getProductById(Long id, String authHeader) {
        return webClient.get()
                .uri("/products/{id}", id)
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new RuntimeException("PRODUCT_NOT_FOUND_OR_FORBIDDEN")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> Mono.error(new RuntimeException("PRODUCT_5XX_SERVICE_DOWN")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
    }
}

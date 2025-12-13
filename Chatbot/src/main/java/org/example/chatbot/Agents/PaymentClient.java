package org.example.chatbot.Agents;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PaymentClient {
    private final WebClient webClient;

    public PaymentClient(WebClient.Builder lbBuilder) {
        this.webClient = lbBuilder.baseUrl("http://PAYMENT-SERVICE").build();
    }

    public Mono<String> getPaymentsByUser(Long userId, String authHeader) {
        return webClient.get()
                .uri("/payments/user/{id}", userId) // adapte selon tes routes
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("{\"error\":\"SERVICE_DOWN_OR_NOT_FOUND\"}"));

    }
}


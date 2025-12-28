package org.example.chatbot.Agents;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PaymentClient {

    private final WebClient webClient;

    // IMPORTANT : ce Builder doit être @LoadBalanced (via WebClientConfig)
    public PaymentClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://payment-service").build();
    }

    public Mono<String> getPaymentsByUser(Long userId, String authHeader) {
        return webClient.get()
                .uri("/payments/user/{id}", userId) // adapte si besoin
                .headers(h -> {
                    if (authHeader != null && !authHeader.isBlank()) {
                        h.set("Authorization", authHeader);
                    }
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new RuntimeException("PAYMENT_4XX_FORBIDDEN_OR_BAD_REQUEST")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        r -> Mono.error(new RuntimeException("PAYMENT_5XX_SERVICE_DOWN")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5));
    }
}

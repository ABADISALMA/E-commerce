package org.example.chatbot.Agents;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PaymentCient {
    private final WebClient webClient;

    public PaymentCient(WebClient.Builder lbBuilder) {
        this.webClient = lbBuilder.baseUrl("http://PAYMENT-SERVICE").build(); // nom Eureka
    }

    public Mono<String> getPaymentsByUser(Long userId) {
        return webClient.get()
                .uri("/orders/user/{id}", userId)
                .retrieve()
                .bodyToMono(String.class);
    }
}


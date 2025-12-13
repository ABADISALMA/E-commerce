package org.example.chatbot.Agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChatOrchestrator {

    private final ChatClient chatClient;
    private final ProductClient productClient;
    private final OrderClient orderClient;
    private final LivraisonClient livraisonClient;
    private final PaymentClient paymentClient;

    public ChatOrchestrator(
            ChatClient.Builder builder,
            ProductClient productClient,
            OrderClient orderClient,
            LivraisonClient livraisonClient,
            PaymentClient paymentClient
    ) {
        this.chatClient = builder.build();
        this.productClient = productClient;
        this.orderClient = orderClient;
        this.livraisonClient = livraisonClient;
        this.paymentClient = paymentClient;
    }

    public Mono<String> chatOnce(String message , String authHeader) {
        String m = message.toLowerCase();

        // 1) règles simples (routing)
        if (m.contains("produit") || m.contains("prix") || m.contains("stock") || m.contains("catalogue")) {
            return productClient.getAllProductsJson(authHeader)
                    .map(json -> answerWithContext(message, "PRODUITS_JSON", json))
                    .onErrorResume(e -> Mono.just("❌ Produit-service indisponible: " + e.getMessage()));

        }

        if (m.contains("commande") || m.contains("order")) {
            return orderClient.getOrdersByUser(1L, authHeader)
                    .map(json -> answerWithContext(message, "ORDERS_JSON", json))
                    .onErrorResume(e -> Mono.just("❌ Je n’arrive pas à récupérer vos commandes (endpoint non trouvé ou service indisponible)."));
        }


        if (m.contains("livraison") || m.contains("suivi") || m.contains("tracking")) {

            String tracking = extractTracking(message);
            if (tracking == null) {
                return Mono.just("Donne-moi le numéro de suivi (ex: suivi ABC123).");
            }

            return livraisonClient.trackByNumber(tracking, authHeader)
                    .map(json -> answerWithContext(message, "LIVRAISON_JSON", json));
        }


        if (m.contains("paiement") || m.contains("payment") || m.contains("facture")) {
            return paymentClient.getPaymentsByUser(1L, authHeader)
                    .map(json -> answerWithContext(message, "PAYMENTS_JSON", json));
        }

        // 2) fallback général
        return Mono.just(chatClient.prompt()
                .system("Tu es un assistant e-commerce. Si tu manques d'infos, pose une question.")
                .user(message)
                .call()
                .content());
    }

    private String answerWithContext(String userMessage, String label, String json) {
        return chatClient.prompt()
                .system("""
            Tu es un assistant e-commerce.
            Réponds uniquement à partir des données JSON fournies.
            Si la donnée n’existe pas, dis-le et demande une précision.
        """)
                .user("""
            Question: %s

            %s:
            %s
        """.formatted(userMessage, label, json))
                .call()
                .content();
    }
    private String extractTracking(String text) {
        // prend le dernier “mot” de la phrase (simple pour tests)
        String[] parts = text.trim().split("\\s+");
        return parts.length > 0 ? parts[parts.length - 1] : null;
    }

}

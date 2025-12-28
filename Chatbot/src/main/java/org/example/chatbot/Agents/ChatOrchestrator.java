package org.example.chatbot.Agents;

import org.example.chatbot.rag.LivraisonRagHelper;
import org.example.chatbot.rag.OrderRagHelper;
import org.example.chatbot.rag.PaymentRagHelper;
import org.example.chatbot.rag.ProductRagHelper;
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

    private final ProductRagHelper productRagHelper;
    private final OrderRagHelper orderRagHelper;
    private final PaymentRagHelper paymentRagHelper;
    private final LivraisonRagHelper livraisonRagHelper;

    public ChatOrchestrator(
            ChatClient.Builder builder,
            ProductClient productClient,
            OrderClient orderClient,
            LivraisonClient livraisonClient,
            PaymentClient paymentClient,
            ProductRagHelper productRagHelper,
            OrderRagHelper orderRagHelper,
            PaymentRagHelper paymentRagHelper,
            LivraisonRagHelper livraisonRagHelper
    ) {
        this.chatClient = builder.build();
        this.productClient = productClient;
        this.orderClient = orderClient;
        this.livraisonClient = livraisonClient;
        this.paymentClient = paymentClient;

        this.productRagHelper = productRagHelper;
        this.orderRagHelper = orderRagHelper;
        this.paymentRagHelper = paymentRagHelper;
        this.livraisonRagHelper = livraisonRagHelper;
    }

    public Mono<String> chatOnce(String message, String authHeader) {
        String m = message.toLowerCase();

        // 1) PRODUITS
        if (m.contains("produit") || m.contains("prix") || m.contains("stock") || m.contains("catalogue")) {
            return productClient.getAllProductsJson(authHeader)
                    .map(json -> {
                        String smallJson = productRagHelper.topKProductsJson(json, message, 8);
                        return answerWithContext(message, "PRODUITS_JSON_TOPK", smallJson);
                    })
                    .onErrorResume(e -> Mono.just(mapServiceError(e.getMessage(), "produits")));
        }

        // 2) COMMANDES
        if (m.contains("commande") || m.contains("order")) {
            return orderClient.getOrdersByUser(1L, authHeader)
                    .map(json -> {
                        String smallJson = orderRagHelper.topKOrdersJson(json, message, 8);
                        return answerWithContext(message, "ORDERS_JSON_TOPK", smallJson);
                    })
                    .onErrorResume(e -> Mono.just(mapServiceError(e.getMessage(), "commandes")));
        }

        // 3) LIVRAISON / TRACKING
        if (m.contains("livraison") || m.contains("suivi") || m.contains("tracking")) {
            String tracking = extractTracking(message);
            if (tracking == null || tracking.length() < 3) {
                return Mono.just("Donne-moi le numéro de suivi (ex: suivi ABC123).");
            }

            return livraisonClient.trackByNumber(tracking, authHeader)
                    .map(json -> {
                        String smallJson = livraisonRagHelper.topKLivraisonsJson(json, message, 5);
                        return answerWithContext(message, "LIVRAISON_JSON", smallJson);
                    })
                    .onErrorResume(e -> Mono.just(mapServiceError(e.getMessage(), "livraison")));
        }

        // 4) PAIEMENT
        if (m.contains("paiement") || m.contains("payment") || m.contains("facture")) {
            return paymentClient.getPaymentsByUser(1L, authHeader)
                    .map(json -> {
                        String smallJson = paymentRagHelper.topKPaymentsJson(json, message, 8);
                        return answerWithContext(message, "PAYMENTS_JSON_TOPK", smallJson);
                    })
                    .onErrorResume(e -> Mono.just(mapServiceError(e.getMessage(), "paiement")));
        }

        // 5) fallback
        return Mono.just(chatClient.prompt()
                .system("Tu es un assistant e-commerce. Si tu manques d'infos, pose une question.")
                .user(message)
                .call()
                .content());
    }

    private String mapServiceError(String rawMsg, String serviceName) {
        String msg = rawMsg == null ? "" : rawMsg;
        if (msg.contains("4XX")) {
            return "❌ Accès refusé au service " + serviceName + " (token manquant/expiré) ou requête invalide.";
        }
        if (msg.contains("5XX")) {
            return "❌ Service " + serviceName + " indisponible (503/5xx). Réessaie dans quelques secondes.";
        }
        return "❌ Erreur service " + serviceName + ": " + msg;
    }

    private String answerWithContext(String userMessage, String label, String json) {
        return chatClient.prompt()
                .system("""
                        Tu es un assistant e-commerce.
                        Réponds uniquement à partir des données JSON fournies.
                        Si la donnée n’existe pas, dis-le et demande une précision.
                        Donne une réponse courte et claire.
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
        String[] parts = text.trim().split("\\s+");
        return parts.length > 0 ? parts[parts.length - 1] : null;
    }
}

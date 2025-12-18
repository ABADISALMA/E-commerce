package com.example.Payment_service.services;

import com.example.Payment_service.client.OrderClient;
import com.example.Payment_service.dto.OrderResponse;
import com.example.Payment_service.entities.Payment;
import com.example.Payment_service.entities.PaymentMethod;
import com.example.Payment_service.enums.Status;
import com.example.Payment_service.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Map<String, PaymentStrategy> strategies;
    private final OrderClient orderClient;

    public Payment process(Long orderId, Double amount, PaymentMethod method) {

        System.out.println("💳 ========================================");
        System.out.println("💳 Début du processus de paiement");
        System.out.println("💳 Order ID: " + orderId);
        System.out.println("💳 Montant: " + amount + " DH");
        System.out.println("💳 Méthode: " + method);
        System.out.println("💳 ========================================");

        // ✅ 1. Vérifier que la commande existe
        OrderResponse order;
        try {
            System.out.println("🔍 Appel à ORDER-SERVICE pour récupérer la commande...");
            order = orderClient.getOrderById(orderId);

            if (order == null) {
                System.out.println("❌ La réponse de ORDER-SERVICE est null");
                throw new IllegalArgumentException("Order ID " + orderId + " n'existe pas.");
            }

            System.out.println("✅ Commande trouvée:");
            System.out.println("   - ID: " + order.getId());
            System.out.println("   - Tracking: " + order.getTrackingNumber());
            System.out.println("   - Statut: " + order.getStatus());
            System.out.println("   - Montant: " + order.getTotalAmount() + " DH");
            System.out.println("   - User ID: " + order.getUserId());

        } catch (HttpClientErrorException e) {
            System.out.println("❌ Erreur HTTP lors de l'appel à ORDER-SERVICE:");
            System.out.println("   - Code: " + e.getStatusCode());
            System.out.println("   - Body: " + e.getResponseBodyAsString());
            System.out.println("   - Message: " + e.getMessage());
            throw new IllegalArgumentException(
                    "Order Service Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());

        } catch (ResourceAccessException e) {
            System.out.println("❌ Impossible de joindre ORDER-SERVICE:");
            System.out.println("   - Message: " + e.getMessage());
            throw new IllegalArgumentException("Service de commandes indisponible (Connection refused).");

        } catch (Exception e) {
            System.out.println("❌ Erreur inattendue dans PaymentService:");
            System.out.println("   - Type: " + e.getClass().getName());
            System.out.println("   - Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur interne lors du traitement du paiement: " + e.getMessage());
        }

        // ✅ 2. Vérifier que la commande n'est pas déjà payée ou annulée
        if (!order.getStatus().equals("CREATED")) {
            System.out.println("❌ Statut invalide: " + order.getStatus());
            throw new IllegalStateException(
                    "Paiement impossible : la commande est déjà " + order.getStatus());
        }

        // ✅ 3. Vérifier le montant
        if (!order.getTotalAmount().equals(amount)) {
            System.out.println("❌ Montant invalide:");
            System.out.println("   - Attendu: " + order.getTotalAmount() + " DH");
            System.out.println("   - Reçu: " + amount + " DH");
            throw new IllegalArgumentException(
                    "Montant invalide. Attendu: " + order.getTotalAmount() + " DH, Reçu: " + amount + " DH");
        }

        // ✅ 4. Vérifier que la stratégie de paiement existe
        System.out.println("🔍 Recherche de la stratégie de paiement: " + method.name());
        PaymentStrategy strategy = strategies.get(method.name());
        if (strategy == null) {
            System.out.println("❌ Stratégies disponibles: " + strategies.keySet());
            throw new IllegalArgumentException("Méthode de paiement non supportée: " + method);
        }
        System.out.println("✅ Stratégie trouvée: " + strategy.getClass().getSimpleName());

        // ✅ 5. Exécuter la stratégie de paiement
        System.out.println("💳 Traitement du paiement...");
        Payment result = strategy.processPayment(orderId, amount);
        result.setPaymentDate(LocalDateTime.now());
        System.out.println("✅ Paiement traité - Statut: " + result.getStatus());

        // ✅ 6. Enregistrer le paiement
        Payment savedPayment = paymentRepository.save(result);
        System.out.println("✅ Paiement enregistré avec ID: " + savedPayment.getId());

        // ✅ 7. Mettre à jour le statut de la commande SEULEMENT si le paiement a réussi
        if (result.getStatus() == Status.SUCCESS) {
            try {
                System.out.println("🔄 Mise à jour du statut de la commande vers PAID...");
                orderClient.updateOrderStatus(orderId, "PAID");
                System.out.println("✅ Statut de la commande mis à jour: PAID");
            } catch (Exception e) {
                System.out.println("⚠️ Erreur lors de la mise à jour du statut:");
                System.out.println("   - " + e.getMessage());
                // Le paiement est enregistré mais le statut de la commande n'a pas été mis à
                // jour
            }
        }

        System.out.println("💳 ========================================");
        System.out.println("💳 Paiement terminé avec succès");
        System.out.println("💳 ========================================");

        return savedPayment;
    }
}
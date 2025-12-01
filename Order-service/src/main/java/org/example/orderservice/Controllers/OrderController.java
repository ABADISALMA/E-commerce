package org.example.orderservice.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.entities.Order;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService orderService;

    // -----------------------------------------
    // ➕ POST - Ajouter une commande
    // -----------------------------------------
    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@RequestBody Order order) {
        try {
            Order saved = orderService.createOrder(order);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la création : " + e.getMessage());
        }
    }

    // -----------------------------------------
    // 🔍 GET - Afficher toutes les commandes
    // -----------------------------------------
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }

    // -----------------------------------------
    // 🔍 GET - Trouver par ID
    // -----------------------------------------
    @GetMapping("/by-id/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {

        Optional<Order> order = orderService.getById(id);

        return order.isPresent()
                ? ResponseEntity.ok(order.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Commande introuvable !");
    }

    // -----------------------------------------
    // 🔍 GET - Toutes les commandes d'un utilisateur
    // -----------------------------------------
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    // -----------------------------------------
    // ✏️ PUT - Modifier le statut
    // -----------------------------------------
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);

            Order updated = orderService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Statut invalide ! Utilise : CREATED, PAID, CANCELLED");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Commande introuvable !");
        }
    }

    // -----------------------------------------
    // 🔍 ➕ IMPORTANT : Récupérer par trackingNumber
    // -----------------------------------------
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<?> getOrderByTracking(@PathVariable String trackingNumber) {

        Optional<Order> order = orderService.getByTrackingNumber(trackingNumber);

        return order.isPresent()
                ? ResponseEntity.ok(order.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Aucune commande trouvée avec ce numéro de suivi !");
    }

    // -----------------------------------------
    // 🗑 DELETE - Supprimer une commande
    // -----------------------------------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {

        if (orderService.getById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Commande introuvable !");
        }

        orderService.delete(id);

        return ResponseEntity.ok("Commande supprimée avec succès !");
    }
}

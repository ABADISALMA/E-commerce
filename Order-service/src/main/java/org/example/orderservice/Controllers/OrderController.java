package org.example.orderservice.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.entities.Order;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.security.JwtUtil;
import org.example.orderservice.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
// ❌ Retirer @CrossOrigin (géré dans SecurityConfig)
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    // ➕ POST - Créer une commande (USER, ADMIN, SUPERADMIN)
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> addOrder(@RequestHeader("Authorization") String authHeader,
                                      @RequestBody Order order) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token); // ✅ récupéré du JWT

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token invalide : userId manquant !");
            }

            order.setUserId(userId); // 🔥 On force le userId depuis le token
            Order saved = orderService.createOrder(order);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la création : " + e.getMessage());
        }
    }


    // 🔍 GET - Toutes les commandes (ADMIN, SUPERADMIN seulement)
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }

    // 🔍 GET - Par ID (tous les authentifiés)
    @GetMapping("/by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getById(id);
        return order.isPresent()
                ? ResponseEntity.ok(order.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Commande introuvable !");
    }

    // 🔍 GET - Par utilisateur (tous les authentifiés)
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    // ✏️ PUT - Modifier le statut (ADMIN, SUPERADMIN seulement)
    @PutMapping("/update-status/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
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

    // 🔍 GET - Par trackingNumber (tous les authentifiés)
    @GetMapping("/tracking/{trackingNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> getOrderByTracking(@PathVariable String trackingNumber) {
        Optional<Order> order = orderService.getByTrackingNumber(trackingNumber);
        return order.isPresent()
                ? ResponseEntity.ok(order.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Aucune commande trouvée avec ce numéro de suivi !");
    }

    // 🗑 DELETE - Supprimer (ADMIN, SUPERADMIN seulement)
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        if (orderService.getById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Commande introuvable !");
        }
        orderService.delete(id);
        return ResponseEntity.ok("Commande supprimée avec succès !");
    }
}
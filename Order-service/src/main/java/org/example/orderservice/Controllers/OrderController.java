package org.example.orderservice.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.OrdreRequest;
import org.example.orderservice.entities.Order;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.enums.StatutColis;
import org.example.orderservice.repositories.OrderRepository;
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
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;

    // ===============================
    // ➕ CREATE ORDER
    // ===============================
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> addOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Order order) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token invalide");
        }

        order.setUserId(userId);
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    // ===============================
    // 🔍 GET ALL (ADMIN)
    // ===============================
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    // ===============================
    // 🔍 GET BY ID
    // ===============================
    @GetMapping("/by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        return orderService.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Commande introuvable")
                );
    }


    // ===============================
    // 🔍 GET BY USER
    // ===============================
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<List<Order>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    // ===============================
    // ✏️ UPDATE PAYMENT STATUS
    // ===============================
    @PutMapping("/update-status/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);
            return ResponseEntity.ok(orderService.updateStatus(id, newStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Statut invalide : CREATED, PAID, CANCELLED");
        }
    }

    // ===============================
    // 🚚 UPDATE DELIVERY INFO
    // ===============================
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> updateOrder(
            @PathVariable Long id,
            @RequestBody OrdreRequest request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // Unicité trackingNumber
        if (!order.getTrackingNumber().equals(request.getTrackingNumber())
                && orderRepository.existsByTrackingNumber(request.getTrackingNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Tracking number déjà utilisé");
        }

        order.setTrackingNumber(request.getTrackingNumber());
        order.setAdresseLivraison(request.getAdresseLivraison());

        try {
            order.setStatut(StatutColis.valueOf(request.getStatut()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Statut colis invalide");
        }

        return ResponseEntity.ok(orderRepository.save(order));
    }

    // ===============================
    // 🔍 GET BY TRACKING
    // ===============================
    @GetMapping("/tracking/{trackingNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> getByTracking(@PathVariable String trackingNumber) {

        Optional<Order> order = orderService.getByTrackingNumber(trackingNumber);

        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Commande introuvable");
        }

        return ResponseEntity.ok(order.get());
    }


    // ===============================
    // 🗑 DELETE
    // ===============================
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        if (orderService.getById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Commande introuvable");
        }

        orderService.delete(id);
        return ResponseEntity.ok("Commande supprimée");
    }
}

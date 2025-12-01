package org.example.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.entities.Order;
import org.example.orderservice.entities.OrderItem;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductClient productClient;


    // ===============================
    // 🔵 CREATE ORDER
    // ===============================
    public Order createOrder(Order order) {

        order.setOrderDate(LocalDateTime.now());
        if (order.getStatus() == null) order.setStatus(OrderStatus.CREATED);
        if (order.getTrackingNumber() == null || order.getTrackingNumber().isBlank()) {
            order.setTrackingNumber("TRK-" + System.currentTimeMillis());
        }

        double total = 0.0;

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);

                // 🔥 Récupérer le prix via Product-service
                Double unitPrice = productClient.getProductPrice(item.getProductId());
                item.setUnitPrice(unitPrice);

                double lineTotal = unitPrice * item.getQuantity();
                item.setLineTotal(lineTotal);

                total += lineTotal;
            }
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }


    public Optional<Order> getByTrackingNumber(String trackingNumber) {
        return orderRepository.findByTrackingNumber(trackingNumber);
    }
    // ===============================
    // 🔵 GET BY ID
    // ===============================
    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    // ===============================
    // 🔵 GET ALL
    // ===============================
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    // ===============================
    // 🔵 GET ORDERS BY USER
    // ===============================
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // ===============================
    // 🔵 UPDATE STATUS ONLY
    // ===============================
    public Order updateStatus(Long id, OrderStatus newStatus) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);

        return orderRepository.save(order);
    }

    // ===============================
    // 🔵 UPDATE ORDER (SANS RE-CALCUL COMPLET)
    // ===============================
    public Order updateOrder(Long id, Order updated) {

        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existing.setTrackingNumber(updated.getTrackingNumber());
        existing.setAdresseLivraison(updated.getAdresseLivraison());
        existing.setUserId(updated.getUserId());
        existing.setStatus(updated.getStatus());

        // Mise à jour des items
        if (updated.getItems() != null) {
            existing.getItems().clear();

            double total = 0.0;

            for (OrderItem item : updated.getItems()) {
                item.setOrder(existing);

                double lineTotal = item.getUnitPrice() * item.getQuantity();
                item.setLineTotal(lineTotal);

                total += lineTotal;

                existing.getItems().add(item);
            }

            existing.setTotalAmount(total);
        }

        return orderRepository.save(existing);
    }

    // ===============================
    // 🔴 DELETE ORDER
    // ===============================
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}

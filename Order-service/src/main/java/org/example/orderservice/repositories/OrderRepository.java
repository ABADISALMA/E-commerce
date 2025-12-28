package org.example.orderservice.repositories;

import org.example.orderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    Optional<Order> findByTrackingNumber(String trackingNumber);

    boolean existsByTrackingNumber(String trackingNumber);
}

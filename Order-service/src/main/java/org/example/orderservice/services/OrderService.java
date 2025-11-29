package org.example.orderservice.services;

import org.example.orderservice.entities.Order;
import org.example.orderservice.entities.OrderItem;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        // calcul du total
        double total = 0.0;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {//calcule pris totale
                item.setOrder(order);//orderitem
                double lineTotal = item.getUnitPrice() * item.getQuantity();
                item.setLineTotal(lineTotal);
                total += lineTotal;
            }
        }
        order.setTotalAmount(total);

        return orderRepository.save(order);//sauvegarde oredr+items
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    public Order updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

}
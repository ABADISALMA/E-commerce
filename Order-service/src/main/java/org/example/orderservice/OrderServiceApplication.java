package org.example.orderservice;

import org.example.orderservice.entities.Order;
import org.example.orderservice.entities.OrderItem;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.repositories.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(OrderRepository orderRepository) {
        return args -> {

            orderRepository.deleteAll();

            // ===== Commande 1 =====
            Order order1 = Order.builder()
                    .userId(1L)
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.CREATED)
                    .totalAmount(0.0)
                    .build();

            List<OrderItem> items1 = new ArrayList<>();

            OrderItem item11 = OrderItem.builder()
                    .order(order1)       // très important !
                    .productId(1L)
                    .quantity(2)
                    .unitPrice(1299.99)
                    .lineTotal(2 * 1299.99)
                    .build();

            OrderItem item12 = OrderItem.builder()
                    .order(order1)
                    .productId(3L)
                    .quantity(1)
                    .unitPrice(349.99)
                    .lineTotal(349.99)
                    .build();

            items1.add(item11);
            items1.add(item12);
            order1.setItems(items1);

            double total1 = items1.stream()
                    .mapToDouble(OrderItem::getLineTotal)
                    .sum();
            order1.setTotalAmount(total1);

            orderRepository.save(order1); // ⇐ ça sauve Order + OrderItems

            // ===== Commande 2 =====
            Order order2 = Order.builder()
                    .userId(2L)
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.PAID)
                    .totalAmount(0.0)
                    .build();

            List<OrderItem> items2 = new ArrayList<>();

            OrderItem item21 = OrderItem.builder()
                    .order(order2)
                    .productId(2L)
                    .quantity(1)
                    .unitPrice(1199.99)
                    .lineTotal(1199.99)
                    .build();

            OrderItem item22 = OrderItem.builder()
                    .order(order2)
                    .productId(5L)
                    .quantity(3)
                    .unitPrice(149.99)
                    .lineTotal(3 * 149.99)
                    .build();

            items2.add(item21);
            items2.add(item22);
            order2.setItems(items2);

            double total2 = items2.stream()
                    .mapToDouble(OrderItem::getLineTotal)
                    .sum();
            order2.setTotalAmount(total2);

            orderRepository.save(order2);

            orderRepository.findAll().forEach(o ->
                    System.out.println("Order ID=" + o.getId() + " total=" + o.getTotalAmount())
            );
        };
    }
}

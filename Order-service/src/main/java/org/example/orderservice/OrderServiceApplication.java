package org.example.orderservice;

import org.example.orderservice.entities.Order;
import org.example.orderservice.entities.OrderItem;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.enums.StatutColis;
import org.example.orderservice.repositories.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
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
                    .statut(StatutColis.EN_PREPARATION)
                    .adresseLivraison("Casablanca, Maroc")
                    .trackingNumber("TRK-0001")
                    .build();

            List<OrderItem> items1 = new ArrayList<>();

            OrderItem item11 = OrderItem.builder()
                    .order(order1)
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

            order1.setTotalAmount(
                    items1.stream().mapToDouble(OrderItem::getLineTotal).sum()
            );

            orderRepository.save(order1);

            // ===== Commande 2 =====
            Order order2 = Order.builder()
                    .userId(2L)
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.PAID)
                    .statut(StatutColis.EN_COURS_DE_LIVRAISON)
                    .adresseLivraison("Rabat, Maroc")
                    .trackingNumber("TRK-0002")
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

            order2.setTotalAmount(
                    items2.stream().mapToDouble(OrderItem::getLineTotal).sum()
            );

            orderRepository.save(order2);

            orderRepository.findAll().forEach(o ->
                    System.out.println(
                            "Order ID=" + o.getId() +
                                    " | tracking=" + o.getTrackingNumber() +
                                    " | statut=" + o.getStatut() +
                                    " | total=" + o.getTotalAmount()
                    )
            );
        };
    }
}

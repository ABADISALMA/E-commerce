package com.example.livraison_service.services;

import com.example.livraison_service.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderServiceClient {
            
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderDTO getOrdreByTracking(String trackingNumber) {

        // Récupération du microservice ORDER-SERVICE
        List<ServiceInstance> instances = discoveryClient.getInstances("ORDER-SERVICE");
        if (instances.isEmpty()) {
            throw new RuntimeException("ORDER-SERVICE non disponible !");
        }

        String baseUrl = instances.get(0).getUri().toString();

        // Appel REST
        OrderDTO order = restTemplate.getForObject(
                baseUrl + "/orders/tracking/" + trackingNumber,
                OrderDTO.class
        );

        if (order == null)
            throw new RuntimeException("Commande introuvable !");

        return order;
    }
}

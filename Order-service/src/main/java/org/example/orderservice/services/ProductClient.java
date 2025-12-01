package org.example.orderservice.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClient {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate = new RestTemplate();

    public Double getProductPrice(Long productId) {
        // Récupérer les instances disponibles du Product-service
        List<ServiceInstance> instances = discoveryClient.getInstances("PRODUCT-SERVICE");
        if (instances.isEmpty()) {
            throw new RuntimeException("Product-service non disponible !");
        }

        String baseUrl = instances.get(0).getUri().toString();

        // Appel REST pour récupérer le produit
        ProductDto product = restTemplate.getForObject(
                baseUrl + "/products/" + productId, ProductDto.class);

        if (product == null) throw new RuntimeException("Produit introuvable !");
        return product.getPrice();
    }

    // DTO pour mapper la réponse du Product-service
    @Data
    public static class ProductDto {
        private Long id;
        private String name;
        private Double price;
    }
}

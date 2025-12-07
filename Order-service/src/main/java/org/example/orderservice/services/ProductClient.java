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
    private final RestTemplate restTemplate;
    public Double getProductPrice(Long productId) {

        List<ServiceInstance> instances = discoveryClient.getInstances("PRODUCT-SERVICE");

        if (instances.isEmpty()) {
            throw new RuntimeException("Product-service non disponible !");
        }

        String baseUrl = instances.get(0).getUri().toString();

        ProductDto product = restTemplate.getForObject(
                baseUrl + "/products/" + productId,
                ProductDto.class
        );

        if (product == null) {
            throw new RuntimeException("Produit introuvable !");
        }

        return product.getPrice();
    }

    @Data
    public static class ProductDto {
        private Long id;
        private String name;
        private Double price;
    }
}

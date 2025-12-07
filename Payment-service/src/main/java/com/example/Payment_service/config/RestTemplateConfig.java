package com.example.Payment_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced  // ⚠️ TRÈS IMPORTANT pour résoudre ORDER-SERVICE via Eureka
    public RestTemplate restTemplate(JwtPropagationInterceptor jwtInterceptor) {
        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(jwtInterceptor);
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }
}
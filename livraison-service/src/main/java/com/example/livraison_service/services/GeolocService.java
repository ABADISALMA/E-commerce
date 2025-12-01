package com.example.livraison_service.services;

import com.example.livraison_service.dto.GeoPositionDTO;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeolocService {

    private final WebClient webClient;

    public GeolocService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "spring-boot-app")
                .build();
    }

    public GeoPositionDTO geocode(String adresse) {

        NominatimResult[] results = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", adresse)
                        .queryParam("format", "json")
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .bodyToMono(NominatimResult[].class)
                .block();

        if (results != null && results.length > 0) {
            return new GeoPositionDTO(
                    results[0].getLat(),
                    results[0].getLon()
            );
        }

        return null;
    }

    @Data
    private static class NominatimResult {
        private String lat;
        private String lon;
    }
}

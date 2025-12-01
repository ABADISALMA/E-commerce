package com.example.livraison_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponseDTO {
    private OrderDTO orderDTO;
    private GeoPositionDTO position;
}
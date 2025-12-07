package com.example.Payment_service.dto;

import lombok.Data;


    @Data
    public class OrderResponse {
        private Long id;
        private String trackingNumber;
        private String status;  // CREATED, PAID, CANCELLED
        private Double totalAmount;
        private Long userId;
        private String adresseLivraison;
    }


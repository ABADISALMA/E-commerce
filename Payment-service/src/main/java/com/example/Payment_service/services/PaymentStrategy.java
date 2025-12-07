package com.example.Payment_service.services;

import com.example.Payment_service.entities.Payment;

public interface PaymentStrategy {
    Payment processPayment(Long orderId, Double amount);
}

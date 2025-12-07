package com.example.Payment_service.services;

import com.example.Payment_service.entities.Payment;
import com.example.Payment_service.entities.PaymentMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.Payment_service.enums.Status.SUCCESS;

@Service("BINANCE")
public class BinanceStrategy implements PaymentStrategy {

    @Override
    public Payment processPayment(Long orderId, Double amount) {

        // Simulation Binance API
        System.out.println("Processing Binance Pay...");

        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setAmount(amount);
        p.setMethod(PaymentMethod.BINANCE);
        p.setStatus(SUCCESS);
        p.setPaymentDate(LocalDateTime.now());

        return p;
    }
}

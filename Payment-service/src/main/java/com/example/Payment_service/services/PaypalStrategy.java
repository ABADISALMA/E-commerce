package com.example.Payment_service.services;

import com.example.Payment_service.entities.Payment;
import com.example.Payment_service.entities.PaymentMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.Payment_service.enums.Status.SUCCESS;

@Service("PAYPAL")
public class PaypalStrategy implements PaymentStrategy {

    @Override
    public Payment processPayment(Long orderId, Double amount) {

        // Simulation API PayPal
        System.out.println("Processing PayPal payment...");

        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setAmount(amount);
        p.setMethod(PaymentMethod.PAYPAL);
        p.setStatus(SUCCESS);
        p.setPaymentDate(LocalDateTime.now());

        return p;
    }
}

package com.example.Payment_service.services;

import com.example.Payment_service.entities.Payment;
import com.example.Payment_service.entities.PaymentMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.Payment_service.enums.Status.SUCCESS;

@Service("CARD")
public class CreditCardStrategy implements PaymentStrategy {

    @Override
    public Payment processPayment(Long orderId, Double amount) {

        System.out.println("Processing Credit Card ...");

        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setAmount(amount);
        p.setMethod(PaymentMethod.CARD);
        p.setStatus(SUCCESS);
        p.setPaymentDate(LocalDateTime.now());

        return p;
    }
}

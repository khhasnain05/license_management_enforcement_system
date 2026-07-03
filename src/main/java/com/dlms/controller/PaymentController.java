package com.dlms.controller;

import com.dlms.dto.PaymentResponse;
import com.dlms.model.Payment;
import com.dlms.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applicant/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping
    public ResponseEntity<?> getMyPayments(@RequestParam Long applicationId) {
        // 1. Fetch raw entities from DB
        List<Payment> payments = paymentRepository.findByApplication_ApplicationId(applicationId);
        
        // 2. Transform Entities into clean DTOs
        List<PaymentResponse> dtoList = payments.stream().map(p -> 
            PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .transactionId(p.getTransactionId())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .paymentType(p.getPaymentType())
                .paymentDate(p.getPaymentDate())
                .build()
        ).collect(Collectors.toList());

        // 3. Return the DTO list wrapped in the "data" key your frontend expects
        return ResponseEntity.ok(Map.of("data", dtoList));
    }
}
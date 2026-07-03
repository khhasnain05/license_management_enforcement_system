package com.dlms.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String transactionId;
    private String paymentMethod;
    private Double amount;
    private String paymentType;
}
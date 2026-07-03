package com.dlms.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long paymentId;
    private String transactionId;
    private Double amount;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentType;
    private LocalDateTime paymentDate;
}
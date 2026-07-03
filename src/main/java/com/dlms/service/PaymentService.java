package com.dlms.service;

import com.dlms.model.Application;
import com.dlms.model.Payment;
import com.dlms.model.PaymentAuditLog;
import com.dlms.repository.PaymentAuditLogRepository;
import com.dlms.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAuditLogRepository auditLogRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public Payment updatePaymentSubmission(Application app, String transactionId, String paymentMethod) {
        Payment payment = paymentRepository.findTopByApplicationOrderByPaymentDateDesc(app)
                .orElseThrow(() -> new RuntimeException("No pending payment found."));
        
        payment.setTransactionId(transactionId);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("VERIFICATION_PENDING");

        payment.setRemarks(null); 
        
        return paymentRepository.save(payment);
    }

    @Transactional
    public void processOfficerDecision(Payment payment, Long officerId, String action, String remarks) {
        jdbcTemplate.update("CALL SP_PROCESS_PAYMENT_DECISION(?, ?, ?, ?)",
                payment.getPaymentId(), officerId, action, remarks);
    }

    public List<java.util.Map<String, Object>> getOfficerAuditHistory(Long officerId) {
        return auditLogRepository.findByOfficerIdOrderByTimestampDesc(officerId).stream().map(log -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            
            // Safe extraction to prevent NullPointerExceptions
            String applicantName = "Unknown";
            if (log.getPayment() != null && log.getPayment().getApplication() != null && 
                log.getPayment().getApplication().getApplicant() != null && 
                log.getPayment().getApplication().getApplicant().getUser() != null) {
                applicantName = log.getPayment().getApplication().getApplicant().getUser().getFullNameEn();
            }

            map.put("applicationId", log.getPayment().getApplication().getApplicationId());
            map.put("applicantName", applicantName);
            map.put("taskType", log.getPayment().getPaymentType()); // 'LEARNER_FEE' or 'LICENSE_FEE'
            map.put("decision", log.getActionTaken()); // 'APPROVED' or 'REJECTED'
            map.put("remarks", log.getRemarks() != null ? log.getRemarks() : "");
            map.put("timestamp", log.getTimestamp().toString());
            
            return map;
        }).toList();
    }
}
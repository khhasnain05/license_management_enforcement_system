package com.dlms.repository;

import com.dlms.model.PaymentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentAuditLogRepository extends JpaRepository<PaymentAuditLog, Long> {
    List<PaymentAuditLog> findByPayment_PaymentIdOrderByTimestampDesc(Long paymentId);
    List<PaymentAuditLog> findByOfficerIdOrderByTimestampDesc(Long officerId);
}
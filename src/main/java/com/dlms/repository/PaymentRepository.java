package com.dlms.repository;

import com.dlms.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.dlms.model.Application;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    boolean existsByApplication_ApplicationIdAndPaymentTypeAndPaymentStatus(
        Long appId, String type, String status);

    List<Payment> findByApplication_ApplicationId(Long applicationId);
    
    Optional<Payment> findTopByApplicationOrderByPaymentDateDesc(Application application);
    Optional<Payment> findByApplication_ApplicationIdAndPaymentStatus(Long applicationId, String paymentStatus);
}
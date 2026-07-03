package com.dlms.service;

import com.dlms.model.Application;
import com.dlms.model.LearnerPermit;
import com.dlms.model.Payment;
import com.dlms.model.enums.ApplicationStage;
import com.dlms.model.enums.ApplicationStatus;
import com.dlms.observer.NotificationSubject;
import com.dlms.repository.ApplicationRepository;
import com.dlms.repository.LearnerPermitRepository;
import com.dlms.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ApplicationWorkflowService {

    private final ApplicationRepository applicationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final LicenseService licenseService;
    private final LearnerPermitRepository learnerPermitRepository;
    private final NotificationSubject notificationSubject;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void rejectPayment(Long applicationId, Long officerId, String reason) {
        jdbcTemplate.update("CALL SP_WORKFLOW_REJECT_PAYMENT(?, ?, ?)",
            applicationId, officerId, reason
        );
    }

    @Transactional
    public void verifyPayment(Long applicationId, Long officerId) {
        jdbcTemplate.update("CALL SP_WORKFLOW_VERIFY_PAYMENT(?, ?)",
            applicationId, officerId
        );
    }
}
package com.dlms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "VW_FINANCIAL_RECONCILIATION")
@Data
public class FinancialReconciliationView {

    @Id
    @Column(name = "PAYMENT_ID")
    private Long paymentId;

    @Column(name = "CHALLAN_NO")
    private String challanNo;

    @Column(name = "PAYMENT_TYPE")
    private String paymentType;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;

    @Column(name = "PAYER_CNIC")
    private String payerCnic;
}
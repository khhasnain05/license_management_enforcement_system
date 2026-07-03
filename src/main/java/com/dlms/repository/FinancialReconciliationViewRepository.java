package com.dlms.repository;

import com.dlms.model.FinancialReconciliationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialReconciliationViewRepository extends JpaRepository<FinancialReconciliationView, Long> {
}
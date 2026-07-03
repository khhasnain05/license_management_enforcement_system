package com.dlms.service;

import com.dlms.model.*;
import com.dlms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final ApplicantDashboardViewRepository masterViewRepo;
    private final CenterPerformanceViewRepository performanceRepo;
    private final HighRiskDriverViewRepository riskRepo;
    private final FinancialReconciliationViewRepository financeRepo;

    public List<ApplicantDashboardView> getMasterView() { 
        return masterViewRepo.findAll(); 
    }
    
    public List<CenterPerformanceView> getPerformanceView() { 
        return performanceRepo.findAll(); 
    }
    
    public List<HighRiskDriverView> getRiskView() { 
        return riskRepo.findAll(); 
    }
    
    public List<FinancialReconciliationView> getFinanceView() { 
        return financeRepo.findAll(); 
    }
}
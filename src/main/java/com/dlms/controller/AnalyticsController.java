package com.dlms.controller;

import com.dlms.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/master")
    public ResponseEntity<?> getMaster() { 
        // FIXED: Call getMasterView() instead of getAllApplicantDashboards()
        return ResponseEntity.ok(service.getMasterView()); 
    }
    
    @GetMapping("/performance")
    public ResponseEntity<?> getPerformance() { 
        return ResponseEntity.ok(service.getPerformanceView()); 
    }
    
    @GetMapping("/high-risk")
    public ResponseEntity<?> getHighRisk() { 
        return ResponseEntity.ok(service.getRiskView()); 
    }
    
    @GetMapping("/finance")
    public ResponseEntity<?> getFinance() { 
        return ResponseEntity.ok(service.getFinanceView()); 
    }
}
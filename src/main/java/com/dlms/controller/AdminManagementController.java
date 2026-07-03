package com.dlms.controller;

import com.dlms.dto.StaffRequest;
import com.dlms.service.AdminManagementService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    @GetMapping("/staff")
    public ResponseEntity<?> getStaffList() {
        return ResponseEntity.ok(adminManagementService.getAllStaff());
    }

    @PostMapping("/staff")
    public ResponseEntity<?> createStaff(@RequestBody StaffRequest request) {
        try {
            return ResponseEntity.ok(adminManagementService.createStaff(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/staff/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable Long id, @RequestBody StaffRequest request) {
        try {
            return ResponseEntity.ok(adminManagementService.updateStaff(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/staff/{id}/suspend")
    public ResponseEntity<?> toggleSuspend(@PathVariable Long id) {
        try {
            adminManagementService.toggleSuspend(id);
            return ResponseEntity.ok().body("Status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/audit-logs")
    public ResponseEntity<List<Map<String, Object>>> getAuditLogs() {
        try {
            return ResponseEntity.ok(adminManagementService.getGlobalOfficerAuditLog());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
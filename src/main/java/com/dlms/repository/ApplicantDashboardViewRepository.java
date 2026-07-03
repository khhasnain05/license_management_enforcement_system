package com.dlms.repository;

import com.dlms.model.ApplicantDashboardView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicantDashboardViewRepository extends JpaRepository<ApplicantDashboardView, Long> {
    
    List<ApplicantDashboardView> findByDistrict(String district);
    
    List<ApplicantDashboardView> findByCnic(String cnic);
}
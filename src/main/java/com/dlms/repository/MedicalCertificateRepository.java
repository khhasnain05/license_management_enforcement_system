package com.dlms.repository;

import com.dlms.model.MedicalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificate, Long> {
    Optional<MedicalCertificate> findByApplication_ApplicationId(Long applicationId);
    Optional<MedicalCertificate> findTopByApplication_ApplicationIdOrderByCertificateIdDesc(Long applicationId);
    List<MedicalCertificate> findByStatus(String status);   
    
    List<MedicalCertificate> findByStatusAndApplication_Applicant_User_District(
            String status,
            String district
    );
 
    List<MedicalCertificate> findByStatusNotAndApplication_Applicant_User_District(
            String status,
            String district
    );
}
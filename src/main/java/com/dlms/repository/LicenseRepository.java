package com.dlms.repository;

import com.dlms.model.DrivingLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<DrivingLicense, Long> {

    Optional<DrivingLicense> findByApplication_ApplicationId(Long applicationId);

    // Police will look up by QR code data
    Optional<DrivingLicense> findByQrCodeData(String qrCodeData);

    // Get all licenses for an applicant (via application)
    List<DrivingLicense> findByApplication_Applicant_ApplicantId(Long applicantId);

    // Find active licenses only
    List<DrivingLicense> findByStatus(String status);
}
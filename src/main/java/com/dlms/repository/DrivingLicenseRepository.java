package com.dlms.repository;

import com.dlms.model.DrivingLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrivingLicenseRepository extends JpaRepository<DrivingLicense, Long> {
    
    Optional<DrivingLicense> findByLicenseNumber(String licenseNumber);
    Optional<DrivingLicense> findByApplication_ApplicationId(Long applicationId);
    @Query("SELECT d FROM DrivingLicense d WHERE CAST(d.licenseId AS string) = :query OR d.application.applicant.user.cnic = :query")
    Optional<DrivingLicense> findByIdOrCnic(@Param("query") String query);
}
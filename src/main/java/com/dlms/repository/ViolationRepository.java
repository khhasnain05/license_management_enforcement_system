package com.dlms.repository;

import com.dlms.model.TrafficViolation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<TrafficViolation, Long> {
	List<TrafficViolation> findByPoliceOfficer_PoliceOfficerIdOrderByViolationDateDesc(Long officerId);
    List<TrafficViolation> findByLicense_LicenseId(Long licenseId);
}

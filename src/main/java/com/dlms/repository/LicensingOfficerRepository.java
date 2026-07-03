package com.dlms.repository;

import com.dlms.model.LicensingOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LicensingOfficerRepository extends JpaRepository<LicensingOfficer, Long> {
    Optional<LicensingOfficer> findByUser_Email(String email);
    Optional<LicensingOfficer> findByUser_UserId(Long userId);
}

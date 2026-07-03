package com.dlms.repository;

import com.dlms.model.TrafficPoliceOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TrafficPoliceOfficerRepository extends JpaRepository<TrafficPoliceOfficer, Long> {
    Optional<TrafficPoliceOfficer> findByUser_Email(String email);
    
}
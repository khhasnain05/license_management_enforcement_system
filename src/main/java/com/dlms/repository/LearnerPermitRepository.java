package com.dlms.repository;

import com.dlms.model.LearnerPermit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LearnerPermitRepository extends JpaRepository<LearnerPermit, Long> {
    Optional<LearnerPermit> findByApplication_ApplicationId(Long applicationId);
}
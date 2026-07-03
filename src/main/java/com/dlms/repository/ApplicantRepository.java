package com.dlms.repository;

import com.dlms.model.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    Optional<Applicant> findByUser_UserId(Long userId);
    Optional<Applicant> findByUser_Email(String email);
}

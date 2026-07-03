package com.dlms.repository;

import com.dlms.model.Application;
import com.dlms.model.enums.ApplicationStatus;
import com.dlms.model.enums.ApplicationStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicant_ApplicantId(Long applicantId);
    List<Application> findByApplicationStatus(ApplicationStatus status);
    List<Application> findByReviewingOfficer_OfficerId(Long officerId);
    List<Application> findByCurrentStage(ApplicationStage stage);
    List<Application> findByCurrentStageIn(List<ApplicationStage> stages);
}
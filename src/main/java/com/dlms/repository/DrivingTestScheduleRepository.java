package com.dlms.repository;

import com.dlms.model.DrivingTestSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrivingTestScheduleRepository extends JpaRepository<DrivingTestSchedule, Long> {
    
    long countByScheduledDateAndTestCenter(LocalDate date, String testCenter);
    
    List<DrivingTestSchedule> findByScheduledDateAndStatus(LocalDate date, String status);
    
    Optional<DrivingTestSchedule> findByApplication_ApplicationId(Long applicationId);
    
    List<DrivingTestSchedule> findByTestCenterAndStatus(String testCenter, String status);
    
    List<DrivingTestSchedule> findByExaminer_OfficerId(Long officerId);

    @Query("SELECT d.scheduledDate FROM DrivingTestSchedule d WHERE d.testCenter = :testCenter AND d.status = 'SCHEDULED' GROUP BY d.scheduledDate HAVING COUNT(d) >= :limit")
    List<LocalDate> findBusyDates(@Param("testCenter") String testCenter, @Param("limit") long limit);
}
package com.dlms.service;

import com.dlms.model.Application;
import com.dlms.model.DrivingTestSchedule;
import com.dlms.observer.NotificationSubject;
import com.dlms.repository.DrivingTestScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    public static final int MAX_TESTS_PER_DAY = 30;
    private final JdbcTemplate jdbcTemplate;

    private final DrivingTestScheduleRepository drivingTestScheduleRepository;
    private final NotificationSubject notificationSubject;

    @Transactional
    public void scheduleTest(Application application, String testType, String testCenter, LocalDate testDate) {
        if (testDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Test date cannot be in the past.");
        }

        try {
            jdbcTemplate.update("CALL SP_SCHEDULE_TEST(?, ?, ?)",
                application.getApplicationId(), 
                testCenter, 
                java.sql.Date.valueOf(testDate)
            );
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public List<LocalDate> getBusyDates(String testCenter) {
        return drivingTestScheduleRepository.findBusyDates(testCenter, MAX_TESTS_PER_DAY);
    }

    public List<Map<String, Object>> getTestsByApplication(Long applicationId) {
        return drivingTestScheduleRepository.findByApplication_ApplicationId(applicationId)
                .stream().map(t -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("testType", "DRIVING");
                    map.put("status", t.getStatus());
                    map.put("scheduledDate", t.getScheduledDate());
                    return map;
                }).collect(Collectors.toList());
    }
    
    public long getRemainingSlots(String testCenter, LocalDate date) {
        // Counts how many tests are already scheduled for this specific date and center
        long count = drivingTestScheduleRepository.countByScheduledDateAndTestCenter(date, testCenter);
        
        // Returns remaining slots, ensuring it never goes below zero
        return Math.max(0, MAX_TESTS_PER_DAY - count);
    }
}
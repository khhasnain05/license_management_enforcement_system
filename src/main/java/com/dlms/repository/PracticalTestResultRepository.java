package com.dlms.repository;

import com.dlms.model.PracticalTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticalTestResultRepository extends JpaRepository<PracticalTestResult, Long> {
}
package com.dlms.repository;

import com.dlms.model.CenterPerformanceView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterPerformanceViewRepository extends JpaRepository<CenterPerformanceView, String> {
}
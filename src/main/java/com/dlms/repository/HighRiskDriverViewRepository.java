package com.dlms.repository;

import com.dlms.model.HighRiskDriverView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HighRiskDriverViewRepository extends JpaRepository<HighRiskDriverView, String> {
}
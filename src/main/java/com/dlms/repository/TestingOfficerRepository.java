package com.dlms.repository;

import com.dlms.model.TestingOfficer;
import com.dlms.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestingOfficerRepository extends JpaRepository<TestingOfficer, Long> {

    Optional<TestingOfficer> findByUser_Email(String email);

    List<TestingOfficer> findByUser_District(String district);
    
    Optional<TestingOfficer> findByUser(User user);
}
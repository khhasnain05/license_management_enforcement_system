package com.dlms.repository;

import com.dlms.model.TheoryTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheoryTestAttemptRepository extends JpaRepository<TheoryTestAttempt, Long> {

}
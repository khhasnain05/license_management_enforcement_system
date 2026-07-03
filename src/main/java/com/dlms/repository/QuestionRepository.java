package com.dlms.repository;

import com.dlms.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Custom query to fetch 5 random questions for the test
    @Query(value = "SELECT * FROM question ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 5 ROWS ONLY", nativeQuery = true)
    List<Question> findRandom5Questions();
}
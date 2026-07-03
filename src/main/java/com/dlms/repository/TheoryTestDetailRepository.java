package com.dlms.repository;

import com.dlms.model.TheoryTestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheoryTestDetailRepository extends JpaRepository<TheoryTestDetail, Long> {

}
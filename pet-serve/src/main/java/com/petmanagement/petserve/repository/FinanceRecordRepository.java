package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.FinanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, Integer> {
    Page<FinanceRecord> findByOwner_UserId(Integer userId, Pageable pageable);
}

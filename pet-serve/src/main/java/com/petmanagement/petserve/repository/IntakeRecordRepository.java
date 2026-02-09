package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.IntakeRecord;

public interface IntakeRecordRepository extends JpaRepository<IntakeRecord, Integer> {
    List<IntakeRecord> findByPet_PetIdOrderByRecordDateDesc(Integer petId, Pageable pageable);
}

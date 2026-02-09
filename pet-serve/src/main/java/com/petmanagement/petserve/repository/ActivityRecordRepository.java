package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.ActivityRecord;

public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Integer> {
    List<ActivityRecord> findByPet_PetIdOrderByRecordDateDesc(Integer petId, Pageable pageable);
}

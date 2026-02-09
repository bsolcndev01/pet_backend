package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.WeightRecord;

public interface WeightRecordRepository extends JpaRepository<WeightRecord, Integer> {
    List<WeightRecord> findTop10ByPet_PetIdOrderByRecordDateDesc(Integer petId);

    List<WeightRecord> findByPet_PetIdOrderByRecordDateDesc(Integer petId, Pageable pageable);
}

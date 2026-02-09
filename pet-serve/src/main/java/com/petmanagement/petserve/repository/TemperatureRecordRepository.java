package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.TemperatureRecord;

public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecord, Integer> {
    List<TemperatureRecord> findByPet_PetIdOrderByRecordDateDesc(Integer petId, Pageable pageable);
}

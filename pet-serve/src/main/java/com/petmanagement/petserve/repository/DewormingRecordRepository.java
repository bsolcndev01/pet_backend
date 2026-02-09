package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.DewormingRecord;

public interface DewormingRecordRepository extends JpaRepository<DewormingRecord, Integer> {
    List<DewormingRecord> findTop20ByPet_PetIdOrderByApplicationDateDesc(Integer petId);
}

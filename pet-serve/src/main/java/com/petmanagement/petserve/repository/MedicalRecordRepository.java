package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.MedicalRecord;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    List<MedicalRecord> findTop20ByPet_PetIdOrderByVisitDateDesc(Integer petId);

    List<MedicalRecord> findTop20ByVetUserIdOrderByVisitDateDesc(Integer vetUserId);

    List<MedicalRecord> findTop20ByVetUserIdAndPet_PetIdOrderByVisitDateDesc(Integer vetUserId, Integer petId);
}

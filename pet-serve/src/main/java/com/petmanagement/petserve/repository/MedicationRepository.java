package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.Medication;

public interface MedicationRepository extends JpaRepository<Medication, Integer> {
    List<Medication> findTop20ByPet_PetIdOrderByStartDateDesc(Integer petId);

    List<Medication> findTop50ByVet_UserIdOrderByStartDateDesc(Integer vetUserId);

    List<Medication> findTop50ByVet_UserIdAndPet_PetIdOrderByStartDateDesc(Integer vetUserId, Integer petId);
}

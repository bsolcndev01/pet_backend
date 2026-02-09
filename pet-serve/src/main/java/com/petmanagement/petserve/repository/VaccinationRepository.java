package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.Vaccination;

public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {
    List<Vaccination> findTop20ByPet_PetIdOrderByVaccinationDateDesc(Integer petId);
}

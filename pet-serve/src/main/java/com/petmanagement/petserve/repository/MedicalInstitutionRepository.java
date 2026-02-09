package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.MedicalInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalInstitutionRepository extends JpaRepository<MedicalInstitution, Integer> {
}

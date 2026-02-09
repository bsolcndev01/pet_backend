package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.PetInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetInsuranceRepository extends JpaRepository<PetInsurance, Integer> {
}

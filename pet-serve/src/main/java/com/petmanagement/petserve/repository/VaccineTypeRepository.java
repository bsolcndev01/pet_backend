package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.VaccineType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineTypeRepository extends JpaRepository<VaccineType, Integer> {
}

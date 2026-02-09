package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.MedicationInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationInventoryRepository extends JpaRepository<MedicationInventory, Integer> {
}

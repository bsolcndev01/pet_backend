package com.petmanagement.petserve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.PetPlan;

public interface PetPlanRepository extends JpaRepository<PetPlan, Integer> {
    List<PetPlan> findTop50ByPet_PetIdOrderByStartDateDesc(Integer petId);
}

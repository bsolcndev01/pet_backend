package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.repository.projection.PetHealthSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    Page<Pet> findByOwner_UserId(Integer userId, Pageable pageable);
    List<Pet> findByOwner_UserId(Integer userId);

    Page<Pet> findByOwner_UserIdAndPetNameContainingIgnoreCase(Integer userId, String keyword, Pageable pageable);
    Page<Pet> findByPetNameContainingIgnoreCase(String keyword, Pageable pageable);

    Optional<Pet> findByPetIdAndOwner_UserId(Integer petId, Integer userId);

    @Query(value = """
            SELECT 
                p.pet_id AS petId,
                p.pet_name AS petName,
                p.species AS species,
                p.breed AS breed,
                p.current_weight AS currentWeight,
                p.health_status AS healthStatus,
                p.last_medical_check AS lastMedicalCheck,
                (SELECT COUNT(*) FROM medical_records mr WHERE mr.pet_id = p.pet_id) AS totalVisits,
                (SELECT COUNT(*) FROM vaccinations v WHERE v.pet_id = p.pet_id) AS vaccineCount,
                (SELECT COUNT(*) FROM deworming_records dr WHERE dr.pet_id = p.pet_id) AS dewormingCount
            FROM pets p
            WHERE p.user_id = :userId
            """, nativeQuery = true)
    List<PetHealthSummaryProjection> findHealthSummaryByUserId(@Param("userId") Integer userId);
}

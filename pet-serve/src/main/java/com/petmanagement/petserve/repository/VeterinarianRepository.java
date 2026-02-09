package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Integer> {
    Optional<Veterinarian> findByUser_UserId(Integer userId);
}

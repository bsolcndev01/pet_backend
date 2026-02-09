package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReminderTypeRepository extends JpaRepository<ReminderType, Integer> {
    Optional<ReminderType> findByTypeName(String typeName);
}

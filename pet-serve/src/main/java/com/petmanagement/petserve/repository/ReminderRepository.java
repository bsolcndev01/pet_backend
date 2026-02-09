package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Integer> {

    @Query("SELECT r FROM Reminder r WHERE r.user.userId = :userId AND r.reminderDate >= :fromDate ORDER BY r.reminderDate ASC")
    Page<Reminder> findUpcomingByUser(@Param("userId") Integer userId, @Param("fromDate") LocalDate fromDate, Pageable pageable);

    Page<Reminder> findByUser_UserId(Integer userId, Pageable pageable);

    Optional<Reminder> findBySourceTableAndSourceRecordId(String sourceTable, Integer sourceRecordId);
}

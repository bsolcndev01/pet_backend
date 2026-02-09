package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query("SELECT a FROM Appointment a WHERE a.owner.userId = :userId AND a.appointmentDate >= :fromDate ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    Page<Appointment> findUpcomingByUser(@Param("userId") Integer userId, @Param("fromDate") LocalDate fromDate, Pageable pageable);

    Page<Appointment> findByOwner_UserId(Integer userId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.vet.userId = :vetUserId AND a.appointmentDate >= :fromDate ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    Page<Appointment> findUpcomingByVet(@Param("vetUserId") Integer vetUserId, @Param("fromDate") LocalDate fromDate, Pageable pageable);

    Page<Appointment> findByVet_UserId(Integer vetUserId, Pageable pageable);
}

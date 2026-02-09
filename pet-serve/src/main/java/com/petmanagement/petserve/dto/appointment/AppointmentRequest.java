package com.petmanagement.petserve.dto.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    @NotNull
    private Integer petId;
    private Integer vetUserId;
    private Integer institutionId;
    @NotNull
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String appointmentType;
    private String reason;
    private String status;
    private String notes;
}

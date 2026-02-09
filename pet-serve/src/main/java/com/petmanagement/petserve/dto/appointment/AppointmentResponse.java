package com.petmanagement.petserve.dto.appointment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
    private Integer appointmentId;
    private Integer petId;
    private String petName;
    private Integer ownerUserId;
    private String ownerUsername;
    private Integer vetUserId;
    private String vetUsername;
    private Integer institutionId;
    private String institutionName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String appointmentType;
    private String reason;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

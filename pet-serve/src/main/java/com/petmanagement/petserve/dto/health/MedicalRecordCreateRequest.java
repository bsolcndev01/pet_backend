package com.petmanagement.petserve.dto.health;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicalRecordCreateRequest {
    @NotNull
    private Integer petId;

    @NotNull
    private Integer institutionId;

    @NotNull
    private Integer vetUserId;

    @NotNull
    private LocalDate visitDate;

    @NotBlank
    private String reason;

    private String diagnosis;
    private String treatment;
    private String prescription;
    private BigDecimal cost;
    private String paymentStatus;
    private String recordStatus;
    private LocalDate followUpDate;
}

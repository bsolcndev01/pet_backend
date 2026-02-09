package com.petmanagement.petserve.dto.health;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicalRecordDto {
    private Integer id;
    private Integer petId;
    private String petName;
    private LocalDate visitDate;
    private String reason;
    private String diagnosis;
    private String treatment;
    private String prescription;
    private BigDecimal cost;
    private String paymentStatus;
    private String recordStatus;
    private LocalDate followUpDate;
    private Integer institutionId;
    private String institutionName;
    private Integer vetUserId;
    private String vetName;
}

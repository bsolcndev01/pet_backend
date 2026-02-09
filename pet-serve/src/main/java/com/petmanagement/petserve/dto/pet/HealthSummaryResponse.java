package com.petmanagement.petserve.dto.pet;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class HealthSummaryResponse {
    private Integer petId;
    private String petName;
    private String species;
    private String breed;
    private BigDecimal currentWeight;
    private String healthStatus;
    private LocalDate lastMedicalCheck;
    private Long totalVisits;
    private Long vaccineCount;
    private Long dewormingCount;
}

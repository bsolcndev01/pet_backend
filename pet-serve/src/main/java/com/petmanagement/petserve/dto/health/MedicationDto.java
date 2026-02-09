package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicationDto {
    private Integer id;
    private Integer petId;
    private String petName;
    private String drugName;
    private String dosage;
    private String frequency;
    private String route;
    private LocalDate startDate;
    private LocalDate endDate;
    private String instructions;
    private String contraindications;
    private Integer vetUserId;
    private String vetName;
    private String status;
}

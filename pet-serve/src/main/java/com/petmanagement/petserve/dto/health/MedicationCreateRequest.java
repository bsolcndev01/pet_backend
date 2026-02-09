package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicationCreateRequest {
    @NotNull
    private Integer petId;

    @NotBlank
    private String drugName;

    private String dosage;
    private String frequency;
    private String route;
    private LocalDate startDate;
    private LocalDate endDate;
    private String instructions;
    private String contraindications;
    private Integer vetUserId;
    private String status;
}

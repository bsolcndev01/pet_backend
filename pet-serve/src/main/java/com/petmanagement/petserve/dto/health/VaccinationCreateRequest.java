package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VaccinationCreateRequest {
    @NotNull
    private Integer petId;

    private Integer vaccineTypeId;

    private String vaccineName;

    @NotNull
    private Integer institutionId;

    private Integer vetId;

    @NotNull
    private LocalDate vaccinationDate;

    @NotNull
    private LocalDate nextDueDate;

    private String lotNumber;
    private String notes;
}

package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VaccinationDto {
    private Integer id;
    private Integer petId;
    private String petName;
    private Integer vaccineTypeId;
    private String vaccineName;
    private Integer institutionId;
    private String institutionName;
    private Integer vetId;
    private String vetName;
    private LocalDate vaccinationDate;
    private LocalDate nextDueDate;
    private String lotNumber;
    private String notes;
}

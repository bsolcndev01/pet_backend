package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IntakeDto {
    private Integer id;
    private Integer petId;
    private String petName;
    private LocalDate recordDate;
    private Double intakeVolume;
    private String notes;
}

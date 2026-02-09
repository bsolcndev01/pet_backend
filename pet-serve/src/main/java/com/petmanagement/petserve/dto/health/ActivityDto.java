package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityDto {
    private Integer id;
    private Integer petId;
    private String petName;
    private LocalDate recordDate;
    private Double activityLevel;
    private String notes;
}

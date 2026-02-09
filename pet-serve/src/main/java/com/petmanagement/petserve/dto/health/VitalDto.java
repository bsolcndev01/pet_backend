package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VitalDto {
    private Integer id;
    private Integer petId;
    private String petName;
    private LocalDate recordDate;
    private String metricType;
    private Double value;
    private String unit;
    private String notes;
}

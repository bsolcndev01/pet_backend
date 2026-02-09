package com.petmanagement.petserve.dto.plan;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanDto {
    private Integer planId;
    private Integer petId;
    private String petName;
    private String planType;
    private String title;
    private String target;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private LocalDateTime createdAt;
    private String createdBy;
}

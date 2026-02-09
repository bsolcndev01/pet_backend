package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VitalCreateRequest {
    @NotNull
    private Integer petId;

    @NotNull
    private String metricType; // 体重/体温/活动量/饮水进食

    @NotNull
    @Positive
    private Double value;

    @NotNull
    private LocalDate recordDate;

    private String notes;
}

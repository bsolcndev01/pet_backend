package com.petmanagement.petserve.dto.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlanRequest {
    @NotNull
    private Integer petId;

    @NotBlank
    private String planType; // 饮食 / 运动

    @NotBlank
    private String title;

    private String target;
    private String frequency;
    private String startDate;
    private String endDate;
    private String notes;
}

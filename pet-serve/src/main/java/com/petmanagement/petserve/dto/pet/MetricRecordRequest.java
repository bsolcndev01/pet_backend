package com.petmanagement.petserve.dto.pet;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MetricRecordRequest {
    @NotBlank(message = "指标类型不能为空")
    private String type;

    @NotNull(message = "数值不能为空")
    private Double value;

    @NotNull(message = "日期不能为空")
    private LocalDate recordDate;

    private String notes;
}

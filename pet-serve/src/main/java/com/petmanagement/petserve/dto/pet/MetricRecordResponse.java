package com.petmanagement.petserve.dto.pet;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricRecordResponse {
    private String type;
    private Double value;
    private LocalDate recordDate;
    private String notes;
}

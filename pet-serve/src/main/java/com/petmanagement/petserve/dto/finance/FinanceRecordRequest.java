package com.petmanagement.petserve.dto.finance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinanceRecordRequest {
    private Integer petId;
    private Integer medicalRecordId;

    @NotBlank
    private String title;

    @NotBlank
    private String recordType;

    private String category;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDate recordDate;

    private String status;

    private String notes;
}

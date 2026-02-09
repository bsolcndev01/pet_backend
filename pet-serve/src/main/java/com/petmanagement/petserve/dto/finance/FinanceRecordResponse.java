package com.petmanagement.petserve.dto.finance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FinanceRecordResponse {
    private Integer recordId;
    private Integer petId;
    private String petName;
    private Integer ownerUserId;
    private Integer medicalRecordId;
    private String title;
    private String recordType;
    private String category;
    private BigDecimal amount;
    private LocalDate recordDate;
    private String status;
    private String notes;
}

package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DewormingDto {
    private Integer id;
    private Integer petId;
    private String petName;
    private Integer productId;
    private String productName;
    private String sourceType;
    private Integer institutionId;
    private String institutionName;
    private Integer vetUserId;
    private String vetName;
    private LocalDate applicationDate;
    private LocalDate nextDueDate;
    private String dosage;
    private String notes;
}

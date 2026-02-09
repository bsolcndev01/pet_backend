package com.petmanagement.petserve.dto.health;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DewormingCreateRequest {
    @NotNull
    private Integer petId;

    private Integer productId;

    @NotNull
    private LocalDate applicationDate;

    private LocalDate nextDueDate;

    private String dosage;
    private String notes;
    /**
     * 自驱 or 医院
     */
    private String sourceType;
    private Integer institutionId;
    private Integer vetUserId;
}

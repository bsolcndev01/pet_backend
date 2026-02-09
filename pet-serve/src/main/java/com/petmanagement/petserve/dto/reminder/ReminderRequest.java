package com.petmanagement.petserve.dto.reminder;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReminderRequest {
    @NotNull
    private Integer reminderTypeId;
    @NotNull
    private Integer petId;
    private Integer userId;
    private Integer vetId;
    private Integer sourceRecordId;
    @NotNull
    private String title;
    @NotNull
    private String message;
    @NotNull
    private LocalDate dueDate;
    @NotNull
    private LocalDate reminderDate;
}

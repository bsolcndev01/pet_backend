package com.petmanagement.petserve.dto.reminder;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReminderResponse {
    private Integer reminderId;
    private Integer reminderTypeId;
    private String reminderTypeName;
    private Integer petId;
    private String petName;
    private Integer userId;
    private Integer vetId;
    private String vetName;
    private String title;
    private String message;
    private LocalDate dueDate;
    private LocalDate reminderDate;
    private Boolean completed;
}

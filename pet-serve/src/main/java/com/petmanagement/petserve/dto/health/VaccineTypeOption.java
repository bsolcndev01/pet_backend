package com.petmanagement.petserve.dto.health;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VaccineTypeOption {
    private Integer vaccineTypeId;
    private String vaccineName;
}

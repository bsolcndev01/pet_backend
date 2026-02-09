package com.petmanagement.petserve.dto.health;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VetOption {
    private Integer userId;
    private String username;
}

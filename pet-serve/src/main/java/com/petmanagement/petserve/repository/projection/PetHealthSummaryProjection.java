package com.petmanagement.petserve.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PetHealthSummaryProjection {
    Integer getPetId();
    String getPetName();
    String getSpecies();
    String getBreed();
    BigDecimal getCurrentWeight();
    String getHealthStatus();
    LocalDate getLastMedicalCheck();
    Long getTotalVisits();
    Long getVaccineCount();
    Long getDewormingCount();
}

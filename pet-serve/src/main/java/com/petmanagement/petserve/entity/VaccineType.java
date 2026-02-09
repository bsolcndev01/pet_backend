package com.petmanagement.petserve.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "vaccine_types")
public class VaccineType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_type_id")
    private Integer vaccineTypeId;

    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "species")
    private String species;

    @Column(name = "validity_period_months")
    private Integer validityPeriodMonths;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean active;

    public Integer getVaccineTypeId() {
        return vaccineTypeId;
    }

    public void setVaccineTypeId(Integer vaccineTypeId) {
        this.vaccineTypeId = vaccineTypeId;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Integer getValidityPeriodMonths() {
        return validityPeriodMonths;
    }

    public void setValidityPeriodMonths(Integer validityPeriodMonths) {
        this.validityPeriodMonths = validityPeriodMonths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

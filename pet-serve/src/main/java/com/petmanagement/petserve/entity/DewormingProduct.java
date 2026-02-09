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
@Table(name = "deworming_products")
public class DewormingProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "deworming_type")
    private String dewormingType;

    @Column(name = "species")
    private String species;

    @Column(name = "validity_period_days")
    private Integer validityPeriodDays;

    @Column(name = "dosage_guide")
    private String dosageGuide;

    @Column(name = "is_active")
    private Boolean active;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDewormingType() {
        return dewormingType;
    }

    public void setDewormingType(String dewormingType) {
        this.dewormingType = dewormingType;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Integer getValidityPeriodDays() {
        return validityPeriodDays;
    }

    public void setValidityPeriodDays(Integer validityPeriodDays) {
        this.validityPeriodDays = validityPeriodDays;
    }

    public String getDosageGuide() {
        return dosageGuide;
    }

    public void setDosageGuide(String dosageGuide) {
        this.dosageGuide = dosageGuide;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

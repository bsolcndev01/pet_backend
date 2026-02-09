package com.petmanagement.petserve.dto.pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PetRequest {
    @NotBlank
    private String petName;
    @NotBlank
    private String species;
    @NotBlank
    private String breed;
    @NotBlank
    private String gender;
    @NotBlank
    private String birthDate;
    @NotNull
    private Double weight;
    private Double currentWeight;
    private String temperament;
    private String healthStatus;
    private String remark;
    private String specialNeeds;
    private String photoUrl;
    private String color;
    private String bodyType;
    private String earId;
    private String microchipId;
    private String adoptDate;
    private String registrationInfo;
    private String bloodType;
    private String allergies;
    private String chronicDiseases;
    private String geneticRisks;
    private String bannedDrugs;
    private Boolean sterilized;
    private String lastMedicalCheck;
}

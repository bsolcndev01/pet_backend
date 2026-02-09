package com.petmanagement.petserve.dto.pet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PetResponse {
    private Integer petId;
    private Integer userId;
    private String ownerUsername;
    private String petName;
    private String species;
    private String breed;
    private String gender;
    private String birthDate;
    private Double weight;
    private Double currentWeight;
    private String temperament;
    private String healthStatus;
    private String photoUrl;
    private String remark;
    private String specialNeeds;
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
    private String createdAt;
    private String updatedAt;
}

package com.petmanagement.petserve.dto.pet;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetPhotoDto {
    private Integer photoId;
    private String fileName;
    private String url;
    private LocalDateTime uploadedAt;
    private Double posX;
    private Double posY;
}

package com.petmanagement.petserve.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petmanagement.petserve.entity.PetPhoto;

public interface PetPhotoRepository extends JpaRepository<PetPhoto, Integer> {
    Optional<PetPhoto> findByPet_PetId(Integer petId);
    List<PetPhoto> findByPet_PetIdOrderByUploadedAtDesc(Integer petId);
    Optional<PetPhoto> findByPhotoIdAndPet_PetId(Integer photoId, Integer petId);
    void deleteByPet_PetId(Integer petId);
    void deleteByPhotoIdAndPet_PetId(Integer photoId, Integer petId);
}

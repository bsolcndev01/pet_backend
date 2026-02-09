package com.petmanagement.petserve.service;

import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.pet.HealthSummaryResponse;
import com.petmanagement.petserve.dto.pet.PetRequest;
import com.petmanagement.petserve.dto.pet.PetResponse;
import com.petmanagement.petserve.dto.pet.PetPhotoDto;
import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.entity.User;
import com.petmanagement.petserve.entity.PetPhoto;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.PetRepository;
import com.petmanagement.petserve.repository.UserRepository;
import com.petmanagement.petserve.repository.PetPhotoRepository;
import com.petmanagement.petserve.repository.projection.PetHealthSummaryProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PetPhotoRepository petPhotoRepository;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository, PetPhotoRepository petPhotoRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.petPhotoRepository = petPhotoRepository;
    }

    public PageResponse<PetResponse> listPets(String keyword, Integer userId, int page, int size) {
        return listPets(keyword, userId, page, size, false);
    }

    public PageResponse<PetResponse> listPets(String keyword, Integer userId, int page, int size, boolean includeAll) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageRequest = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "petId"));

        Integer ownerId = requireUser(userId);
        Page<Pet> petPage;

        if (includeAll) {
            if (StringUtils.hasText(keyword)) {
                petPage = petRepository.findByPetNameContainingIgnoreCase(keyword.trim(), pageRequest);
            } else {
                petPage = petRepository.findAll(pageRequest);
            }
        } else {
            if (StringUtils.hasText(keyword)) {
                petPage = petRepository.findByOwner_UserIdAndPetNameContainingIgnoreCase(ownerId, keyword.trim(), pageRequest);
            } else {
                petPage = petRepository.findByOwner_UserId(ownerId, pageRequest);
            }
        }

        List<PetResponse> records = petPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(records, petPage.getTotalElements(), safePage, safeSize);
    }

    public List<PetResponse> listAllPets(String keyword) {
        List<Pet> pets = petRepository.findAll(Sort.by(Sort.Direction.DESC, "petId"));
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim().toLowerCase();
            pets = pets.stream()
                    .filter(p -> (p.getPetName() != null && p.getPetName().toLowerCase().contains(kw))
                            || (p.getSpecies() != null && p.getSpecies().toLowerCase().contains(kw))
                            || (p.getBreed() != null && p.getBreed().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }
        return pets.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PetResponse getPet(Integer petId, Integer userId) {
        Pet pet = petRepository.findByPetIdAndOwner_UserId(petId, requireUser(userId))
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        return toResponse(pet);
    }

    @Transactional
    public PetResponse createPet(PetRequest request, Integer userId) {
        User owner = resolveUser(userId);

        Double weightValue = request.getCurrentWeight() != null ? request.getCurrentWeight() : request.getWeight();
        Pet pet = new Pet();
        pet.setOwner(owner);
        pet.setPetName(request.getPetName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setGender(toDbGender(request.getGender()));
        pet.setBirthDate(LocalDate.parse(request.getBirthDate()));
        if (weightValue != null) {
            pet.setCurrentWeight(BigDecimal.valueOf(weightValue));
        }
        pet.setTemperament(request.getTemperament());
        pet.setHealthStatus(StringUtils.hasText(request.getHealthStatus()) ? request.getHealthStatus() : "良好");
        pet.setSpecialNeeds(StringUtils.hasText(request.getSpecialNeeds()) ? request.getSpecialNeeds() : request.getRemark());
        pet.setPhotoUrl(request.getPhotoUrl());
        pet.setColor(request.getColor());
        pet.setBodyType(request.getBodyType());
        pet.setMicrochipId(request.getMicrochipId());
        pet.setEarId(request.getEarId());
        if (StringUtils.hasText(request.getAdoptDate())) {
            pet.setAdoptDate(LocalDate.parse(request.getAdoptDate()));
        }
        pet.setRegistrationInfo(request.getRegistrationInfo());
        pet.setBloodType(request.getBloodType());
        pet.setAllergies(request.getAllergies());
        pet.setChronicDiseases(request.getChronicDiseases());
        pet.setGeneticRisks(request.getGeneticRisks());
        pet.setBannedDrugs(request.getBannedDrugs());
        pet.setSterilized(request.getSterilized());
        if (StringUtils.hasText(request.getLastMedicalCheck())) {
            pet.setLastMedicalCheck(LocalDate.parse(request.getLastMedicalCheck()));
        }

        Pet saved = petRepository.save(pet);
        return toResponse(saved);
    }

    @Transactional
    public PetResponse updatePet(Integer petId, PetRequest request, Integer userId) {
        Integer ownerId = requireUser(userId);
        Pet pet = petRepository.findByPetIdAndOwner_UserId(petId, ownerId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));

        Double weightValue = request.getCurrentWeight() != null ? request.getCurrentWeight() : request.getWeight();
        pet.setPetName(request.getPetName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setGender(toDbGender(request.getGender()));
        pet.setBirthDate(LocalDate.parse(request.getBirthDate()));
        if (weightValue != null) {
            pet.setCurrentWeight(BigDecimal.valueOf(weightValue));
        } else {
            pet.setCurrentWeight(null);
        }
        pet.setTemperament(request.getTemperament());
        pet.setHealthStatus(StringUtils.hasText(request.getHealthStatus()) ? request.getHealthStatus() : pet.getHealthStatus());
        pet.setSpecialNeeds(StringUtils.hasText(request.getSpecialNeeds()) ? request.getSpecialNeeds() : request.getRemark());
        if (request.getPhotoUrl() != null) {
            pet.setPhotoUrl(request.getPhotoUrl());
        }
        pet.setColor(request.getColor());
        pet.setBodyType(request.getBodyType());
        pet.setMicrochipId(request.getMicrochipId());
        pet.setEarId(request.getEarId());
        if (StringUtils.hasText(request.getAdoptDate())) {
            pet.setAdoptDate(LocalDate.parse(request.getAdoptDate()));
        } else {
            pet.setAdoptDate(null);
        }
        pet.setRegistrationInfo(request.getRegistrationInfo());
        pet.setBloodType(request.getBloodType());
        pet.setAllergies(request.getAllergies());
        pet.setChronicDiseases(request.getChronicDiseases());
        pet.setGeneticRisks(request.getGeneticRisks());
        pet.setBannedDrugs(request.getBannedDrugs());
        pet.setSterilized(request.getSterilized());
        if (StringUtils.hasText(request.getLastMedicalCheck())) {
            pet.setLastMedicalCheck(LocalDate.parse(request.getLastMedicalCheck()));
        } else {
            pet.setLastMedicalCheck(null);
        }

        Pet updated = petRepository.save(pet);
        return toResponse(updated);
    }

    @Transactional
    public void deletePet(Integer petId, Integer userId) {
        Integer ownerId = requireUser(userId);
        Pet pet = petRepository.findByPetIdAndOwner_UserId(petId, ownerId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        petPhotoRepository.deleteByPet_PetId(petId);
        petRepository.delete(pet);
    }

    @Transactional
    public PetResponse updatePetAdmin(Integer petId, PetRequest request, Integer userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        if (userId != null) {
            pet.setOwner(resolveUser(userId));
        }

        pet.setPetName(request.getPetName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setGender(toDbGender(request.getGender()));
        pet.setBirthDate(LocalDate.parse(request.getBirthDate()));
        pet.setCurrentWeight(BigDecimal.valueOf(request.getWeight()));
        pet.setHealthStatus(StringUtils.hasText(request.getHealthStatus()) ? request.getHealthStatus() : pet.getHealthStatus());
        pet.setSpecialNeeds(request.getRemark());
        if (request.getPhotoUrl() != null) {
            pet.setPhotoUrl(request.getPhotoUrl());
        }
        pet.setColor(request.getColor());
        pet.setBodyType(request.getBodyType());
        pet.setMicrochipId(request.getMicrochipId());
        pet.setEarId(request.getEarId());
        if (StringUtils.hasText(request.getAdoptDate())) {
            pet.setAdoptDate(LocalDate.parse(request.getAdoptDate()));
        } else {
            pet.setAdoptDate(null);
        }
        pet.setRegistrationInfo(request.getRegistrationInfo());
        pet.setBloodType(request.getBloodType());
        pet.setAllergies(request.getAllergies());
        pet.setChronicDiseases(request.getChronicDiseases());
        pet.setGeneticRisks(request.getGeneticRisks());
        pet.setBannedDrugs(request.getBannedDrugs());
        pet.setSterilized(request.getSterilized());

        Pet updated = petRepository.save(pet);
        return toResponse(updated);
    }

    @Transactional
    public void deletePetAdmin(Integer petId) {
        petPhotoRepository.deleteByPet_PetId(petId);
        petRepository.deleteById(petId);
    }

    @Transactional
    public PetResponse uploadPhoto(Integer petId, Integer userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的图片");
        }
        Integer ownerId = requireUser(userId);
        Pet pet = petRepository.findByPetIdAndOwner_UserId(petId, ownerId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));

        try {
            // 先删除旧照片，保证一宠一图
            petPhotoRepository.deleteByPet_PetId(petId);

            PetPhoto photo = new PetPhoto();
            photo.setPet(pet);
            photo.setFileName(file.getOriginalFilename());
            photo.setContentType(file.getContentType());
            photo.setPhotoData(file.getBytes());
            photo.setUploadedAt(LocalDateTime.now());
            petPhotoRepository.save(photo);

            // 设置可访问的接口地址，前端可直接用
            String url = "/api/pets/" + petId + "/photo";
            pet.setPhotoUrl(url);
            petRepository.save(pet);

            return toResponse(pet);
        } catch (Exception e) {
            throw new BusinessException(500, "上传失败: " + e.getMessage());
        }
    }

    public Optional<PetPhoto> getPhoto(Integer petId, Integer userId) {
        Integer viewerId = requireUser(userId);
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        if (pet.getOwner() != null && !viewerId.equals(pet.getOwner().getUserId())) {
            throw new BusinessException(403, "无权查看该宠物");
        }
        return petPhotoRepository.findByPet_PetId(petId);
    }

    public List<HealthSummaryResponse> healthSummary(Integer userId) {
        Integer ownerId = requireUser(userId);
        List<PetHealthSummaryProjection> rows = petRepository.findHealthSummaryByUserId(ownerId);
        return rows.stream().map(row -> HealthSummaryResponse.builder()
                        .petId(row.getPetId())
                        .petName(row.getPetName())
                        .species(row.getSpecies())
                        .breed(row.getBreed())
                        .currentWeight(row.getCurrentWeight())
                        .healthStatus(row.getHealthStatus())
                        .lastMedicalCheck(row.getLastMedicalCheck())
                        .totalVisits(row.getTotalVisits())
                        .vaccineCount(row.getVaccineCount())
                        .dewormingCount(row.getDewormingCount())
                        .build())
                .collect(Collectors.toList());
    }

    public List<PetPhotoDto> listPhotos(Integer petId, Integer userId) {
        Pet pet = petRepository.findByPetIdAndOwner_UserId(petId, requireUser(userId))
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        return petPhotoRepository.findByPet_PetIdOrderByUploadedAtDesc(pet.getPetId()).stream()
                .map(this::toPhotoDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PetPhotoDto uploadPhotoToWall(Integer petId, Integer userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的图片");
        }
        Pet pet = petRepository.findByPetIdAndOwner_UserId(petId, requireUser(userId))
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        try {
            PetPhoto photo = new PetPhoto();
            photo.setPet(pet);
            photo.setFileName(file.getOriginalFilename());
            photo.setContentType(file.getContentType());
            photo.setPhotoData(file.getBytes());
            photo.setUploadedAt(LocalDateTime.now());
            photo.setPosX(0d);
            photo.setPosY(0d);
            PetPhoto saved = petPhotoRepository.save(photo);
            return toPhotoDto(saved);
        } catch (Exception e) {
            throw new BusinessException(500, "上传失败: " + e.getMessage());
        }
    }

    @Transactional
    public void deletePhoto(Integer photoId, Integer petId, Integer userId) {
        Pet pet = petRepository.findByPetIdAndOwner_UserId(petId, requireUser(userId))
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        PetPhoto photo = petPhotoRepository.findByPhotoIdAndPet_PetId(photoId, pet.getPetId())
                .orElseThrow(() -> new BusinessException(404, "照片不存在"));
        petPhotoRepository.delete(photo);
    }

    @Transactional
    public PetPhotoDto updatePhotoPosition(Integer photoId, Integer petId, Integer userId, Double posX, Double posY) {
        Pet pet = petRepository.findByPetIdAndOwner_UserId(petId, requireUser(userId))
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        PetPhoto photo = petPhotoRepository.findByPhotoIdAndPet_PetId(photoId, pet.getPetId())
                .orElseThrow(() -> new BusinessException(404, "照片不存在"));
        if (posX != null) {
            photo.setPosX(posX);
        }
        if (posY != null) {
            photo.setPosY(posY);
        }
        PetPhoto saved = petPhotoRepository.save(photo);
        return toPhotoDto(saved);
    }

    public Optional<PetPhoto> getPhotoById(Integer photoId, Integer userId) {
        Integer viewerId = requireUser(userId);
        PetPhoto photo = petPhotoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException(404, "照片不存在"));
        Integer ownerId = photo.getPet() != null && photo.getPet().getOwner() != null
                ? photo.getPet().getOwner().getUserId()
                : null;
        if (ownerId != null && !ownerId.equals(viewerId) && !isAdmin(viewerId)) {
            throw new BusinessException(403, "无权查看该照片");
        }
        return Optional.of(photo);
    }

    private Integer requireUser(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return userId;
    }

    private boolean isAdmin(Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    String roleName = user.getRole() != null ? user.getRole().getRoleName() : "";
                    return roleName.contains("管理员");
                })
                .orElse(false);
    }

    private PetPhotoDto toPhotoDto(PetPhoto photo) {
        return PetPhotoDto.builder()
                .photoId(photo.getPhotoId())
                .fileName(photo.getFileName())
                .uploadedAt(photo.getUploadedAt())
                .url("/api/pets/photos/" + photo.getPhotoId())
                .posX(photo.getPosX())
                .posY(photo.getPosY())
                .build();
    }

    private PetResponse toResponse(Pet pet) {
        String genderForFront = switch (pet.getGender() == null ? "" : pet.getGender()) {
            case "公", "M", "male" -> "M";
            case "母", "F", "female" -> "F";
            default -> "U";
        };
        return PetResponse.builder()
                .petId(pet.getPetId())
                .userId(pet.getOwner() != null ? pet.getOwner().getUserId() : null)
                .ownerUsername(pet.getOwner() != null ? pet.getOwner().getUsername() : null)
                .petName(pet.getPetName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .gender(genderForFront)
                .birthDate(pet.getBirthDate() != null ? pet.getBirthDate().toString() : null)
                .weight(pet.getCurrentWeight() != null ? pet.getCurrentWeight().doubleValue() : null)
                .currentWeight(pet.getCurrentWeight() != null ? pet.getCurrentWeight().doubleValue() : null)
                .temperament(pet.getTemperament())
                .healthStatus(pet.getHealthStatus())
                .photoUrl(pet.getPhotoUrl())
                .remark(pet.getSpecialNeeds())
                .specialNeeds(pet.getSpecialNeeds())
                .color(pet.getColor())
                .bodyType(pet.getBodyType())
                .microchipId(pet.getMicrochipId())
                .earId(pet.getEarId())
                .adoptDate(pet.getAdoptDate() != null ? pet.getAdoptDate().toString() : null)
                .registrationInfo(pet.getRegistrationInfo())
                .bloodType(pet.getBloodType())
                .allergies(pet.getAllergies())
                .chronicDiseases(pet.getChronicDiseases())
                .geneticRisks(pet.getGeneticRisks())
                .bannedDrugs(pet.getBannedDrugs())
                .sterilized(pet.getSterilized())
                .lastMedicalCheck(pet.getLastMedicalCheck() != null ? pet.getLastMedicalCheck().toString() : null)
                .createdAt(pet.getCreatedAt() != null ? pet.getCreatedAt().toString() : null)
                .updatedAt(pet.getUpdatedAt() != null ? pet.getUpdatedAt().toString() : null)
                .build();
    }

    private String toDbGender(String frontGender) {
        if (!StringUtils.hasText(frontGender)) {
            return "未知";
        }
        return switch (frontGender.toUpperCase()) {
            case "M", "MALE" -> "公";
            case "F", "FEMALE" -> "母";
            default -> "未知";
        };
    }

    private User resolveUser(Integer userId) {
        Integer ownerId = requireUser(userId);
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }
}

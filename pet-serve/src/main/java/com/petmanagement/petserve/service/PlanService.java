package com.petmanagement.petserve.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petmanagement.petserve.dto.plan.PlanDto;
import com.petmanagement.petserve.dto.plan.PlanRequest;
import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.entity.PetPlan;
import com.petmanagement.petserve.entity.User;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.PetPlanRepository;
import com.petmanagement.petserve.repository.PetRepository;
import com.petmanagement.petserve.repository.UserRepository;

@Service
public class PlanService {
    private final PetPlanRepository planRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public PlanService(PetPlanRepository planRepository, PetRepository petRepository, UserRepository userRepository) {
        this.planRepository = planRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public List<PlanDto> listPlans(Integer petId, Integer userId) {
        Pet pet = ensurePet(petId, userId);
        return planRepository.findTop50ByPet_PetIdOrderByStartDateDesc(pet.getPetId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PlanDto create(PlanRequest request, Integer userId) {
        Pet pet = ensurePet(request.getPetId(), userId);
        PetPlan plan = new PetPlan();
        plan.setPet(pet);
        plan.setPlanType(request.getPlanType());
        plan.setTitle(request.getTitle());
        plan.setTarget(request.getTarget());
        plan.setFrequency(request.getFrequency());
        plan.setStartDate(parseDate(request.getStartDate()));
        plan.setEndDate(parseDate(request.getEndDate()));
        plan.setNotes(request.getNotes());
        if (userId != null) {
            userRepository.findById(userId).ifPresent(plan::setCreatedBy);
        }
        PetPlan saved = planRepository.save(plan);
        return toDto(saved);
    }

    public PlanDto update(Integer planId, PlanRequest request, Integer userId) {
        PetPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(404, "计划不存在"));
        Integer ownerId = plan.getPet() != null && plan.getPet().getOwner() != null
                ? plan.getPet().getOwner().getUserId()
                : null;
        if (ownerId != null && userId != null && !ownerId.equals(userId)) {
            throw new BusinessException(403, "无权编辑该计划");
        }
        Pet pet = ensurePet(request.getPetId(), userId);
        plan.setPet(pet);
        plan.setPlanType(request.getPlanType());
        plan.setTitle(request.getTitle());
        plan.setTarget(request.getTarget());
        plan.setFrequency(request.getFrequency());
        plan.setStartDate(parseDate(request.getStartDate()));
        plan.setEndDate(parseDate(request.getEndDate()));
        plan.setNotes(request.getNotes());
        PetPlan saved = planRepository.save(plan);
        return toDto(saved);
    }

    public void delete(Integer planId, Integer userId) {
        PetPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(404, "计划不存在"));
        Integer ownerId = plan.getPet() != null && plan.getPet().getOwner() != null
                ? plan.getPet().getOwner().getUserId()
                : null;
        if (ownerId != null && userId != null && !ownerId.equals(userId)) {
            throw new BusinessException(403, "无权删除该计划");
        }
        planRepository.delete(plan);
    }

    private Pet ensurePet(Integer petId, Integer userId) {
        return petRepository.findByPetIdAndOwner_UserId(petId, requireUser(userId))
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
    }

    private Integer requireUser(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return userId;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FMT);
    }

    private PlanDto toDto(PetPlan plan) {
        return PlanDto.builder()
                .planId(plan.getPlanId())
                .petId(plan.getPet() != null ? plan.getPet().getPetId() : null)
                .petName(plan.getPet() != null ? plan.getPet().getPetName() : null)
                .planType(plan.getPlanType())
                .title(plan.getTitle())
                .target(plan.getTarget())
                .frequency(plan.getFrequency())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .notes(plan.getNotes())
                .createdAt(plan.getCreatedAt())
                .createdBy(plan.getCreatedBy() != null ? plan.getCreatedBy().getUsername() : null)
                .build();
    }
}

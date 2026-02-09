package com.petmanagement.petserve.service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.petmanagement.petserve.dto.pet.MetricRecordRequest;
import com.petmanagement.petserve.dto.pet.MetricRecordResponse;
import com.petmanagement.petserve.entity.ActivityRecord;
import com.petmanagement.petserve.entity.IntakeRecord;
import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.entity.TemperatureRecord;
import com.petmanagement.petserve.entity.WeightRecord;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.ActivityRecordRepository;
import com.petmanagement.petserve.repository.IntakeRecordRepository;
import com.petmanagement.petserve.repository.PetRepository;
import com.petmanagement.petserve.repository.TemperatureRecordRepository;
import com.petmanagement.petserve.repository.WeightRecordRepository;

@Service
public class MetricService {

    private static final Logger log = LoggerFactory.getLogger(MetricService.class);

    private final PetRepository petRepository;
    private final WeightRecordRepository weightRecordRepository;
    private final TemperatureRecordRepository temperatureRecordRepository;
    private final ActivityRecordRepository activityRecordRepository;
    private final IntakeRecordRepository intakeRecordRepository;

    @Autowired
    public MetricService(PetRepository petRepository,
                         WeightRecordRepository weightRecordRepository,
                         TemperatureRecordRepository temperatureRecordRepository,
                         ActivityRecordRepository activityRecordRepository,
                         IntakeRecordRepository intakeRecordRepository) {
        this.petRepository = petRepository;
        this.weightRecordRepository = weightRecordRepository;
        this.temperatureRecordRepository = temperatureRecordRepository;
        this.activityRecordRepository = activityRecordRepository;
        this.intakeRecordRepository = intakeRecordRepository;
    }

    public List<MetricRecordResponse> metrics(Integer petId, Integer userId, String type, Integer limit) {
        Pet pet = ensurePet(petId, userId);
        String metricType = normalizeType(type);
        int size = (limit != null && limit > 0) ? Math.min(limit, 180) : 30;

        return fetch(metricType, pet.getPetId(), size);
    }

    public MetricRecordResponse addMetric(Integer petId, Integer userId, MetricRecordRequest request) {
        Pet pet = ensurePet(petId, userId);
        String metricType = normalizeType(request.getType());
        if (request.getRecordDate() == null) {
            throw new BusinessException(400, "记录日期不能为空");
        }
        Double value = request.getValue();
        if (value == null) {
            throw new BusinessException(400, "数值不能为空");
        }
        Integer recorder = requireUser(userId);
        switch (metricType) {
            case "weight":
                WeightRecord weightRecord = new WeightRecord();
                weightRecord.setPet(pet);
                weightRecord.setWeight(value != null ? java.math.BigDecimal.valueOf(value) : null);
                weightRecord.setRecordDate(request.getRecordDate());
                weightRecord.setNotes(request.getNotes());
                weightRecord.setRecordedBy(recorder);
                weightRecordRepository.save(weightRecord);
                return MetricRecordResponse.builder()
                        .type("weight")
                        .recordDate(weightRecord.getRecordDate())
                        .value(value)
                        .notes(weightRecord.getNotes())
                        .build();
            case "temperature":
                TemperatureRecord temperatureRecord = new TemperatureRecord();
                temperatureRecord.setPet(pet);
                temperatureRecord.setTemperature(value != null ? java.math.BigDecimal.valueOf(value) : null);
                temperatureRecord.setRecordDate(request.getRecordDate());
                temperatureRecord.setNotes(request.getNotes());
                temperatureRecord.setRecordedBy(recorder);
                temperatureRecordRepository.save(temperatureRecord);
                return MetricRecordResponse.builder()
                        .type("temperature")
                        .recordDate(temperatureRecord.getRecordDate())
                        .value(value)
                        .notes(temperatureRecord.getNotes())
                        .build();
            case "activity":
                ActivityRecord activityRecord = new ActivityRecord();
                activityRecord.setPet(pet);
                activityRecord.setActivityLevel(value != null ? java.math.BigDecimal.valueOf(value) : null);
                activityRecord.setRecordDate(request.getRecordDate());
                activityRecord.setNotes(request.getNotes());
                activityRecord.setRecordedBy(recorder);
                activityRecordRepository.save(activityRecord);
                return MetricRecordResponse.builder()
                        .type("activity")
                        .recordDate(activityRecord.getRecordDate())
                        .value(value)
                        .notes(activityRecord.getNotes())
                        .build();
            case "intake":
                IntakeRecord intakeRecord = new IntakeRecord();
                intakeRecord.setPet(pet);
                intakeRecord.setIntakeVolume(value != null ? java.math.BigDecimal.valueOf(value) : null);
                intakeRecord.setRecordDate(request.getRecordDate());
                intakeRecord.setNotes(request.getNotes());
                intakeRecord.setRecordedBy(recorder);
                intakeRecordRepository.save(intakeRecord);
                return MetricRecordResponse.builder()
                        .type("intake")
                        .recordDate(intakeRecord.getRecordDate())
                        .value(value)
                        .notes(intakeRecord.getNotes())
                        .build();
            default:
                throw new BusinessException(400, "不支持的指标类型");
        }
    }

    private Pet ensurePet(Integer petId, Integer userId) {
        Integer ownerId = requireUser(userId);
        return petRepository.findByPetIdAndOwner_UserId(petId, ownerId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
    }

    private String normalizeType(String type) {
        if (!StringUtils.hasText(type)) {
            throw new BusinessException(400, "指标类型不能为空");
        }
        String normalized = type.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("temp")) {
            return "temperature";
        }
        return normalized;
    }

    private List<MetricRecordResponse> mapWeight(List<WeightRecord> records) {
        return records.stream()
                .map(r -> MetricRecordResponse.builder()
                        .type("weight")
                        .recordDate(r.getRecordDate())
                        .value(r.getWeight() != null ? r.getWeight().doubleValue() : null)
                        .notes(r.getNotes())
                        .build())
                .collect(Collectors.toList());
    }

    private List<MetricRecordResponse> mapTemperature(List<TemperatureRecord> records) {
        return records.stream()
                .map(r -> MetricRecordResponse.builder()
                        .type("temperature")
                        .recordDate(r.getRecordDate())
                        .value(r.getTemperature() != null ? r.getTemperature().doubleValue() : null)
                        .notes(r.getNotes())
                        .build())
                .collect(Collectors.toList());
    }

    private List<MetricRecordResponse> mapActivity(List<ActivityRecord> records) {
        return records.stream()
                .map(r -> MetricRecordResponse.builder()
                        .type("activity")
                        .recordDate(r.getRecordDate())
                        .value(r.getActivityLevel() != null ? r.getActivityLevel().doubleValue() : null)
                        .notes(r.getNotes())
                        .build())
                .collect(Collectors.toList());
    }

    private List<MetricRecordResponse> mapIntake(List<IntakeRecord> records) {
        return records.stream()
                .map(r -> MetricRecordResponse.builder()
                        .type("intake")
                        .recordDate(r.getRecordDate())
                        .value(r.getIntakeVolume() != null ? r.getIntakeVolume().doubleValue() : null)
                        .notes(r.getNotes())
                        .build())
                .collect(Collectors.toList());
    }

    private List<MetricRecordResponse> fetch(String type, Integer petId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "recordDate"));
        List<MetricRecordResponse> list = switch (type) {
            case "weight" -> mapWeight(weightRecordRepository.findByPet_PetIdOrderByRecordDateDesc(petId, pageable));
            case "temperature" -> mapTemperature(temperatureRecordRepository.findByPet_PetIdOrderByRecordDateDesc(petId, pageable));
            case "activity" -> mapActivity(activityRecordRepository.findByPet_PetIdOrderByRecordDateDesc(petId, pageable));
            case "intake" -> mapIntake(intakeRecordRepository.findByPet_PetIdOrderByRecordDateDesc(petId, pageable));
            default -> Collections.emptyList();
        };

        Collections.reverse(list); // 结果按日期升序返回
        if (list.isEmpty()) {
            log.warn("No {} metrics found for pet {} (limit {})", type, petId, limit);
        }
        return list;
    }

    private Integer requireUser(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return userId;
    }
}

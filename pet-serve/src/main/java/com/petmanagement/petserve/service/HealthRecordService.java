package com.petmanagement.petserve.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.petmanagement.petserve.dto.health.DewormingCreateRequest;
import com.petmanagement.petserve.dto.health.DewormingDto;
import com.petmanagement.petserve.dto.health.DewormingProductOption;
import com.petmanagement.petserve.dto.health.TemperatureDto;
import com.petmanagement.petserve.dto.health.ActivityDto;
import com.petmanagement.petserve.dto.health.IntakeDto;
import com.petmanagement.petserve.dto.health.MedicalRecordCreateRequest;
import com.petmanagement.petserve.dto.health.MedicalRecordDto;
import com.petmanagement.petserve.dto.health.MedicationCreateRequest;
import com.petmanagement.petserve.dto.health.MedicationDto;
import com.petmanagement.petserve.dto.health.VaccinationCreateRequest;
import com.petmanagement.petserve.dto.health.VaccinationDto;
import com.petmanagement.petserve.dto.health.VaccineTypeOption;
import com.petmanagement.petserve.dto.health.VitalCreateRequest;
import com.petmanagement.petserve.dto.health.VitalDto;
import com.petmanagement.petserve.dto.health.VetDailyStatsResponse;
import com.petmanagement.petserve.dto.health.VetOverviewStatsResponse;
import com.petmanagement.petserve.entity.DewormingRecord;
import com.petmanagement.petserve.dto.health.InstitutionOption;
import com.petmanagement.petserve.dto.health.VetOption;
import com.petmanagement.petserve.entity.Medication;
import com.petmanagement.petserve.entity.MedicalRecord;
import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.entity.Vaccination;
import com.petmanagement.petserve.entity.Veterinarian;
import com.petmanagement.petserve.entity.WeightRecord;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.DewormingRecordRepository;
import com.petmanagement.petserve.repository.MedicalRecordRepository;
import com.petmanagement.petserve.repository.MedicationRepository;
import com.petmanagement.petserve.repository.PetRepository;
import com.petmanagement.petserve.repository.UserRepository;
import com.petmanagement.petserve.repository.VaccinationRepository;
import com.petmanagement.petserve.repository.WeightRecordRepository;
import com.petmanagement.petserve.repository.VeterinarianRepository;

@Service
public class HealthRecordService {
    private final PetRepository petRepository;
    private final VaccinationRepository vaccinationRepository;
    private final DewormingRecordRepository dewormingRecordRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final WeightRecordRepository weightRecordRepository;
    private final MedicationRepository medicationRepository;
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public HealthRecordService(PetRepository petRepository,
                               VaccinationRepository vaccinationRepository,
                               DewormingRecordRepository dewormingRecordRepository,
                               MedicalRecordRepository medicalRecordRepository,
                               WeightRecordRepository weightRecordRepository,
                               MedicationRepository medicationRepository,
                               UserRepository userRepository,
                               VeterinarianRepository veterinarianRepository,
                               JdbcTemplate jdbcTemplate,
                               NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.petRepository = petRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.dewormingRecordRepository = dewormingRecordRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.weightRecordRepository = weightRecordRepository;
        this.medicationRepository = medicationRepository;
        this.userRepository = userRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private Integer resolveUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return userId;
    }

    private Pet ensurePet(Integer petId, Integer userId) {
        Integer ownerId = resolveUserId(userId);
        return petRepository.findByPetIdAndOwner_UserId(petId, ownerId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
    }

    private Pet ensurePetForActor(Integer petId, Integer userId, Integer vetUserId) {
        Integer actorId = resolveUserId(userId);
        if (vetUserId != null && vetUserId.equals(actorId)) {
            return petRepository.findById(petId)
                    .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        }
        return ensurePet(petId, actorId);
    }

    private List<Pet> resolvePets(Integer petId, Integer userId) {
        Integer ownerId = resolveUserId(userId);
        if (petId != null) {
            try {
                return Collections.singletonList(ensurePet(petId, ownerId));
            } catch (BusinessException ex) {
                return Collections.emptyList();
            }
        }
        List<Pet> pets = petRepository.findByOwner_UserId(ownerId);
        return pets;
    }

    public List<InstitutionOption> institutions() {
        return jdbcTemplate.query(
                "select institution_id, institution_name from medical_institutions order by institution_id asc limit 100",
                (rs, rowNum) -> new InstitutionOption(rs.getInt(1), rs.getString(2)));
    }

    public List<VetOption> vets(Integer institutionId) {
        String baseSql = """
                select u.user_id, u.username
                from users u
                join veterinarians v on v.user_id = u.user_id
                where u.role_id = 2
                """;
        if (institutionId != null) {
            baseSql += " and v.institution_id = ?";
            return jdbcTemplate.query(
                    baseSql + " order by u.user_id asc limit 100",
                    (rs, rowNum) -> new VetOption(rs.getInt(1), rs.getString(2)),
                    institutionId);
        }
        return jdbcTemplate.query(
                baseSql + " order by u.user_id asc limit 100",
                (rs, rowNum) -> new VetOption(rs.getInt(1), rs.getString(2)));
    }

    public InstitutionOption vetInstitution(Integer vetUserId) {
        if (vetUserId == null) {
            return null;
        }
        try {
            return jdbcTemplate.queryForObject(
                    """
                    select mi.institution_id, mi.institution_name
                    from veterinarians v
                    join medical_institutions mi on mi.institution_id = v.institution_id
                    where v.user_id = ?
                    limit 1
                    """,
                    (rs, rowNum) -> new InstitutionOption(rs.getInt(1), rs.getString(2)),
                    vetUserId
            );
        } catch (Exception ex) {
            return null;
        }
    }

    public List<DewormingProductOption> dewormProductNames() {
        return jdbcTemplate.query(
                "select product_id, product_name from deworming_products where is_active is null or is_active = true order by product_id asc limit 100",
                (rs, rowNum) -> new DewormingProductOption(rs.getInt(1), rs.getString(2)));
    }

    public List<DewormingProductOption> dewormProducts() {
        return dewormProductNames();
    }

    public List<VaccineTypeOption> vaccineTypes() {
        return jdbcTemplate.query(
                "select vaccine_type_id, vaccine_name from vaccine_types order by vaccine_type_id asc limit 100",
                (rs, rowNum) -> new VaccineTypeOption(rs.getInt(1), rs.getString(2)));
    }

    @Transactional
    public VaccinationDto addVaccination(VaccinationCreateRequest request, Integer userId) {
        Pet pet = ensurePet(request.getPetId(), userId);
        Vaccination vaccination = new Vaccination();
        vaccination.setPet(pet);
        vaccination.setVaccineTypeId(resolveVaccineTypeId(request));
        vaccination.setInstitutionId(request.getInstitutionId());
        vaccination.setVeterinarian(resolveVeterinarian(request.getVetId()));
        vaccination.setVaccinationDate(request.getVaccinationDate());
        vaccination.setNextDueDate(request.getNextDueDate());
        vaccination.setLotNumber(request.getLotNumber());
        vaccination.setNotes(request.getNotes());
        vaccination.setCreatedBy(resolveUserId(userId));
        vaccination.setCreatedAt(LocalDateTime.now());
        Vaccination saved = vaccinationRepository.save(vaccination);
        return toVaccinationDto(saved, pet);
    }

    @Transactional
    public DewormingDto addDeworming(DewormingCreateRequest request, Integer userId) {
        Pet pet = ensurePet(request.getPetId(), userId);
        DewormingRecord record = new DewormingRecord();
        record.setPet(pet);
        record.setProductId(request.getProductId());
        record.setSourceType(StringUtils.hasText(request.getSourceType()) ? request.getSourceType() : "自驱");
        record.setInstitutionId(request.getInstitutionId());
        record.setVetUserId(request.getVetUserId());
        record.setApplicationDate(request.getApplicationDate());
        record.setNextDueDate(request.getNextDueDate() != null ? request.getNextDueDate() : request.getApplicationDate());
        record.setDosage(request.getDosage());
        record.setNotes(request.getNotes());
        record.setRecordedBy(resolveUserId(userId));
        record.setCreatedAt(LocalDateTime.now());
        DewormingRecord saved = dewormingRecordRepository.save(record);
        return toDewormingDto(saved, pet);
    }

    @Transactional
    public MedicalRecordDto addMedicalRecord(MedicalRecordCreateRequest request, Integer userId) {
        Pet pet = ensurePetForActor(request.getPetId(), userId, request.getVetUserId());
        MedicalRecord record = new MedicalRecord();
        record.setPet(pet);
        record.setInstitutionId(request.getInstitutionId());
        record.setVetUserId(request.getVetUserId());
        record.setVisitDate(request.getVisitDate());
        record.setReason(request.getReason());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setPrescription(request.getPrescription());
        record.setCost(request.getCost());
        record.setPaymentStatus(StringUtils.hasText(request.getPaymentStatus()) ? request.getPaymentStatus() : "未支付");
        record.setRecordStatus(StringUtils.hasText(request.getRecordStatus()) ? request.getRecordStatus() : "初诊");
        record.setFollowUpDate(request.getFollowUpDate());
        record.setCreatedAt(LocalDateTime.now());
        MedicalRecord saved = medicalRecordRepository.save(record);
        return toMedicalRecordDto(saved, pet);
    }

    @Transactional
    public MedicalRecordDto updateMedicalRecord(Integer recordId, MedicalRecordCreateRequest request, Integer userId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(404, "医疗记录不存在"));
        Integer ownerId = record.getPet() != null && record.getPet().getOwner() != null
                ? record.getPet().getOwner().getUserId()
                : null;
        boolean isVetActor = record.getVetUserId() != null && userId != null && record.getVetUserId().equals(userId);
        if (ownerId != null && userId != null && !ownerId.equals(userId) && !isVetActor) {
            throw new BusinessException(403, "无权修改该医疗记录");
        }
        Pet pet = ensurePetForActor(request.getPetId(), userId, request.getVetUserId());
        record.setPet(pet);
        record.setInstitutionId(request.getInstitutionId());
        record.setVetUserId(request.getVetUserId());
        record.setVisitDate(request.getVisitDate());
        record.setReason(request.getReason());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setPrescription(request.getPrescription());
        record.setCost(request.getCost());
        record.setPaymentStatus(StringUtils.hasText(request.getPaymentStatus()) ? request.getPaymentStatus() : record.getPaymentStatus());
        record.setRecordStatus(StringUtils.hasText(request.getRecordStatus()) ? request.getRecordStatus() : record.getRecordStatus());
        record.setFollowUpDate(request.getFollowUpDate());
        MedicalRecord saved = medicalRecordRepository.save(record);
        return toMedicalRecordDto(saved, pet);
    }

    @Transactional
    public void deleteMedicalRecord(Integer recordId, Integer userId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(404, "医疗记录不存在"));
        Integer ownerId = record.getPet() != null && record.getPet().getOwner() != null
                ? record.getPet().getOwner().getUserId()
                : null;
        boolean isVetActor = record.getVetUserId() != null && userId != null && record.getVetUserId().equals(userId);
        if (ownerId != null && userId != null && !ownerId.equals(userId) && !isVetActor) {
            throw new BusinessException(403, "无权删除该医疗记录");
        }
        medicalRecordRepository.delete(record);
    }

    @Transactional
    public VitalDto addVital(VitalCreateRequest request, Integer userId) {
        Pet pet = ensurePet(request.getPetId(), userId);
        String type = StringUtils.hasText(request.getMetricType()) ? request.getMetricType() : "体重";
        if ("体重".equals(type)) {
            WeightRecord weightRecord = new WeightRecord();
            weightRecord.setPet(pet);
            weightRecord.setWeight(BigDecimal.valueOf(request.getValue()));
            weightRecord.setRecordDate(request.getRecordDate());
            weightRecord.setNotes(request.getNotes());
            weightRecord.setRecordedBy(resolveUserId(userId));
            weightRecord.setCreatedAt(LocalDateTime.now());
            WeightRecord saved = weightRecordRepository.save(weightRecord);
            return toVitalDto(saved, pet);
        }
        String insertSql;
        String unit;
        switch (type) {
            case "体温":
                insertSql = "insert into temperature_records (pet_id, temperature, record_date, notes, recorded_by) values (?, ?, ?, ?, ?)";
                unit = "°C";
                break;
            case "活动量":
                insertSql = "insert into activity_records (pet_id, activity_level, record_date, notes, recorded_by) values (?, ?, ?, ?, ?)";
                unit = "%";
                break;
            case "饮水/进食":
                insertSql = "insert into intake_records (pet_id, intake_volume, record_date, notes, recorded_by) values (?, ?, ?, ?, ?)";
                unit = "ml";
                break;
            default:
                throw new BusinessException(400, "不支持的体征类型");
        }
        jdbcTemplate.update(insertSql, pet.getPetId(), request.getValue(), request.getRecordDate(), request.getNotes(), resolveUserId(userId));
        return VitalDto.builder()
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .recordDate(request.getRecordDate())
                .metricType(type)
                .value(request.getValue())
                .unit(unit)
                .notes(request.getNotes())
                .build();
    }

    @Transactional
    public VitalDto addTemperature(VitalCreateRequest request, Integer userId) {
        request.setMetricType("体温");
        return addVital(request, userId);
    }

    @Transactional
    public VitalDto addActivity(VitalCreateRequest request, Integer userId) {
        request.setMetricType("活动量");
        return addVital(request, userId);
    }

    @Transactional
    public VitalDto addIntake(VitalCreateRequest request, Integer userId) {
        request.setMetricType("饮水/进食");
        return addVital(request, userId);
    }

    @Transactional
    public VitalDto updateVital(Integer id, VitalCreateRequest request, Integer userId) {
        String type = StringUtils.hasText(request.getMetricType()) ? request.getMetricType() : "体重";
        Pet pet = ensurePet(request.getPetId(), userId);
        Integer ownerId = pet.getOwner() != null ? pet.getOwner().getUserId() : null;
        if (ownerId != null && userId != null && !ownerId.equals(userId)) {
            throw new BusinessException(403, "无权编辑该记录");
        }
        switch (type) {
            case "体重": {
                WeightRecord record = weightRecordRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(404, "体重记录不存在"));
                record.setPet(pet);
                record.setWeight(BigDecimal.valueOf(request.getValue()));
                record.setRecordDate(request.getRecordDate());
                record.setNotes(request.getNotes());
                WeightRecord saved = weightRecordRepository.save(record);
                return toVitalDto(saved, pet);
            }
            case "体温":
                jdbcTemplate.update("update temperature_records set temperature = ?, record_date = ?, notes = ? where temperature_id = ? and pet_id = ?",
                        request.getValue(), request.getRecordDate(), request.getNotes(), id, pet.getPetId());
                break;
            case "活动量":
                jdbcTemplate.update("update activity_records set activity_level = ?, record_date = ?, notes = ? where activity_id = ? and pet_id = ?",
                        request.getValue(), request.getRecordDate(), request.getNotes(), id, pet.getPetId());
                break;
            case "饮水/进食":
                jdbcTemplate.update("update intake_records set intake_volume = ?, record_date = ?, notes = ? where intake_id = ? and pet_id = ?",
                        request.getValue(), request.getRecordDate(), request.getNotes(), id, pet.getPetId());
                break;
            default:
                throw new BusinessException(400, "不支持的体征类型");
        }
        return VitalDto.builder()
                .id(id)
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .recordDate(request.getRecordDate())
                .metricType(type)
                .value(request.getValue())
                .notes(request.getNotes())
                .build();
    }

    @Transactional
    public void deleteVital(Integer id, Integer userId) {
        WeightRecord record = weightRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "体征记录不存在"));
        Integer ownerId = record.getPet() != null && record.getPet().getOwner() != null
                ? record.getPet().getOwner().getUserId()
                : null;
        if (ownerId != null && userId != null && !ownerId.equals(userId)) {
            throw new BusinessException(403, "无权删除该记录");
        }
        weightRecordRepository.delete(record);
    }

    @Transactional
    public MedicationDto addMedication(MedicationCreateRequest request, Integer userId) {
        Pet pet = ensurePetForActor(request.getPetId(), userId, request.getVetUserId());
        Medication medication = new Medication();
        medication.setPet(pet);
        if (request.getVetUserId() != null) {
            userRepository.findById(request.getVetUserId()).ifPresent(medication::setVet);
        }
        medication.setDrugName(request.getDrugName());
        medication.setDosage(request.getDosage());
        medication.setFrequency(request.getFrequency());
        medication.setRoute(request.getRoute());
        medication.setStartDate(request.getStartDate());
        medication.setEndDate(request.getEndDate());
        medication.setInstructions(request.getInstructions());
        medication.setContraindications(request.getContraindications());
        medication.setStatus(resolveMedicationStatus(request.getStatus(), request.getEndDate()));
        medication.setCreatedAt(LocalDateTime.now());
        Medication saved = medicationRepository.save(medication);
        return toMedicationDto(saved, pet);
    }

    @Transactional
    public MedicationDto updateMedication(Integer id, MedicationCreateRequest request, Integer userId) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用药记录不存在"));
        Pet pet = medication.getPet();
        boolean isOwner = pet != null && pet.getOwner() != null && userId != null && pet.getOwner().getUserId().equals(userId);
        boolean isVetActor = medication.getVet() != null && userId != null && medication.getVet().getUserId().equals(userId);
        if (!isOwner && !isVetActor && userId != null) {
            throw new BusinessException(403, "无权操作该用药记录");
        }
        Pet targetPet = ensurePetForActor(request.getPetId(), userId, request.getVetUserId());
        medication.setPet(targetPet);
        if (request.getVetUserId() != null) {
            userRepository.findById(request.getVetUserId()).ifPresent(medication::setVet);
        }
        medication.setDrugName(request.getDrugName());
        medication.setDosage(request.getDosage());
        medication.setFrequency(request.getFrequency());
        medication.setRoute(request.getRoute());
        medication.setStartDate(request.getStartDate());
        medication.setEndDate(request.getEndDate());
        medication.setInstructions(request.getInstructions());
        medication.setContraindications(request.getContraindications());
        medication.setStatus(resolveMedicationStatus(request.getStatus(), request.getEndDate()));
        Medication saved = medicationRepository.save(medication);
        return toMedicationDto(saved, targetPet);
    }

    public List<VaccinationDto> vaccinations(Integer petId, Integer userId) {
        List<Pet> pets = resolvePets(petId, userId);
        return pets.stream()
                .flatMap(p -> vaccinationRepository.findTop20ByPet_PetIdOrderByVaccinationDateDesc(p.getPetId()).stream()
                        .map(v -> toVaccinationDto(v, p)))
                .sorted(Comparator.comparing(VaccinationDto::getVaccinationDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Transactional
    public VaccinationDto updateVaccination(Integer id, VaccinationCreateRequest request, Integer userId) {
        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "疫苗记录不存在"));
        Pet pet = vaccination.getPet();
        if (pet == null || pet.getOwner() == null || (userId != null && !pet.getOwner().getUserId().equals(userId))) {
            throw new BusinessException(403, "无权操作该疫苗记录");
        }

        vaccination.setVaccineTypeId(request.getVaccineTypeId());
        vaccination.setInstitutionId(request.getInstitutionId());
        vaccination.setVeterinarian(resolveVeterinarian(request.getVetId()));
        vaccination.setVaccinationDate(request.getVaccinationDate());
        vaccination.setNextDueDate(request.getNextDueDate());
        vaccination.setLotNumber(request.getLotNumber());
        vaccination.setNotes(request.getNotes());
        vaccination.setVaccineTypeId(resolveVaccineTypeId(request));
        Vaccination saved = vaccinationRepository.save(vaccination);
        return toVaccinationDto(saved, pet);
    }

    @Transactional
    public void deleteVaccination(Integer id, Integer userId) {
        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "疫苗记录不存在"));
        Pet pet = vaccination.getPet();
        if (pet == null || pet.getOwner() == null || (userId != null && !pet.getOwner().getUserId().equals(userId))) {
            throw new BusinessException(403, "无权删除该疫苗记录");
        }
        vaccinationRepository.delete(vaccination);
    }

    public List<DewormingDto> deworming(Integer petId, Integer userId) {
        List<Pet> pets = resolvePets(petId, userId);
        return pets.stream()
                .flatMap(p -> dewormingRecordRepository.findTop20ByPet_PetIdOrderByApplicationDateDesc(p.getPetId()).stream()
                        .map(r -> toDewormingDto(r, p)))
                .sorted(Comparator.comparing(DewormingDto::getApplicationDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public List<MedicalRecordDto> medicalRecords(Integer petId, Integer userId, Integer vetUserId) {
        if (vetUserId != null) {
            List<MedicalRecord> records;
            if (petId != null) {
                records = medicalRecordRepository.findTop20ByVetUserIdAndPet_PetIdOrderByVisitDateDesc(vetUserId, petId);
            } else {
                records = medicalRecordRepository.findTop20ByVetUserIdOrderByVisitDateDesc(vetUserId);
            }
            return records.stream()
                    .map(m -> toMedicalRecordDto(m, m.getPet()))
                    .sorted(Comparator.comparing(MedicalRecordDto::getVisitDate, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        List<Pet> pets = resolvePets(petId, userId);
        return pets.stream()
                .flatMap(p -> medicalRecordRepository.findTop20ByPet_PetIdOrderByVisitDateDesc(p.getPetId()).stream()
                        .map(m -> toMedicalRecordDto(m, p)))
                .sorted(Comparator.comparing(MedicalRecordDto::getVisitDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public List<TemperatureDto> temperature(Integer petId, Integer userId) {
        List<Pet> pets = resolvePets(petId, userId);
        RowMapper<TemperatureDto> mapper = (rs, rowNum) -> new TemperatureDto(
                rs.getObject("id") != null ? rs.getInt("id") : null,
                rs.getInt("pet_id"),
                null,
                rs.getDate("record_date").toLocalDate(),
                rs.getDouble("val"),
                rs.getString("notes"));
        return pets.stream()
                .flatMap(p -> jdbcTemplate.query(
                        "select temperature_id as id, temperature as val, record_date, notes, pet_id from temperature_records where pet_id = ? order by record_date desc limit 10",
                        mapper,
                        p.getPetId()).stream()
                        .peek(t -> t.setPetName(p.getPetName())))
                .collect(Collectors.toList());
    }

    public List<ActivityDto> activity(Integer petId, Integer userId) {
        List<Pet> pets = resolvePets(petId, userId);
        RowMapper<ActivityDto> mapper = (rs, rowNum) -> new ActivityDto(
                rs.getObject("id") != null ? rs.getInt("id") : null,
                rs.getInt("pet_id"),
                null,
                rs.getDate("record_date").toLocalDate(),
                rs.getDouble("val"),
                rs.getString("notes"));
        return pets.stream()
                .flatMap(p -> jdbcTemplate.query(
                        "select activity_id as id, activity_level as val, record_date, notes, pet_id from activity_records where pet_id = ? order by record_date desc limit 10",
                        mapper,
                        p.getPetId()).stream()
                        .peek(t -> t.setPetName(p.getPetName())))
                .collect(Collectors.toList());
    }

    public List<IntakeDto> intake(Integer petId, Integer userId) {
        List<Pet> pets = resolvePets(petId, userId);
        RowMapper<IntakeDto> mapper = (rs, rowNum) -> new IntakeDto(
                rs.getObject("id") != null ? rs.getInt("id") : null,
                rs.getInt("pet_id"),
                null,
                rs.getDate("record_date").toLocalDate(),
                rs.getDouble("val"),
                rs.getString("notes"));
        return pets.stream()
                .flatMap(p -> jdbcTemplate.query(
                        "select intake_id as id, intake_volume as val, record_date, notes, pet_id from intake_records where pet_id = ? order by record_date desc limit 10",
                        mapper,
                        p.getPetId()).stream()
                        .peek(t -> t.setPetName(p.getPetName())))
                .collect(Collectors.toList());
    }

    public List<VitalDto> vitals(Integer petId, Integer userId) {
        List<Pet> pets = resolvePets(petId, userId);
        List<VitalDto> list = pets.stream()
                .flatMap(p -> weightRecordRepository.findTop10ByPet_PetIdOrderByRecordDateDesc(p.getPetId()).stream()
                        .map(w -> VitalDto.builder()
                                .id(w.getWeightId())
                                .petId(p.getPetId())
                                .petName(p.getPetName())
                                .recordDate(w.getRecordDate())
                                .metricType("体重")
                                .value(w.getWeight() != null ? w.getWeight().doubleValue() : null)
                                .unit("kg")
                                .notes(w.getNotes())
                                .build()))
                .collect(Collectors.toList());

        list.addAll(loadSimpleVitals(pets, "体温", "°C",
                "select temperature_id as id, temperature as val, record_date, notes, pet_id from temperature_records where pet_id = ? order by record_date desc limit 10"));
        list.addAll(loadSimpleVitals(pets, "活动量", "%",
                "select activity_id as id, activity_level as val, record_date, notes, pet_id from activity_records where pet_id = ? order by record_date desc limit 10"));
        list.addAll(loadSimpleVitals(pets, "饮水/进食", "ml",
                "select intake_id as id, intake_volume as val, record_date, notes, pet_id from intake_records where pet_id = ? order by record_date desc limit 10"));

        return list.stream()
                .sorted(Comparator.comparing(VitalDto::getRecordDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public List<MedicationDto> medications(Integer petId, Integer userId, Integer vetUserId) {
        if (vetUserId != null) {
            List<Medication> meds;
            if (petId != null) {
                meds = medicationRepository.findTop50ByVet_UserIdAndPet_PetIdOrderByStartDateDesc(vetUserId, petId);
            } else {
                meds = medicationRepository.findTop50ByVet_UserIdOrderByStartDateDesc(vetUserId);
            }
            return meds.stream()
                    .map(m -> toMedicationDto(m, m.getPet()))
                    .sorted(Comparator.comparing(MedicationDto::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        List<Pet> pets = resolvePets(petId, userId);
        return pets.stream()
                .flatMap(p -> medicationRepository.findTop20ByPet_PetIdOrderByStartDateDesc(p.getPetId()).stream()
                        .map(m -> toMedicationDto(m, p)))
                .sorted(Comparator.comparing(MedicationDto::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public VetDailyStatsResponse dailyStats(Integer vetUserId) {
        String sql = """
                select
                    coalesce(sum(case when status in ('待确认','pending') then 1 else 0 end),0) as pending_count,
                    coalesce(sum(case when status in ('已确认','confirmed','已到店','进行中') then 1 else 0 end),0) as confirmed_count,
                    coalesce(sum(case when status in ('已完成','completed') then 1 else 0 end),0) as completed_count,
                    coalesce(sum(case when status in ('已取消','cancelled','患者爽约','医生改期') then 1 else 0 end),0) as cancelled_count
                from appointments
                where appointment_date >= current_date()
                  and (:vetId is null or vet_user_id = :vetId)
                """;
        Map<String, Object> row = namedParameterJdbcTemplate.queryForMap(sql,
                Collections.singletonMap("vetId", vetUserId));
        return VetDailyStatsResponse.builder()
                .pendingCount(safeNumber(row, "pending_count"))
                .confirmedCount(safeNumber(row, "confirmed_count"))
                .completedCount(safeNumber(row, "completed_count"))
                .cancelledCount(safeNumber(row, "cancelled_count"))
                .build();
    }

    public VetOverviewStatsResponse overviewStats(Integer vetUserId) {
        String appointmentSql = """
                select
                    coalesce(sum(case when appointment_date = current_date() then 1 else 0 end),0) as today_appointments,
                    coalesce(count(distinct case when appointment_date = current_date() then pet_id end),0) as today_patients,
                    coalesce(sum(case when appointment_date between current_date() and date_add(current_date(), interval 7 day) then 1 else 0 end),0) as week_appointments,
                    coalesce(count(distinct case when appointment_date between current_date() and date_add(current_date(), interval 7 day) then pet_id end),0) as week_patients,
                    coalesce(sum(case when appointment_date between current_date() and date_add(current_date(), interval 30 day) then 1 else 0 end),0) as month_appointments,
                    coalesce(count(distinct case when appointment_date between current_date() and date_add(current_date(), interval 30 day) then pet_id end),0) as month_patients
                from appointments
                where (:vetId is null or vet_user_id = :vetId)
                """;

        String medicationSql = """
                select
                    coalesce(sum(case when start_date = current_date() then 1 else 0 end),0) as today_meds,
                    coalesce(sum(case when start_date between current_date() and date_add(current_date(), interval 7 day) then 1 else 0 end),0) as week_meds,
                    coalesce(sum(case when start_date between current_date() and date_add(current_date(), interval 30 day) then 1 else 0 end),0) as month_meds
                from medications
                where (:vetId is null or vet_user_id = :vetId)
                """;

        Map<String, Object> appt = namedParameterJdbcTemplate.queryForMap(appointmentSql, Collections.singletonMap("vetId", vetUserId));
        Map<String, Object> meds = namedParameterJdbcTemplate.queryForMap(medicationSql, Collections.singletonMap("vetId", vetUserId));

        VetOverviewStatsResponse.PeriodStats today = VetOverviewStatsResponse.PeriodStats.builder()
                .appointments(safeNumber(appt, "today_appointments"))
                .patients(safeNumber(appt, "today_patients"))
                .medications(safeNumber(meds, "today_meds"))
                .build();
        VetOverviewStatsResponse.PeriodStats week = VetOverviewStatsResponse.PeriodStats.builder()
                .appointments(safeNumber(appt, "week_appointments"))
                .patients(safeNumber(appt, "week_patients"))
                .medications(safeNumber(meds, "week_meds"))
                .build();
        VetOverviewStatsResponse.PeriodStats month = VetOverviewStatsResponse.PeriodStats.builder()
                .appointments(safeNumber(appt, "month_appointments"))
                .patients(safeNumber(appt, "month_patients"))
                .medications(safeNumber(meds, "month_meds"))
                .build();

        return VetOverviewStatsResponse.builder()
                .today(today)
                .week(week)
                .month(month)
                .build();
    }

    @Transactional
    public void deleteMedication(Integer id, Integer userId) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用药记录不存在"));
        Pet pet = medication.getPet();
        boolean isOwner = pet != null && pet.getOwner() != null && userId != null && pet.getOwner().getUserId().equals(userId);
        boolean isVetActor = medication.getVet() != null && userId != null && medication.getVet().getUserId().equals(userId);
        if (!isOwner && !isVetActor && userId != null) {
            throw new BusinessException(403, "无权删除该用药记录");
        }
        medicationRepository.delete(medication);
    }

    private VaccinationDto toVaccinationDto(Vaccination vaccination, Pet pet) {
        return VaccinationDto.builder()
                .id(vaccination.getVaccinationId())
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .vaccineTypeId(vaccination.getVaccineTypeId())
                .vaccineName(fetchVaccineName(vaccination.getVaccineTypeId()))
                .institutionId(vaccination.getInstitutionId())
                .institutionName(fetchInstitutionName(vaccination.getInstitutionId()))
                .vetId(vaccination.getVeterinarian() != null ? vaccination.getVeterinarian().getVetId() : null)
                .vetName(fetchVetName(vaccination.getVeterinarian()))
                .vaccinationDate(vaccination.getVaccinationDate())
                .nextDueDate(vaccination.getNextDueDate())
                .lotNumber(vaccination.getLotNumber())
                .notes(vaccination.getNotes())
                .build();
    }

    private Veterinarian resolveVeterinarian(Integer vetId) {
        if (vetId == null) {
            return null;
        }
        return veterinarianRepository.findById(vetId)
                .orElseThrow(() -> new BusinessException(404, "兽医不存在"));
    }

    private String fetchVetName(Veterinarian vet) {
        if (vet == null || vet.getUser() == null) {
            return null;
        }
        return vet.getUser().getUsername();
    }

    private DewormingDto toDewormingDto(DewormingRecord record, Pet pet) {
        return DewormingDto.builder()
                .id(record.getDewormingId())
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .productId(record.getProductId())
                .productName(fetchDewormProductName(record.getProductId()))
                .sourceType(record.getSourceType())
                .institutionId(record.getInstitutionId())
                .institutionName(fetchInstitutionName(record.getInstitutionId()))
                .vetUserId(record.getVetUserId())
                .vetName(fetchUserName(record.getVetUserId()))
                .applicationDate(record.getApplicationDate())
                .nextDueDate(record.getNextDueDate())
                .dosage(record.getDosage())
                .notes(record.getNotes())
                .build();
    }

    private MedicalRecordDto toMedicalRecordDto(MedicalRecord record, Pet pet) {
        return MedicalRecordDto.builder()
                .id(record.getRecordId())
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .institutionId(record.getInstitutionId())
                .institutionName(fetchInstitutionName(record.getInstitutionId()))
                .vetUserId(record.getVetUserId())
                .vetName(fetchUserName(record.getVetUserId()))
                .visitDate(record.getVisitDate())
                .reason(record.getReason())
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .prescription(record.getPrescription())
                .cost(record.getCost())
                .paymentStatus(record.getPaymentStatus())
                .recordStatus(record.getRecordStatus())
                .followUpDate(record.getFollowUpDate())
                .build();
    }

    private VitalDto toVitalDto(WeightRecord record, Pet pet) {
        return VitalDto.builder()
                .id(record.getWeightId())
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .recordDate(record.getRecordDate())
                .metricType("体重")
                .value(record.getWeight() != null ? record.getWeight().doubleValue() : null)
                .unit("kg")
                .notes(record.getNotes())
                .build();
    }

    private MedicationDto toMedicationDto(Medication medication, Pet pet) {
        return MedicationDto.builder()
                .id(medication.getMedicationId())
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .drugName(medication.getDrugName())
                .dosage(medication.getDosage())
                .frequency(medication.getFrequency())
                .route(medication.getRoute())
                .startDate(medication.getStartDate())
                .endDate(medication.getEndDate())
                .instructions(medication.getInstructions())
                .contraindications(medication.getContraindications())
                .vetUserId(medication.getVet() != null ? medication.getVet().getUserId() : null)
                .vetName(fetchUserName(medication.getVet() != null ? medication.getVet().getUserId() : null))
                .status(StringUtils.hasText(medication.getStatus()) ? medication.getStatus() : resolveMedicationStatus(null, medication.getEndDate()))
                .build();
    }

    private List<VitalDto> loadSimpleVitals(List<Pet> pets, String type, String unit, String sql) {
        RowMapper<VitalDto> mapper = (rs, rowNum) -> VitalDto.builder()
                .id(rs.getObject("id") != null ? rs.getInt("id") : null)
                .petId(rs.getInt("pet_id"))
                .recordDate(rs.getDate("record_date").toLocalDate())
                .metricType(type)
                .value(rs.getDouble("val"))
                .unit(unit)
                .notes(rs.getString("notes"))
                .build();
        return pets.stream()
                .flatMap(p -> jdbcTemplate.query(sql, mapper, p.getPetId()).stream()
                        .peek(v -> v.setPetName(p.getPetName())))
                .collect(Collectors.toList());
    }

    private String fetchDewormProductName(Integer productId) {
        if (productId == null) {
            return null;
        }
        try {
            return jdbcTemplate.queryForObject(
                    "select product_name from deworming_products where product_id = ?",
                    String.class,
                    productId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private String resolveMedicationStatus(String status, LocalDate endDate) {
        if (StringUtils.hasText(status)) {
            return status;
        }
        if (endDate != null && endDate.isBefore(LocalDate.now())) {
            return "已完成";
        }
        return "进行中";
    }

    private int safeNumber(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }

    @Transactional
    public void deleteDeworming(Integer id, Integer userId) {
        DewormingRecord record = dewormingRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "驱虫记录不存在"));
        Integer ownerId = record.getPet() != null && record.getPet().getOwner() != null
                ? record.getPet().getOwner().getUserId()
                : null;
        if (ownerId != null && userId != null && !ownerId.equals(userId)) {
            throw new BusinessException(403, "无权删除该驱虫记录");
        }
        dewormingRecordRepository.delete(record);
    }

    private String fetchUserName(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).map(u -> u.getUsername()).orElse(null);
    }

    private String fetchInstitutionName(Integer institutionId) {
        if (institutionId == null) {
            return null;
        }
        try {
            return jdbcTemplate.queryForObject(
                    "select institution_name from medical_institutions where institution_id = ?",
                    String.class,
                    institutionId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private String fetchVaccineName(Integer vaccineTypeId) {
        if (vaccineTypeId == null) {
            return null;
        }
        try {
            return jdbcTemplate.queryForObject(
                    "select vaccine_name from vaccine_types where vaccine_type_id = ?",
                    String.class,
                    vaccineTypeId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private Integer resolveVaccineTypeId(VaccinationCreateRequest request) {
        if (request.getVaccineTypeId() != null) {
            return request.getVaccineTypeId();
        }
        if (StringUtils.hasText(request.getVaccineName())) {
            try {
                return jdbcTemplate.queryForObject(
                        "select vaccine_type_id from vaccine_types where vaccine_name = ? limit 1",
                        Integer.class,
                        request.getVaccineName());
            } catch (EmptyResultDataAccessException ex) {
                throw new BusinessException(400, "未找到该疫苗名称，请确认已存在疫苗类型");
            }
        }
        throw new BusinessException(400, "疫苗类型不能为空");
    }
}

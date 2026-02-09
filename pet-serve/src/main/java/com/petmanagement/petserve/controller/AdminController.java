package com.petmanagement.petserve.controller;

import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.entity.MedicalInstitution;
import com.petmanagement.petserve.entity.MedicationInventory;
import com.petmanagement.petserve.entity.MedicalRecord;
import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.entity.PetInsurance;
import com.petmanagement.petserve.entity.ReminderType;
import com.petmanagement.petserve.entity.User;
import com.petmanagement.petserve.entity.UserFeedback;
import com.petmanagement.petserve.entity.UserRole;
import com.petmanagement.petserve.entity.VaccineType;
import com.petmanagement.petserve.entity.DewormingProduct;
import com.petmanagement.petserve.entity.Veterinarian;
import com.petmanagement.petserve.entity.PetPhoto;
import com.petmanagement.petserve.entity.Vaccination;
import com.petmanagement.petserve.entity.WeightRecord;
import com.petmanagement.petserve.entity.TemperatureRecord;
import com.petmanagement.petserve.entity.ActivityRecord;
import com.petmanagement.petserve.entity.IntakeRecord;
import com.petmanagement.petserve.entity.DewormingRecord;
import com.petmanagement.petserve.entity.Medication;
import com.petmanagement.petserve.entity.PetPlan;
import com.petmanagement.petserve.entity.Reminder;
import com.petmanagement.petserve.entity.FinanceRecord;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.dto.pet.PetRequest;
import com.petmanagement.petserve.dto.pet.PetResponse;
import com.petmanagement.petserve.dto.appointment.AppointmentRequest;
import com.petmanagement.petserve.dto.appointment.AppointmentResponse;
import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.admin.RoleRequest;
import com.petmanagement.petserve.dto.admin.VeterinarianRequest;
import com.petmanagement.petserve.repository.MedicalInstitutionRepository;
import com.petmanagement.petserve.repository.MedicationInventoryRepository;
import com.petmanagement.petserve.repository.MedicalRecordRepository;
import com.petmanagement.petserve.repository.PetInsuranceRepository;
import com.petmanagement.petserve.repository.PetRepository;
import com.petmanagement.petserve.repository.ReminderTypeRepository;
import com.petmanagement.petserve.repository.UserFeedbackRepository;
import com.petmanagement.petserve.repository.UserRepository;
import com.petmanagement.petserve.repository.UserRoleRepository;
import com.petmanagement.petserve.repository.VaccineTypeRepository;
import com.petmanagement.petserve.repository.DewormingProductRepository;
import com.petmanagement.petserve.repository.VeterinarianRepository;
import com.petmanagement.petserve.repository.PetPhotoRepository;
import com.petmanagement.petserve.repository.VaccinationRepository;
import com.petmanagement.petserve.repository.WeightRecordRepository;
import com.petmanagement.petserve.repository.TemperatureRecordRepository;
import com.petmanagement.petserve.repository.ActivityRecordRepository;
import com.petmanagement.petserve.repository.IntakeRecordRepository;
import com.petmanagement.petserve.repository.DewormingRecordRepository;
import com.petmanagement.petserve.repository.MedicationRepository;
import com.petmanagement.petserve.repository.PetPlanRepository;
import com.petmanagement.petserve.repository.ReminderRepository;
import com.petmanagement.petserve.repository.FinanceRecordRepository;
import com.petmanagement.petserve.service.PetService;
import com.petmanagement.petserve.service.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Base64;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final MedicalInstitutionRepository medicalInstitutionRepository;
    private final VaccineTypeRepository vaccineTypeRepository;
    private final MedicationInventoryRepository medicationInventoryRepository;
    private final ReminderTypeRepository reminderTypeRepository;
    private final UserFeedbackRepository userFeedbackRepository;
    private final PetInsuranceRepository petInsuranceRepository;
    private final PetRepository petRepository;
    private final DewormingProductRepository dewormingProductRepository;
    private final DewormingRecordRepository dewormingRecordRepository;
    private final FinanceRecordRepository financeRecordRepository;
    private final MedicationRepository medicationRepository;
    private final PetPlanRepository petPlanRepository;
    private final ReminderRepository reminderRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PetPhotoRepository petPhotoRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final WeightRecordRepository weightRecordRepository;
    private final TemperatureRecordRepository temperatureRecordRepository;
    private final ActivityRecordRepository activityRecordRepository;
    private final IntakeRecordRepository intakeRecordRepository;
    private final PasswordEncoder passwordEncoder;
    private final PetService petService;
    private final AppointmentService appointmentService;

    @Autowired
    public AdminController(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           MedicalInstitutionRepository medicalInstitutionRepository,
                           VaccineTypeRepository vaccineTypeRepository,
                           MedicationInventoryRepository medicationInventoryRepository,
                           ReminderTypeRepository reminderTypeRepository,
                           UserFeedbackRepository userFeedbackRepository,
                           PetInsuranceRepository petInsuranceRepository,
                           PetRepository petRepository,
                           DewormingProductRepository dewormingProductRepository,
                           DewormingRecordRepository dewormingRecordRepository,
                           FinanceRecordRepository financeRecordRepository,
                           MedicationRepository medicationRepository,
                           PetPlanRepository petPlanRepository,
                           ReminderRepository reminderRepository,
                           VaccinationRepository vaccinationRepository,
                           MedicalRecordRepository medicalRecordRepository,
                           WeightRecordRepository weightRecordRepository,
                           TemperatureRecordRepository temperatureRecordRepository,
                           ActivityRecordRepository activityRecordRepository,
                           IntakeRecordRepository intakeRecordRepository,
                           PasswordEncoder passwordEncoder,
                           PetService petService,
                           AppointmentService appointmentService,
                           VeterinarianRepository veterinarianRepository,
                           PetPhotoRepository petPhotoRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.medicalInstitutionRepository = medicalInstitutionRepository;
        this.vaccineTypeRepository = vaccineTypeRepository;
        this.medicationInventoryRepository = medicationInventoryRepository;
        this.reminderTypeRepository = reminderTypeRepository;
        this.userFeedbackRepository = userFeedbackRepository;
        this.petInsuranceRepository = petInsuranceRepository;
        this.petRepository = petRepository;
        this.dewormingProductRepository = dewormingProductRepository;
        this.dewormingRecordRepository = dewormingRecordRepository;
        this.financeRecordRepository = financeRecordRepository;
        this.medicationRepository = medicationRepository;
        this.petPlanRepository = petPlanRepository;
        this.reminderRepository = reminderRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.weightRecordRepository = weightRecordRepository;
        this.temperatureRecordRepository = temperatureRecordRepository;
        this.activityRecordRepository = activityRecordRepository;
        this.intakeRecordRepository = intakeRecordRepository;
        this.passwordEncoder = passwordEncoder;
        this.petService = petService;
        this.appointmentService = appointmentService;
        this.veterinarianRepository = veterinarianRepository;
        this.petPhotoRepository = petPhotoRepository;
    }

    // 用户管理
    @GetMapping("/users")
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.success(userRepository.findAll());
    }

    @PostMapping("/users")
    public ApiResponse<User> createUser(@RequestBody @Valid UserRequest request) {
        UserRole role = resolveRole(request.getRoleName());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setActive(request.getActive() != null ? request.getActive() : Boolean.TRUE);
        user.setRole(role);
        if (StringUtils.hasText(request.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        } else {
            user.setPasswordHash(passwordEncoder.encode("123456"));
        }
        User saved = userRepository.save(user);
        return ApiResponse.success(saved);
    }

    @PutMapping("/users/{id}")
    public ApiResponse<User> updateUser(@PathVariable Integer id, @RequestBody @Valid UserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (StringUtils.hasText(request.getUsername())) user.setUsername(request.getUsername());
        if (StringUtils.hasText(request.getEmail())) user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setActive(request.getActive() != null ? request.getActive() : user.getActive());
        if (StringUtils.hasText(request.getRoleName())) {
            user.setRole(resolveRole(request.getRoleName()));
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        User saved = userRepository.save(user);
        return ApiResponse.success(saved);
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 医疗机构
    @GetMapping("/institutions")
    public ApiResponse<List<MedicalInstitution>> listInstitutions() {
        return ApiResponse.success(medicalInstitutionRepository.findAll());
    }

    @PostMapping("/institutions")
    public ApiResponse<MedicalInstitution> createInstitution(@RequestBody @Valid MedicalInstitution request) {
        if (request.getCreatedAt() == null) {
            throw new BusinessException(400, "创立时间不能为空");
        }
        return ApiResponse.success(medicalInstitutionRepository.save(request));
    }

    @PutMapping("/institutions/{id}")
    public ApiResponse<MedicalInstitution> updateInstitution(@PathVariable Integer id, @RequestBody MedicalInstitution request) {
        MedicalInstitution exist = medicalInstitutionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "机构不存在"));
        exist.setInstitutionName(request.getInstitutionName());
        exist.setInstitutionType(request.getInstitutionType());
        exist.setAddress(request.getAddress());
        exist.setPhone(request.getPhone());
        exist.setEmail(request.getEmail());
        exist.setLicenseNumber(request.getLicenseNumber());
        exist.setDescription(request.getDescription());
        exist.setVerified(request.getVerified());
        if (request.getCreatedAt() == null) {
            throw new BusinessException(400, "创立时间不能为空");
        }
        exist.setCreatedAt(request.getCreatedAt());
        return ApiResponse.success(medicalInstitutionRepository.save(exist));
    }

    @DeleteMapping("/institutions/{id}")
    public ApiResponse<Void> deleteInstitution(@PathVariable Integer id) {
        medicalInstitutionRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 疫苗类型
    @GetMapping("/vaccine-types")
    public ApiResponse<List<VaccineType>> listVaccineTypes() {
        return ApiResponse.success(vaccineTypeRepository.findAll());
    }

    @PostMapping("/vaccine-types")
    public ApiResponse<VaccineType> createVaccineType(@RequestBody @Valid VaccineType request) {
        return ApiResponse.success(vaccineTypeRepository.save(request));
    }

    @PutMapping("/vaccine-types/{id}")
    public ApiResponse<VaccineType> updateVaccineType(@PathVariable Integer id, @RequestBody VaccineType request) {
        VaccineType exist = vaccineTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "疫苗类型不存在"));
        exist.setVaccineName(request.getVaccineName());
        exist.setSpecies(request.getSpecies());
        exist.setValidityPeriodMonths(request.getValidityPeriodMonths());
        exist.setDescription(request.getDescription());
        exist.setActive(request.getActive());
        return ApiResponse.success(vaccineTypeRepository.save(exist));
    }

    @DeleteMapping("/vaccine-types/{id}")
    public ApiResponse<Void> deleteVaccineType(@PathVariable Integer id) {
        vaccineTypeRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 角色管理
    @GetMapping("/roles")
    public ApiResponse<List<UserRole>> listRoles() {
        return ApiResponse.success(userRoleRepository.findAll());
    }

    @PostMapping("/roles")
    public ApiResponse<UserRole> createRole(@RequestBody @Valid RoleRequest request) {
        UserRole role = new UserRole();
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        return ApiResponse.success(userRoleRepository.save(role));
    }

    @PutMapping("/roles/{id}")
    public ApiResponse<UserRole> updateRole(@PathVariable Integer id, @RequestBody @Valid RoleRequest request) {
        UserRole exist = userRoleRepository.findById(id).orElseThrow(() -> new BusinessException(404, "角色不存在"));
        if (StringUtils.hasText(request.getRoleName())) exist.setRoleName(request.getRoleName());
        exist.setDescription(request.getDescription());
        return ApiResponse.success(userRoleRepository.save(exist));
    }

    @DeleteMapping("/roles/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Integer id) {
        userRoleRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 兽医管理
    @GetMapping("/veterinarians")
    public ApiResponse<List<Veterinarian>> listVeterinarians() {
        return ApiResponse.success(veterinarianRepository.findAll());
    }

    @PostMapping("/veterinarians")
    public ApiResponse<Veterinarian> createVeterinarian(@RequestBody @Valid VeterinarianRequest request) {
        Veterinarian vet = new Veterinarian();
        vet.setUser(userRepository.findById(request.getUserId()).orElseThrow(() -> new BusinessException(404, "用户不存在")));
        vet.setInstitution(medicalInstitutionRepository.findById(request.getInstitutionId()).orElseThrow(() -> new BusinessException(404, "机构不存在")));
        vet.setLicenseNumber(request.getLicenseNumber());
        vet.setSpecialization(request.getSpecialization());
        vet.setYearsExperience(request.getYearsExperience());
        vet.setQualification(request.getQualification());
        vet.setPosition(request.getPosition());
        vet.setVerified(request.getVerified());
        return ApiResponse.success(veterinarianRepository.save(vet));
    }

    @PutMapping("/veterinarians/{id}")
    public ApiResponse<Veterinarian> updateVeterinarian(@PathVariable Integer id, @RequestBody @Valid VeterinarianRequest request) {
        Veterinarian vet = veterinarianRepository.findById(id).orElseThrow(() -> new BusinessException(404, "兽医不存在"));
        if (request.getUserId() != null) {
            vet.setUser(userRepository.findById(request.getUserId()).orElseThrow(() -> new BusinessException(404, "用户不存在")));
        }
        if (request.getInstitutionId() != null) {
            vet.setInstitution(medicalInstitutionRepository.findById(request.getInstitutionId()).orElseThrow(() -> new BusinessException(404, "机构不存在")));
        }
        if (StringUtils.hasText(request.getLicenseNumber())) vet.setLicenseNumber(request.getLicenseNumber());
        vet.setSpecialization(request.getSpecialization());
        vet.setYearsExperience(request.getYearsExperience());
        vet.setQualification(request.getQualification());
        vet.setPosition(request.getPosition());
        vet.setVerified(request.getVerified());
        return ApiResponse.success(veterinarianRepository.save(vet));
    }

    @DeleteMapping("/veterinarians/{id}")
    public ApiResponse<Void> deleteVeterinarian(@PathVariable Integer id) {
        veterinarianRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 预约管理（管理员）
    @GetMapping("/appointments")
    public ApiResponse<PageResponse<AppointmentResponse>> listAppointments(@RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "100") int size) {
        return ApiResponse.success(appointmentService.listAllForAdmin(page, size));
    }

    @PostMapping("/appointments")
    public ApiResponse<AppointmentResponse> createAppointment(@RequestBody @Valid AppointmentRequest request) {
        return ApiResponse.success(appointmentService.createByAdmin(request));
    }

    @PutMapping("/appointments/{id}")
    public ApiResponse<AppointmentResponse> updateAppointment(@PathVariable Integer id,
                                                              @RequestBody @Valid AppointmentRequest request) {
        return ApiResponse.success(appointmentService.updateByAdmin(id, request));
    }

    @DeleteMapping("/appointments/{id}")
    public ApiResponse<Void> deleteAppointment(@PathVariable Integer id) {
        appointmentService.deleteByAdmin(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/appointments/{id}/confirm")
    public ApiResponse<AppointmentResponse> confirmAppointment(@PathVariable Integer id) {
        return ApiResponse.success(appointmentService.confirmByAdmin(id));
    }

    // 药品库存（药品管理）
    @GetMapping("/medications")
    public ApiResponse<List<MedicationInventory>> listMedications() {
        return ApiResponse.success(medicationInventoryRepository.findAll());
    }

    @PostMapping("/medications")
    public ApiResponse<MedicationInventory> createMedication(@RequestBody MedicationInventory request) {
        return ApiResponse.success(medicationInventoryRepository.save(request));
    }

    @PutMapping("/medications/{id}")
    public ApiResponse<MedicationInventory> updateMedication(@PathVariable Integer id, @RequestBody MedicationInventory request) {
        MedicationInventory exist = medicationInventoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "药品不存在"));
        exist.setMedicationName(request.getMedicationName());
        exist.setMedicationType(request.getMedicationType());
        exist.setInstitutionId(request.getInstitutionId());
        exist.setQuantity(request.getQuantity());
        exist.setUnit(request.getUnit());
        exist.setExpiryDate(request.getExpiryDate());
        exist.setPurchaseDate(request.getPurchaseDate());
        exist.setSupplier(request.getSupplier());
        exist.setUnitPrice(request.getUnitPrice());
        exist.setStorageLocation(request.getStorageLocation());
        return ApiResponse.success(medicationInventoryRepository.save(exist));
    }

    @DeleteMapping("/medications/{id}")
    public ApiResponse<Void> deleteMedication(@PathVariable Integer id) {
        medicationInventoryRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 驱虫药品
    @GetMapping("/deworming-products")
    public ApiResponse<List<DewormingProduct>> listDewormingProducts() {
        return ApiResponse.success(dewormingProductRepository.findAll());
    }

    @PostMapping("/deworming-products")
    public ApiResponse<DewormingProduct> createDewormingProduct(@RequestBody DewormingProduct request) {
        return ApiResponse.success(dewormingProductRepository.save(request));
    }

    @PutMapping("/deworming-products/{id}")
    public ApiResponse<DewormingProduct> updateDewormingProduct(@PathVariable Integer id, @RequestBody DewormingProduct request) {
        DewormingProduct exist = dewormingProductRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "驱虫药品不存在"));
        exist.setProductName(request.getProductName());
        exist.setDewormingType(request.getDewormingType());
        exist.setSpecies(request.getSpecies());
        exist.setValidityPeriodDays(request.getValidityPeriodDays());
        exist.setDosageGuide(request.getDosageGuide());
        exist.setActive(request.getActive());
        return ApiResponse.success(dewormingProductRepository.save(exist));
    }

    @DeleteMapping("/deworming-products/{id}")
    public ApiResponse<Void> deleteDewormingProduct(@PathVariable Integer id) {
        dewormingProductRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 提醒类型
    @GetMapping("/reminder-types")
    public ApiResponse<List<ReminderType>> listReminderTypes() {
        return ApiResponse.success(reminderTypeRepository.findAll());
    }

    @PostMapping("/reminder-types")
    public ApiResponse<ReminderType> createReminderType(@RequestBody ReminderType request) {
        return ApiResponse.success(reminderTypeRepository.save(request));
    }

    @PutMapping("/reminder-types/{id}")
    public ApiResponse<ReminderType> updateReminderType(@PathVariable Integer id, @RequestBody ReminderType request) {
        ReminderType exist = reminderTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "提醒类型不存在"));
        exist.setTypeName(request.getTypeName());
        exist.setSourceTable(request.getSourceTable());
        exist.setSourceField(request.getSourceField());
        exist.setAdvanceDays(request.getAdvanceDays());
        exist.setTemplateMessage(request.getTemplateMessage());
        exist.setAutoGenerated(request.getAutoGenerated());
        return ApiResponse.success(reminderTypeRepository.save(exist));
    }

    @DeleteMapping("/reminder-types/{id}")
    public ApiResponse<Void> deleteReminderType(@PathVariable Integer id) {
        reminderTypeRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 疫苗接种记录
    @GetMapping("/vaccinations")
    public ApiResponse<List<VaccinationAdminResponse>> listVaccinations() {
        List<Vaccination> list = vaccinationRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toVaccinationResponse).toList());
    }

    @PostMapping("/vaccinations")
    public ApiResponse<VaccinationAdminResponse> createVaccination(@RequestBody VaccinationAdminRequest request) {
        Vaccination vaccination = new Vaccination();
        fillVaccination(vaccination, request);
        vaccination.setCreatedAt(LocalDateTime.now());
        Vaccination saved = vaccinationRepository.save(vaccination);
        return ApiResponse.success(toVaccinationResponse(saved));
    }

    @PutMapping("/vaccinations/{id}")
    public ApiResponse<VaccinationAdminResponse> updateVaccination(@PathVariable Integer id, @RequestBody VaccinationAdminRequest request) {
        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "接种记录不存在"));
        fillVaccination(vaccination, request);
        Vaccination saved = vaccinationRepository.save(vaccination);
        return ApiResponse.success(toVaccinationResponse(saved));
    }

    @DeleteMapping("/vaccinations/{id}")
    public ApiResponse<Void> deleteVaccination(@PathVariable Integer id) {
        vaccinationRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    private void fillVaccination(Vaccination vaccination, VaccinationAdminRequest request) {
        if (request.getPetId() != null) {
        vaccination.setPet(resolvePet(request.getPetId()));
        }
        vaccination.setVaccineTypeId(request.getVaccineTypeId());
        vaccination.setInstitutionId(request.getInstitutionId());
        if (request.getVetUserId() != null) {
            vaccination.setVeterinarian(resolveVeterinarianByUserId(request.getVetUserId()));
        } else {
            vaccination.setVeterinarian(null);
        }
        vaccination.setVaccinationDate(request.getVaccinationDate());
        vaccination.setNextDueDate(request.getNextDueDate());
        vaccination.setLotNumber(request.getLotNumber());
        vaccination.setNotes(request.getNotes());
        if (request.getCreatedBy() != null) vaccination.setCreatedBy(request.getCreatedBy());
        if (request.getCreatedAt() != null) vaccination.setCreatedAt(request.getCreatedAt());
    }

    private VaccinationAdminResponse toVaccinationResponse(Vaccination vaccination) {
        VaccinationAdminResponse resp = new VaccinationAdminResponse();
        resp.setVaccinationId(vaccination.getVaccinationId());
        resp.setPetId(vaccination.getPet() != null ? vaccination.getPet().getPetId() : null);
        resp.setPetName(vaccination.getPet() != null ? vaccination.getPet().getPetName() : null);
        resp.setVaccineTypeId(vaccination.getVaccineTypeId());
        resp.setInstitutionId(vaccination.getInstitutionId());
        resp.setVetUserId(vaccination.getVeterinarian() != null && vaccination.getVeterinarian().getUser() != null
                ? vaccination.getVeterinarian().getUser().getUserId()
                : null);
        resp.setVaccinationDate(vaccination.getVaccinationDate());
        resp.setNextDueDate(vaccination.getNextDueDate());
        resp.setLotNumber(vaccination.getLotNumber());
        resp.setNotes(vaccination.getNotes());
        resp.setCreatedBy(vaccination.getCreatedBy());
        resp.setCreatedAt(vaccination.getCreatedAt());
        return resp;
    }

    // 医疗记录
    @GetMapping("/medical-records")
    public ApiResponse<List<MedicalRecordAdminResponse>> listMedicalRecords() {
        List<MedicalRecord> list = medicalRecordRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toMedicalRecordResponse).toList());
    }

    @PostMapping("/medical-records")
    public ApiResponse<MedicalRecordAdminResponse> createMedicalRecord(@RequestBody MedicalRecordAdminRequest request) {
        MedicalRecord record = new MedicalRecord();
        fillMedicalRecord(record, request);
        record.setCreatedAt(LocalDateTime.now());
        MedicalRecord saved = medicalRecordRepository.save(record);
        return ApiResponse.success(toMedicalRecordResponse(saved));
    }

    @PutMapping("/medical-records/{id}")
    public ApiResponse<MedicalRecordAdminResponse> updateMedicalRecord(@PathVariable Integer id, @RequestBody MedicalRecordAdminRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "医疗记录不存在"));
        fillMedicalRecord(record, request);
        MedicalRecord saved = medicalRecordRepository.save(record);
        return ApiResponse.success(toMedicalRecordResponse(saved));
    }

    @DeleteMapping("/medical-records/{id}")
    public ApiResponse<Void> deleteMedicalRecord(@PathVariable Integer id) {
        medicalRecordRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    private void fillMedicalRecord(MedicalRecord record, MedicalRecordAdminRequest request) {
        if (request.getPetId() != null) {
            record.setPet(resolvePet(request.getPetId()));
        }
        record.setInstitutionId(request.getInstitutionId());
        record.setVetUserId(request.getVetUserId());
        record.setVisitDate(request.getVisitDate());
        record.setReason(request.getReason());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setPrescription(request.getPrescription());
        record.setCost(request.getCost());
        record.setPaymentStatus(request.getPaymentStatus());
        record.setRecordStatus(request.getRecordStatus());
        record.setInsuranceClaimId(request.getInsuranceClaimId());
        record.setFollowUpDate(request.getFollowUpDate());
        if (request.getCreatedAt() != null) record.setCreatedAt(request.getCreatedAt());
    }

    private MedicalRecordAdminResponse toMedicalRecordResponse(MedicalRecord record) {
        MedicalRecordAdminResponse resp = new MedicalRecordAdminResponse();
        resp.setRecordId(record.getRecordId());
        resp.setPetId(record.getPet() != null ? record.getPet().getPetId() : null);
        resp.setPetName(record.getPet() != null ? record.getPet().getPetName() : null);
        resp.setInstitutionId(record.getInstitutionId());
        resp.setVetUserId(record.getVetUserId());
        resp.setVisitDate(record.getVisitDate());
        resp.setReason(record.getReason());
        resp.setDiagnosis(record.getDiagnosis());
        resp.setTreatment(record.getTreatment());
        resp.setPrescription(record.getPrescription());
        resp.setCost(record.getCost());
        resp.setPaymentStatus(record.getPaymentStatus());
        resp.setRecordStatus(record.getRecordStatus());
        resp.setInsuranceClaimId(record.getInsuranceClaimId());
        resp.setFollowUpDate(record.getFollowUpDate());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }

    // 体重记录
    @GetMapping("/weight-records")
    public ApiResponse<List<WeightRecordAdminResponse>> listWeightRecords() {
        List<WeightRecord> list = weightRecordRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toWeightRecordResponse).toList());
    }

    @PostMapping("/weight-records")
    public ApiResponse<WeightRecordAdminResponse> createWeightRecord(@RequestBody WeightRecordAdminRequest request) {
        WeightRecord record = new WeightRecord();
        fillWeightRecord(record, request);
        record.setCreatedAt(LocalDateTime.now());
        WeightRecord saved = weightRecordRepository.save(record);
        return ApiResponse.success(toWeightRecordResponse(saved));
    }

    @PutMapping("/weight-records/{id}")
    public ApiResponse<WeightRecordAdminResponse> updateWeightRecord(@PathVariable Integer id, @RequestBody WeightRecordAdminRequest request) {
        WeightRecord record = weightRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "体重记录不存在"));
        fillWeightRecord(record, request);
        WeightRecord saved = weightRecordRepository.save(record);
        return ApiResponse.success(toWeightRecordResponse(saved));
    }

    @DeleteMapping("/weight-records/{id}")
    public ApiResponse<Void> deleteWeightRecord(@PathVariable Integer id) {
        weightRecordRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    private void fillWeightRecord(WeightRecord record, WeightRecordAdminRequest request) {
        if (request.getPetId() != null) {
            record.setPet(resolvePet(request.getPetId()));
        }
        record.setWeight(request.getWeight());
        record.setRecordDate(request.getRecordDate());
        record.setNotes(request.getNotes());
        record.setRecordedBy(request.getRecordedBy());
        if (request.getCreatedAt() != null) record.setCreatedAt(request.getCreatedAt());
    }

    private WeightRecordAdminResponse toWeightRecordResponse(WeightRecord record) {
        WeightRecordAdminResponse resp = new WeightRecordAdminResponse();
        resp.setWeightId(record.getWeightId());
        resp.setPetId(record.getPet() != null ? record.getPet().getPetId() : null);
        resp.setPetName(record.getPet() != null ? record.getPet().getPetName() : null);
        resp.setWeight(record.getWeight());
        resp.setRecordDate(record.getRecordDate());
        resp.setNotes(record.getNotes());
        resp.setRecordedBy(record.getRecordedBy());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }

    // 体温记录
    @GetMapping("/temperature-records")
    public ApiResponse<List<TemperatureRecordAdminResponse>> listTemperatureRecords() {
        List<TemperatureRecord> list = temperatureRecordRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toTemperatureRecordResponse).toList());
    }

    @PostMapping("/temperature-records")
    public ApiResponse<TemperatureRecordAdminResponse> createTemperatureRecord(@RequestBody TemperatureRecordAdminRequest request) {
        TemperatureRecord record = new TemperatureRecord();
        fillTemperatureRecord(record, request);
        record.setCreatedAt(LocalDateTime.now());
        TemperatureRecord saved = temperatureRecordRepository.save(record);
        return ApiResponse.success(toTemperatureRecordResponse(saved));
    }

    @PutMapping("/temperature-records/{id}")
    public ApiResponse<TemperatureRecordAdminResponse> updateTemperatureRecord(@PathVariable Integer id, @RequestBody TemperatureRecordAdminRequest request) {
        TemperatureRecord record = temperatureRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "体温记录不存在"));
        fillTemperatureRecord(record, request);
        TemperatureRecord saved = temperatureRecordRepository.save(record);
        return ApiResponse.success(toTemperatureRecordResponse(saved));
    }

    @DeleteMapping("/temperature-records/{id}")
    public ApiResponse<Void> deleteTemperatureRecord(@PathVariable Integer id) {
        temperatureRecordRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    private void fillTemperatureRecord(TemperatureRecord record, TemperatureRecordAdminRequest request) {
        if (request.getPetId() != null) {
            record.setPet(resolvePet(request.getPetId()));
        }
        record.setTemperature(request.getTemperature());
        record.setRecordDate(request.getRecordDate());
        record.setNotes(request.getNotes());
        record.setRecordedBy(request.getRecordedBy());
        if (request.getCreatedAt() != null) record.setCreatedAt(request.getCreatedAt());
    }

    private TemperatureRecordAdminResponse toTemperatureRecordResponse(TemperatureRecord record) {
        TemperatureRecordAdminResponse resp = new TemperatureRecordAdminResponse();
        resp.setTemperatureId(record.getTemperatureId());
        resp.setPetId(record.getPet() != null ? record.getPet().getPetId() : null);
        resp.setPetName(record.getPet() != null ? record.getPet().getPetName() : null);
        resp.setTemperature(record.getTemperature());
        resp.setRecordDate(record.getRecordDate());
        resp.setNotes(record.getNotes());
        resp.setRecordedBy(record.getRecordedBy());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }

    // 活动记录
    @GetMapping("/activity-records")
    public ApiResponse<List<ActivityRecordAdminResponse>> listActivityRecords() {
        List<ActivityRecord> list = activityRecordRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toActivityRecordResponse).toList());
    }

    @PostMapping("/activity-records")
    public ApiResponse<ActivityRecordAdminResponse> createActivityRecord(@RequestBody ActivityRecordAdminRequest request) {
        ActivityRecord record = new ActivityRecord();
        fillActivityRecord(record, request);
        record.setCreatedAt(LocalDateTime.now());
        ActivityRecord saved = activityRecordRepository.save(record);
        return ApiResponse.success(toActivityRecordResponse(saved));
    }

    @PutMapping("/activity-records/{id}")
    public ApiResponse<ActivityRecordAdminResponse> updateActivityRecord(@PathVariable Integer id, @RequestBody ActivityRecordAdminRequest request) {
        ActivityRecord record = activityRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "活动记录不存在"));
        fillActivityRecord(record, request);
        ActivityRecord saved = activityRecordRepository.save(record);
        return ApiResponse.success(toActivityRecordResponse(saved));
    }

    @DeleteMapping("/activity-records/{id}")
    public ApiResponse<Void> deleteActivityRecord(@PathVariable Integer id) {
        activityRecordRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    private void fillActivityRecord(ActivityRecord record, ActivityRecordAdminRequest request) {
        if (request.getPetId() != null) {
            record.setPet(resolvePet(request.getPetId()));
        }
        record.setActivityLevel(request.getActivityLevel());
        record.setRecordDate(request.getRecordDate());
        record.setNotes(request.getNotes());
        record.setRecordedBy(request.getRecordedBy());
        if (request.getCreatedAt() != null) record.setCreatedAt(request.getCreatedAt());
    }

    private ActivityRecordAdminResponse toActivityRecordResponse(ActivityRecord record) {
        ActivityRecordAdminResponse resp = new ActivityRecordAdminResponse();
        resp.setActivityId(record.getActivityId());
        resp.setPetId(record.getPet() != null ? record.getPet().getPetId() : null);
        resp.setPetName(record.getPet() != null ? record.getPet().getPetName() : null);
        resp.setActivityLevel(record.getActivityLevel());
        resp.setRecordDate(record.getRecordDate());
        resp.setNotes(record.getNotes());
        resp.setRecordedBy(record.getRecordedBy());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }

    // 摄入记录
    @GetMapping("/intake-records")
    public ApiResponse<List<IntakeRecordAdminResponse>> listIntakeRecords() {
        List<IntakeRecord> list = intakeRecordRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toIntakeRecordResponse).toList());
    }

    @PostMapping("/intake-records")
    public ApiResponse<IntakeRecordAdminResponse> createIntakeRecord(@RequestBody IntakeRecordAdminRequest request) {
        IntakeRecord record = new IntakeRecord();
        fillIntakeRecord(record, request);
        record.setCreatedAt(LocalDateTime.now());
        IntakeRecord saved = intakeRecordRepository.save(record);
        return ApiResponse.success(toIntakeRecordResponse(saved));
    }

    @PutMapping("/intake-records/{id}")
    public ApiResponse<IntakeRecordAdminResponse> updateIntakeRecord(@PathVariable Integer id, @RequestBody IntakeRecordAdminRequest request) {
        IntakeRecord record = intakeRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "摄入记录不存在"));
        fillIntakeRecord(record, request);
        IntakeRecord saved = intakeRecordRepository.save(record);
        return ApiResponse.success(toIntakeRecordResponse(saved));
    }

    @DeleteMapping("/intake-records/{id}")
    public ApiResponse<Void> deleteIntakeRecord(@PathVariable Integer id) {
        intakeRecordRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 驱虫记录
    @GetMapping("/deworming-records")
    public ApiResponse<List<DewormingRecordAdminResponse>> listDewormingRecords() {
        List<DewormingRecord> list = dewormingRecordRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toDewormingResponse).toList());
    }

    @PostMapping("/deworming-records")
    public ApiResponse<DewormingRecordAdminResponse> createDewormingRecord(@RequestBody DewormingRecordAdminRequest request) {
        DewormingRecord record = new DewormingRecord();
        fillDewormingRecord(record, request);
        record.setCreatedAt(LocalDateTime.now());
        DewormingRecord saved = dewormingRecordRepository.save(record);
        return ApiResponse.success(toDewormingResponse(saved));
    }

    @PutMapping("/deworming-records/{id}")
    public ApiResponse<DewormingRecordAdminResponse> updateDewormingRecord(@PathVariable Integer id, @RequestBody DewormingRecordAdminRequest request) {
        DewormingRecord record = dewormingRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "驱虫记录不存在"));
        fillDewormingRecord(record, request);
        DewormingRecord saved = dewormingRecordRepository.save(record);
        return ApiResponse.success(toDewormingResponse(saved));
    }

    @DeleteMapping("/deworming-records/{id}")
    public ApiResponse<Void> deleteDewormingRecord(@PathVariable Integer id) {
        dewormingRecordRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 财务记录
    @GetMapping("/finance-records")
    public ApiResponse<List<FinanceRecordAdminResponse>> listFinanceRecords() {
        List<FinanceRecord> list = financeRecordRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toFinanceRecordResponse).toList());
    }

    @PostMapping("/finance-records")
    public ApiResponse<FinanceRecordAdminResponse> createFinanceRecord(@RequestBody FinanceRecordAdminRequest request) {
        FinanceRecord record = new FinanceRecord();
        fillFinanceRecord(record, request);
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }
        FinanceRecord saved = financeRecordRepository.save(record);
        return ApiResponse.success(toFinanceRecordResponse(saved));
    }

    @PutMapping("/finance-records/{id}")
    public ApiResponse<FinanceRecordAdminResponse> updateFinanceRecord(@PathVariable Integer id, @RequestBody FinanceRecordAdminRequest request) {
        FinanceRecord record = financeRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "财务记录不存在"));
        fillFinanceRecord(record, request);
        FinanceRecord saved = financeRecordRepository.save(record);
        return ApiResponse.success(toFinanceRecordResponse(saved));
    }

    @DeleteMapping("/finance-records/{id}")
    public ApiResponse<Void> deleteFinanceRecord(@PathVariable Integer id) {
        financeRecordRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 宠物用药记录
    @GetMapping("/pet-medications")
    public ApiResponse<List<MedicationAdminResponse>> listPetMedications() {
        List<Medication> list = medicationRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toMedicationResponse).toList());
    }

    @PostMapping("/pet-medications")
    public ApiResponse<MedicationAdminResponse> createPetMedication(@RequestBody MedicationAdminRequest request) {
        Medication record = new Medication();
        fillPetMedication(record, request);
        if (record.getCreatedAt() == null) record.setCreatedAt(LocalDateTime.now());
        Medication saved = medicationRepository.save(record);
        return ApiResponse.success(toMedicationResponse(saved));
    }

    @PutMapping("/pet-medications/{id}")
    public ApiResponse<MedicationAdminResponse> updatePetMedication(@PathVariable Integer id, @RequestBody MedicationAdminRequest request) {
        Medication record = medicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用药记录不存在"));
        fillPetMedication(record, request);
        Medication saved = medicationRepository.save(record);
        return ApiResponse.success(toMedicationResponse(saved));
    }

    @DeleteMapping("/pet-medications/{id}")
    public ApiResponse<Void> deletePetMedication(@PathVariable Integer id) {
        medicationRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 宠物计划
    @GetMapping("/pet-plans")
    public ApiResponse<List<PetPlanAdminResponse>> listPetPlans() {
        List<PetPlan> list = petPlanRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toPetPlanResponse).toList());
    }

    @PostMapping("/pet-plans")
    public ApiResponse<PetPlanAdminResponse> createPetPlan(@RequestBody PetPlanAdminRequest request) {
        PetPlan plan = new PetPlan();
        fillPetPlan(plan, request);
        if (plan.getCreatedAt() == null) plan.setCreatedAt(LocalDateTime.now());
        PetPlan saved = petPlanRepository.save(plan);
        return ApiResponse.success(toPetPlanResponse(saved));
    }

    @PutMapping("/pet-plans/{id}")
    public ApiResponse<PetPlanAdminResponse> updatePetPlan(@PathVariable Integer id, @RequestBody PetPlanAdminRequest request) {
        PetPlan plan = petPlanRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "宠物计划不存在"));
        fillPetPlan(plan, request);
        PetPlan saved = petPlanRepository.save(plan);
        return ApiResponse.success(toPetPlanResponse(saved));
    }

    @DeleteMapping("/pet-plans/{id}")
    public ApiResponse<Void> deletePetPlan(@PathVariable Integer id) {
        petPlanRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 提醒记录
    @GetMapping("/reminders")
    public ApiResponse<List<ReminderAdminResponse>> listReminders() {
        List<Reminder> list = reminderRepository.findAll();
        return ApiResponse.success(list.stream().map(this::toReminderResponse).toList());
    }

    @PostMapping("/reminders")
    public ApiResponse<ReminderAdminResponse> createReminder(@RequestBody ReminderAdminRequest request) {
        Reminder reminder = new Reminder();
        fillReminder(reminder, request);
        if (reminder.getCreatedAt() == null) reminder.setCreatedAt(LocalDateTime.now());
        Reminder saved = reminderRepository.save(reminder);
        return ApiResponse.success(toReminderResponse(saved));
    }

    @PutMapping("/reminders/{id}")
    public ApiResponse<ReminderAdminResponse> updateReminder(@PathVariable Integer id, @RequestBody ReminderAdminRequest request) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "提醒不存在"));
        fillReminder(reminder, request);
        Reminder saved = reminderRepository.save(reminder);
        return ApiResponse.success(toReminderResponse(saved));
    }

    @DeleteMapping("/reminders/{id}")
    public ApiResponse<Void> deleteReminder(@PathVariable Integer id) {
        reminderRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    private void fillFinanceRecord(FinanceRecord record, FinanceRecordAdminRequest request) {
        if (request.getUserId() != null) {
            record.setOwner(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException(404, "用户不存在")));
        }
        if (request.getPetId() != null) {
            record.setPet(resolvePet(request.getPetId()));
        }
        record.setTitle(request.getTitle());
        record.setRecordType(request.getRecordType());
        record.setMedicalRecordId(request.getMedicalRecordId());
        record.setCategory(request.getCategory());
        record.setAmount(request.getAmount());
        record.setRecordDate(request.getRecordDate());
        record.setStatus(request.getStatus());
        record.setNotes(request.getNotes());
        if (request.getCreatedAt() != null) record.setCreatedAt(request.getCreatedAt());
    }

    private FinanceRecordAdminResponse toFinanceRecordResponse(FinanceRecord record) {
        FinanceRecordAdminResponse resp = new FinanceRecordAdminResponse();
        resp.setRecordId(record.getRecordId());
        resp.setUserId(record.getOwner() != null ? record.getOwner().getUserId() : null);
        resp.setOwnerName(record.getOwner() != null ? record.getOwner().getUsername() : null);
        resp.setPetId(record.getPet() != null ? record.getPet().getPetId() : null);
        resp.setPetName(record.getPet() != null ? record.getPet().getPetName() : null);
        resp.setTitle(record.getTitle());
        resp.setRecordType(record.getRecordType());
        resp.setMedicalRecordId(record.getMedicalRecordId());
        resp.setCategory(record.getCategory());
        resp.setAmount(record.getAmount());
        resp.setRecordDate(record.getRecordDate());
        resp.setStatus(record.getStatus());
        resp.setNotes(record.getNotes());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }

    private void fillPetMedication(Medication record, MedicationAdminRequest request) {
        if (request.getPetId() != null) {
            record.setPet(resolvePet(request.getPetId()));
        }
        record.setDrugName(request.getDrugName());
        record.setDosage(request.getDosage());
        record.setFrequency(request.getFrequency());
        record.setRoute(request.getRoute());
        record.setStartDate(request.getStartDate());
        record.setEndDate(request.getEndDate());
        record.setInstructions(request.getInstructions());
        record.setContraindications(request.getContraindications());
        if (request.getCreatedAt() != null) record.setCreatedAt(request.getCreatedAt());
    }

    private MedicationAdminResponse toMedicationResponse(Medication medication) {
        MedicationAdminResponse resp = new MedicationAdminResponse();
        resp.setMedicationId(medication.getMedicationId());
        resp.setPetId(medication.getPet() != null ? medication.getPet().getPetId() : null);
        resp.setPetName(medication.getPet() != null ? medication.getPet().getPetName() : null);
        resp.setDrugName(medication.getDrugName());
        resp.setDosage(medication.getDosage());
        resp.setFrequency(medication.getFrequency());
        resp.setRoute(medication.getRoute());
        resp.setStartDate(medication.getStartDate());
        resp.setEndDate(medication.getEndDate());
        resp.setInstructions(medication.getInstructions());
        resp.setContraindications(medication.getContraindications());
        resp.setCreatedAt(medication.getCreatedAt());
        return resp;
    }

    private void fillPetPlan(PetPlan plan, PetPlanAdminRequest request) {
        if (request.getPetId() != null) {
            plan.setPet(resolvePet(request.getPetId()));
        }
        plan.setPlanType(request.getPlanType());
        plan.setTitle(request.getTitle());
        plan.setTarget(request.getTarget());
        plan.setFrequency(request.getFrequency());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setNotes(request.getNotes());
        if (request.getCreatedAt() != null) plan.setCreatedAt(request.getCreatedAt());
        if (request.getCreatedBy() != null) {
            plan.setCreatedBy(userRepository.findById(request.getCreatedBy())
                    .orElseThrow(() -> new BusinessException(404, "创建人不存在")));
        }
    }

    private PetPlanAdminResponse toPetPlanResponse(PetPlan plan) {
        PetPlanAdminResponse resp = new PetPlanAdminResponse();
        resp.setPlanId(plan.getPlanId());
        resp.setPetId(plan.getPet() != null ? plan.getPet().getPetId() : null);
        resp.setPetName(plan.getPet() != null ? plan.getPet().getPetName() : null);
        resp.setPlanType(plan.getPlanType());
        resp.setTitle(plan.getTitle());
        resp.setTarget(plan.getTarget());
        resp.setFrequency(plan.getFrequency());
        resp.setStartDate(plan.getStartDate());
        resp.setEndDate(plan.getEndDate());
        resp.setNotes(plan.getNotes());
        resp.setCreatedAt(plan.getCreatedAt());
        resp.setCreatedBy(plan.getCreatedBy() != null ? plan.getCreatedBy().getUserId() : null);
        resp.setCreatedByName(plan.getCreatedBy() != null ? plan.getCreatedBy().getUsername() : null);
        return resp;
    }

    private void fillReminder(Reminder reminder, ReminderAdminRequest request) {
        if (request.getReminderTypeId() != null) {
            reminder.setReminderType(reminderTypeRepository.findById(request.getReminderTypeId())
                    .orElseThrow(() -> new BusinessException(404, "提醒类型不存在")));
        }
        if (request.getPetId() != null) {
            reminder.setPet(resolvePet(request.getPetId()));
        }
        if (request.getUserId() != null) {
            reminder.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException(404, "用户不存在")));
        }
        if (reminder.getUser() == null && reminder.getPet() != null && reminder.getPet().getOwner() != null) {
            reminder.setUser(reminder.getPet().getOwner());
        }
        if (reminder.getPet() != null && reminder.getUser() != null
                && reminder.getPet().getOwner() != null
                && !reminder.getPet().getOwner().getUserId().equals(reminder.getUser().getUserId())) {
            throw new BusinessException(400, "提醒所属用户必须是宠物主人");
        }
        if (request.getVetId() != null) {
            reminder.setVeterinarian(veterinarianRepository.findById(request.getVetId())
                    .orElseThrow(() -> new BusinessException(404, "兽医不存在")));
        }
        reminder.setSourceTable(request.getSourceTable());
        reminder.setSourceRecordId(request.getSourceRecordId());
        reminder.setTitle(request.getTitle());
        reminder.setMessage(request.getMessage());
        reminder.setDueDate(request.getDueDate());
        reminder.setReminderDate(request.getReminderDate());
        reminder.setCompleted(request.getCompleted());
        reminder.setCompletedDate(request.getCompletedDate());
        reminder.setSentCount(request.getSentCount());
        reminder.setLastSentAt(request.getLastSentAt());
        if (request.getCreatedAt() != null) reminder.setCreatedAt(request.getCreatedAt());
    }

    private ReminderAdminResponse toReminderResponse(Reminder reminder) {
        ReminderAdminResponse resp = new ReminderAdminResponse();
        resp.setReminderId(reminder.getReminderId());
        resp.setReminderTypeId(reminder.getReminderType() != null ? reminder.getReminderType().getReminderTypeId() : null);
        resp.setReminderTypeName(reminder.getReminderType() != null ? reminder.getReminderType().getTypeName() : null);
        resp.setPetId(reminder.getPet() != null ? reminder.getPet().getPetId() : null);
        resp.setPetName(reminder.getPet() != null ? reminder.getPet().getPetName() : null);
        resp.setUserId(reminder.getUser() != null ? reminder.getUser().getUserId() : null);
        resp.setUserName(reminder.getUser() != null ? reminder.getUser().getUsername() : null);
        resp.setVetId(reminder.getVeterinarian() != null ? reminder.getVeterinarian().getVetId() : null);
        resp.setVetName(reminder.getVeterinarian() != null && reminder.getVeterinarian().getUser() != null ? reminder.getVeterinarian().getUser().getUsername() : null);
        resp.setSourceTable(reminder.getSourceTable());
        resp.setSourceRecordId(reminder.getSourceRecordId());
        resp.setTitle(reminder.getTitle());
        resp.setMessage(reminder.getMessage());
        resp.setDueDate(reminder.getDueDate());
        resp.setReminderDate(reminder.getReminderDate());
        resp.setCompleted(reminder.getCompleted());
        resp.setCompletedDate(reminder.getCompletedDate());
        resp.setSentCount(reminder.getSentCount());
        resp.setLastSentAt(reminder.getLastSentAt());
        resp.setCreatedAt(reminder.getCreatedAt());
        return resp;
    }

    private void fillDewormingRecord(DewormingRecord record, DewormingRecordAdminRequest request) {
        if (request.getPetId() != null) {
            record.setPet(resolvePet(request.getPetId()));
        }
        record.setProductId(request.getProductId());
        record.setSourceType(request.getSourceType());
        record.setInstitutionId(request.getInstitutionId());
        record.setVetUserId(request.getVetUserId());
        record.setApplicationDate(request.getApplicationDate());
        record.setNextDueDate(request.getNextDueDate());
        record.setDosage(request.getDosage());
        record.setNotes(request.getNotes());
        if (request.getRecordedBy() != null) record.setRecordedBy(request.getRecordedBy());
        if (request.getCreatedAt() != null) record.setCreatedAt(request.getCreatedAt());
    }

    private DewormingRecordAdminResponse toDewormingResponse(DewormingRecord record) {
        DewormingRecordAdminResponse resp = new DewormingRecordAdminResponse();
        resp.setDewormingId(record.getDewormingId());
        resp.setPetId(record.getPet() != null ? record.getPet().getPetId() : null);
        resp.setPetName(record.getPet() != null ? record.getPet().getPetName() : null);
        resp.setProductId(record.getProductId());
        resp.setSourceType(record.getSourceType());
        resp.setInstitutionId(record.getInstitutionId());
        resp.setVetUserId(record.getVetUserId());
        resp.setApplicationDate(record.getApplicationDate());
        resp.setNextDueDate(record.getNextDueDate());
        resp.setDosage(record.getDosage());
        resp.setNotes(record.getNotes());
        resp.setRecordedBy(record.getRecordedBy());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }

    private void fillIntakeRecord(IntakeRecord record, IntakeRecordAdminRequest request) {
        if (request.getPetId() != null) {
            record.setPet(resolvePet(request.getPetId()));
        }
        record.setIntakeVolume(request.getIntakeVolume());
        record.setRecordDate(request.getRecordDate());
        record.setNotes(request.getNotes());
        record.setRecordedBy(request.getRecordedBy());
        if (request.getCreatedAt() != null) record.setCreatedAt(request.getCreatedAt());
    }

    private IntakeRecordAdminResponse toIntakeRecordResponse(IntakeRecord record) {
        IntakeRecordAdminResponse resp = new IntakeRecordAdminResponse();
        resp.setIntakeId(record.getIntakeId());
        resp.setPetId(record.getPet() != null ? record.getPet().getPetId() : null);
        resp.setPetName(record.getPet() != null ? record.getPet().getPetName() : null);
        resp.setIntakeVolume(record.getIntakeVolume());
        resp.setRecordDate(record.getRecordDate());
        resp.setNotes(record.getNotes());
        resp.setRecordedBy(record.getRecordedBy());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }

    // 用户反馈
    @GetMapping("/feedback")
    public ApiResponse<List<UserFeedback>> listFeedback() {
        return ApiResponse.success(userFeedbackRepository.findAll());
    }

    @PostMapping("/feedback")
    public ApiResponse<UserFeedback> createFeedback(@RequestBody UserFeedback request) {
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }
        return ApiResponse.success(userFeedbackRepository.save(request));
    }

    @PutMapping("/feedback/{id}")
    public ApiResponse<UserFeedback> updateFeedback(@PathVariable Integer id, @RequestBody UserFeedback request) {
        UserFeedback exist = userFeedbackRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "反馈不存在"));
        exist.setFeedbackType(request.getFeedbackType());
        exist.setContent(request.getContent());
        exist.setRating(request.getRating());
        exist.setStatus(request.getStatus());
        exist.setResponse(request.getResponse());
        exist.setRespondedBy(request.getRespondedBy());
        exist.setRespondedAt(request.getRespondedAt());
        return ApiResponse.success(userFeedbackRepository.save(exist));
    }

    @DeleteMapping("/feedback/{id}")
    public ApiResponse<Void> deleteFeedback(@PathVariable Integer id) {
        userFeedbackRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 保险信息
    @GetMapping("/insurance")
    public ApiResponse<List<PetInsurance>> listInsurance() {
        return ApiResponse.success(petInsuranceRepository.findAll());
    }

    @PostMapping("/insurance")
    public ApiResponse<PetInsurance> createInsurance(@RequestBody PetInsurance request) {
        ensurePetExists(request.getPetId());
        return ApiResponse.success(petInsuranceRepository.save(request));
    }

    @PutMapping("/insurance/{id}")
    public ApiResponse<PetInsurance> updateInsurance(@PathVariable Integer id, @RequestBody PetInsurance request) {
        PetInsurance exist = petInsuranceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "保险记录不存在"));
        if (request.getPetId() != null) {
            ensurePetExists(request.getPetId());
            exist.setPetId(request.getPetId());
        }
        exist.setInsuranceCompany(request.getInsuranceCompany());
        exist.setPolicyNumber(request.getPolicyNumber());
        exist.setCoverageType(request.getCoverageType());
        exist.setStartDate(request.getStartDate());
        exist.setEndDate(request.getEndDate());
        exist.setAnnualPremium(request.getAnnualPremium());
        exist.setCoverageLimit(request.getCoverageLimit());
        exist.setDeductible(request.getDeductible());
        exist.setContactPhone(request.getContactPhone());
        exist.setNotes(request.getNotes());
        return ApiResponse.success(petInsuranceRepository.save(exist));
    }

    @DeleteMapping("/insurance/{id}")
    public ApiResponse<Void> deleteInsurance(@PathVariable Integer id) {
        petInsuranceRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    // 宠物档案（管理员视角）
    @GetMapping("/pets")
    public ApiResponse<List<PetResponse>> listPets(@RequestParam(value = "keyword", required = false) String keyword) {
        return ApiResponse.success(petService.listAllPets(keyword));
    }

    @PostMapping("/pets")
    public ApiResponse<PetResponse> createPet(@RequestBody @Valid PetRequest request,
                                              @RequestParam(value = "userId", required = false) Integer userId) {
        return ApiResponse.success(petService.createPet(request, userId));
    }

    @PutMapping("/pets/{id}")
    public ApiResponse<PetResponse> updatePet(@PathVariable Integer id,
                                              @RequestBody @Valid PetRequest request,
                                              @RequestParam(value = "userId", required = false) Integer userId) {
        return ApiResponse.success(petService.updatePetAdmin(id, request, userId));
    }

    @DeleteMapping("/pets/{id}")
    public ApiResponse<Void> deletePet(@PathVariable Integer id) {
        petService.deletePetAdmin(id);
        return ApiResponse.success(null);
    }

    // 宠物照片管理
    @GetMapping("/pet-photos")
    public ApiResponse<List<PetPhotoAdminResponse>> listPetPhotos(@RequestParam(value = "petId", required = false) Integer petId,
                                                                  @RequestParam(value = "limit", defaultValue = "200") int limit) {
        List<PetPhoto> photos = (petId != null)
                ? petPhotoRepository.findByPet_PetIdOrderByUploadedAtDesc(petId)
                : petPhotoRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadedAt"));
        if (limit > 0 && photos.size() > limit) {
            photos = photos.subList(0, limit);
        }
        List<PetPhotoAdminResponse> result = photos.stream().map(this::mapPetPhoto).collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    @PostMapping("/pet-photos")
    public ApiResponse<PetPhotoAdminResponse> createPetPhoto(@RequestBody @Valid PetPhotoAdminRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        if (!StringUtils.hasText(request.getPhotoDataBase64())) {
            throw new BusinessException(400, "photoDataBase64 不能为空");
        }
        PetPhoto photo = new PetPhoto();
        photo.setPet(pet);
        fillPhotoFields(photo, request);
        photo.setUploadedAt(LocalDateTime.now());
        PetPhoto saved = petPhotoRepository.save(photo);
        return ApiResponse.success(mapPetPhoto(saved));
    }

    @PutMapping("/pet-photos/{photoId}")
    public ApiResponse<PetPhotoAdminResponse> updatePetPhoto(@PathVariable Integer photoId,
                                                             @RequestBody @Valid PetPhotoAdminRequest request) {
        PetPhoto photo = petPhotoRepository.findById(photoId)
                .orElseThrow(() -> new BusinessException(404, "照片不存在"));
        if (request.getPetId() != null && (photo.getPet() == null || !photo.getPet().getPetId().equals(request.getPetId()))) {
            Pet pet = petRepository.findById(request.getPetId())
                    .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
            photo.setPet(pet);
        }
        fillPhotoFields(photo, request);
        PetPhoto saved = petPhotoRepository.save(photo);
        return ApiResponse.success(mapPetPhoto(saved));
    }

    @DeleteMapping("/pet-photos/{photoId}")
    public ApiResponse<Void> deletePetPhoto(@PathVariable Integer photoId) {
        petPhotoRepository.deleteById(photoId);
        return ApiResponse.success(null);
    }

    private void fillPhotoFields(PetPhoto photo, PetPhotoAdminRequest request) {
        if (StringUtils.hasText(request.getFileName())) {
            photo.setFileName(request.getFileName());
        }
        if (StringUtils.hasText(request.getContentType())) {
            photo.setContentType(request.getContentType());
        }
        if (request.getPosX() != null) {
            photo.setPosX(request.getPosX());
        }
        if (request.getPosY() != null) {
            photo.setPosY(request.getPosY());
        }
        if (StringUtils.hasText(request.getPhotoDataBase64())) {
            try {
                photo.setPhotoData(Base64.getDecoder().decode(request.getPhotoDataBase64()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(400, "照片数据不是有效的Base64");
            }
            photo.setUploadedAt(request.getUploadedAt() != null ? request.getUploadedAt() : LocalDateTime.now());
        } else if (request.getUploadedAt() != null) {
            photo.setUploadedAt(request.getUploadedAt());
        }
    }

    private PetPhotoAdminResponse mapPetPhoto(PetPhoto photo) {
        PetPhotoAdminResponse resp = new PetPhotoAdminResponse();
        resp.setPhotoId(photo.getPhotoId());
        resp.setPetId(photo.getPet() != null ? photo.getPet().getPetId() : null);
        resp.setPetName(photo.getPet() != null ? photo.getPet().getPetName() : null);
        resp.setFileName(photo.getFileName());
        resp.setContentType(photo.getContentType());
        resp.setUploadedAt(photo.getUploadedAt());
        resp.setPosX(photo.getPosX());
        resp.setPosY(photo.getPosY());
        resp.setSizeBytes(photo.getPhotoData() != null ? photo.getPhotoData().length : 0);
        if (photo.getPhotoId() != null) {
            resp.setPreviewUrl("/api/pets/photos/" + photo.getPhotoId());
        }
        return resp;
    }

    private UserRole resolveRole(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            return userRoleRepository.findByRoleName("宠物主人").orElse(null);
        }
        return userRoleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new BusinessException(400, "角色不存在"));
    }

    private void ensurePetExists(Integer petId) {
        if (petId == null) {
            throw new BusinessException(400, "petId 不能为空");
        }
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
    }

    private Pet resolvePet(Integer petId) {
        if (petId == null) {
            throw new BusinessException(400, "petId 不能为空");
        }
        return petRepository.findById(petId)
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
    }

    private Veterinarian resolveVeterinarianByUserId(Integer vetUserId) {
        return veterinarianRepository.findByUser_UserId(vetUserId)
                .orElseThrow(() -> new BusinessException(404, "兽医不存在"));
    }

    public static class UserRequest {
        @NotBlank
        private String username;
        @Email
        private String email;
        private String phone;
        private String roleName;
        private String password;
        private Boolean active;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean getActive() {
            return active;
        }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

    public static class VaccinationAdminRequest {
        private Integer petId;
        private Integer vaccineTypeId;
        private Integer institutionId;
        private Integer vetUserId;
        private LocalDate vaccinationDate;
        private LocalDate nextDueDate;
        private String lotNumber;
        private String notes;
        private Integer createdBy;
        private LocalDateTime createdAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public Integer getVaccineTypeId() {
            return vaccineTypeId;
        }

        public void setVaccineTypeId(Integer vaccineTypeId) {
            this.vaccineTypeId = vaccineTypeId;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public Integer getVetUserId() {
            return vetUserId;
        }

        public void setVetUserId(Integer vetUserId) {
            this.vetUserId = vetUserId;
        }

        public LocalDate getVaccinationDate() {
            return vaccinationDate;
        }

        public void setVaccinationDate(LocalDate vaccinationDate) {
            this.vaccinationDate = vaccinationDate;
        }

        public LocalDate getNextDueDate() {
            return nextDueDate;
        }

        public void setNextDueDate(LocalDate nextDueDate) {
            this.nextDueDate = nextDueDate;
        }

        public String getLotNumber() {
            return lotNumber;
        }

        public void setLotNumber(String lotNumber) {
            this.lotNumber = lotNumber;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class ReminderAdminResponse {
        private Integer reminderId;
        private Integer reminderTypeId;
        private String reminderTypeName;
        private Integer petId;
        private String petName;
        private Integer userId;
        private String userName;
        private Integer vetId;
        private String vetName;
        private String sourceTable;
        private Integer sourceRecordId;
        private String title;
        private String message;
        private LocalDate dueDate;
        private LocalDate reminderDate;
        private Boolean completed;
        private LocalDate completedDate;
        private Integer sentCount;
        private LocalDateTime lastSentAt;
        private LocalDateTime createdAt;

        public Integer getReminderId() {
            return reminderId;
        }

        public void setReminderId(Integer reminderId) {
            this.reminderId = reminderId;
        }

        public Integer getReminderTypeId() {
            return reminderTypeId;
        }

        public void setReminderTypeId(Integer reminderTypeId) {
            this.reminderTypeId = reminderTypeId;
        }

        public String getReminderTypeName() {
            return reminderTypeName;
        }

        public void setReminderTypeName(String reminderTypeName) {
            this.reminderTypeName = reminderTypeName;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getVetId() {
            return vetId;
        }

        public void setVetId(Integer vetId) {
            this.vetId = vetId;
        }

        public String getVetName() {
            return vetName;
        }

        public void setVetName(String vetName) {
            this.vetName = vetName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getSourceTable() {
            return sourceTable;
        }

        public void setSourceTable(String sourceTable) {
            this.sourceTable = sourceTable;
        }

        public Integer getSourceRecordId() {
            return sourceRecordId;
        }

        public void setSourceRecordId(Integer sourceRecordId) {
            this.sourceRecordId = sourceRecordId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public LocalDate getReminderDate() {
            return reminderDate;
        }

        public void setReminderDate(LocalDate reminderDate) {
            this.reminderDate = reminderDate;
        }

        public Boolean getCompleted() {
            return completed;
        }

        public void setCompleted(Boolean completed) {
            this.completed = completed;
        }

        public LocalDate getCompletedDate() {
            return completedDate;
        }

        public void setCompletedDate(LocalDate completedDate) {
            this.completedDate = completedDate;
        }

        public Integer getSentCount() {
            return sentCount;
        }

        public void setSentCount(Integer sentCount) {
            this.sentCount = sentCount;
        }

        public LocalDateTime getLastSentAt() {
            return lastSentAt;
        }

        public void setLastSentAt(LocalDateTime lastSentAt) {
            this.lastSentAt = lastSentAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class VaccinationAdminResponse {
        private Integer vaccinationId;
        private Integer petId;
        private String petName;
        private Integer vaccineTypeId;
        private Integer institutionId;
        private Integer vetUserId;
        private LocalDate vaccinationDate;
        private LocalDate nextDueDate;
        private String lotNumber;
        private String notes;
        private Integer createdBy;
        private LocalDateTime createdAt;

        public Integer getVaccinationId() {
            return vaccinationId;
        }

        public void setVaccinationId(Integer vaccinationId) {
            this.vaccinationId = vaccinationId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public Integer getVaccineTypeId() {
            return vaccineTypeId;
        }

        public void setVaccineTypeId(Integer vaccineTypeId) {
            this.vaccineTypeId = vaccineTypeId;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public Integer getVetUserId() {
            return vetUserId;
        }

        public void setVetUserId(Integer vetUserId) {
            this.vetUserId = vetUserId;
        }

        public LocalDate getVaccinationDate() {
            return vaccinationDate;
        }

        public void setVaccinationDate(LocalDate vaccinationDate) {
            this.vaccinationDate = vaccinationDate;
        }

        public LocalDate getNextDueDate() {
            return nextDueDate;
        }

        public void setNextDueDate(LocalDate nextDueDate) {
            this.nextDueDate = nextDueDate;
        }

        public String getLotNumber() {
            return lotNumber;
        }

        public void setLotNumber(String lotNumber) {
            this.lotNumber = lotNumber;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class MedicalRecordAdminResponse {
        private Integer recordId;
        private Integer petId;
        private String petName;
        private Integer institutionId;
        private Integer vetUserId;
        private LocalDate visitDate;
        private String reason;
        private String diagnosis;
        private String treatment;
        private String prescription;
        private java.math.BigDecimal cost;
        private String paymentStatus;
        private String recordStatus;
        private String insuranceClaimId;
        private LocalDate followUpDate;
        private LocalDateTime createdAt;

        public Integer getRecordId() {
            return recordId;
        }

        public void setRecordId(Integer recordId) {
            this.recordId = recordId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public Integer getVetUserId() {
            return vetUserId;
        }

        public void setVetUserId(Integer vetUserId) {
            this.vetUserId = vetUserId;
        }

        public LocalDate getVisitDate() {
            return visitDate;
        }

        public void setVisitDate(LocalDate visitDate) {
            this.visitDate = visitDate;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        public String getTreatment() {
            return treatment;
        }

        public void setTreatment(String treatment) {
            this.treatment = treatment;
        }

        public String getPrescription() {
            return prescription;
        }

        public void setPrescription(String prescription) {
            this.prescription = prescription;
        }

        public java.math.BigDecimal getCost() {
            return cost;
        }

        public void setCost(java.math.BigDecimal cost) {
            this.cost = cost;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getRecordStatus() {
            return recordStatus;
        }

        public void setRecordStatus(String recordStatus) {
            this.recordStatus = recordStatus;
        }

        public String getInsuranceClaimId() {
            return insuranceClaimId;
        }

        public void setInsuranceClaimId(String insuranceClaimId) {
            this.insuranceClaimId = insuranceClaimId;
        }

        public LocalDate getFollowUpDate() {
            return followUpDate;
        }

        public void setFollowUpDate(LocalDate followUpDate) {
            this.followUpDate = followUpDate;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class MedicalRecordAdminRequest {
        private Integer petId;
        private Integer institutionId;
        private Integer vetUserId;
        private LocalDate visitDate;
        private String reason;
        private String diagnosis;
        private String treatment;
        private String prescription;
        private java.math.BigDecimal cost;
        private String paymentStatus;
        private String recordStatus;
        private String insuranceClaimId;
        private LocalDate followUpDate;
        private LocalDateTime createdAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public Integer getVetUserId() {
            return vetUserId;
        }

        public void setVetUserId(Integer vetUserId) {
            this.vetUserId = vetUserId;
        }

        public LocalDate getVisitDate() {
            return visitDate;
        }

        public void setVisitDate(LocalDate visitDate) {
            this.visitDate = visitDate;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        public String getTreatment() {
            return treatment;
        }

        public void setTreatment(String treatment) {
            this.treatment = treatment;
        }

        public String getPrescription() {
            return prescription;
        }

        public void setPrescription(String prescription) {
            this.prescription = prescription;
        }

        public java.math.BigDecimal getCost() {
            return cost;
        }

        public void setCost(java.math.BigDecimal cost) {
            this.cost = cost;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getRecordStatus() {
            return recordStatus;
        }

        public void setRecordStatus(String recordStatus) {
            this.recordStatus = recordStatus;
        }

        public String getInsuranceClaimId() {
            return insuranceClaimId;
        }

        public void setInsuranceClaimId(String insuranceClaimId) {
            this.insuranceClaimId = insuranceClaimId;
        }

        public LocalDate getFollowUpDate() {
            return followUpDate;
        }

        public void setFollowUpDate(LocalDate followUpDate) {
            this.followUpDate = followUpDate;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class WeightRecordAdminResponse {
        private Integer weightId;
        private Integer petId;
        private String petName;
        private java.math.BigDecimal weight;
        private LocalDate recordDate;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getWeightId() {
            return weightId;
        }

        public void setWeightId(Integer weightId) {
            this.weightId = weightId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public java.math.BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(java.math.BigDecimal weight) {
            this.weight = weight;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class WeightRecordAdminRequest {
        private Integer petId;
        private java.math.BigDecimal weight;
        private LocalDate recordDate;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public java.math.BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(java.math.BigDecimal weight) {
            this.weight = weight;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class TemperatureRecordAdminRequest {
        private Integer petId;
        private java.math.BigDecimal temperature;
        private LocalDate recordDate;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public java.math.BigDecimal getTemperature() {
            return temperature;
        }

        public void setTemperature(java.math.BigDecimal temperature) {
            this.temperature = temperature;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class TemperatureRecordAdminResponse {
        private Integer temperatureId;
        private Integer petId;
        private String petName;
        private java.math.BigDecimal temperature;
        private LocalDate recordDate;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getTemperatureId() {
            return temperatureId;
        }

        public void setTemperatureId(Integer temperatureId) {
            this.temperatureId = temperatureId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public java.math.BigDecimal getTemperature() {
            return temperature;
        }

        public void setTemperature(java.math.BigDecimal temperature) {
            this.temperature = temperature;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class ActivityRecordAdminRequest {
        private Integer petId;
        private java.math.BigDecimal activityLevel;
        private LocalDate recordDate;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public java.math.BigDecimal getActivityLevel() {
            return activityLevel;
        }

        public void setActivityLevel(java.math.BigDecimal activityLevel) {
            this.activityLevel = activityLevel;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class ActivityRecordAdminResponse {
        private Integer activityId;
        private Integer petId;
        private String petName;
        private java.math.BigDecimal activityLevel;
        private LocalDate recordDate;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public java.math.BigDecimal getActivityLevel() {
            return activityLevel;
        }

        public void setActivityLevel(java.math.BigDecimal activityLevel) {
            this.activityLevel = activityLevel;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class IntakeRecordAdminRequest {
        private Integer petId;
        private java.math.BigDecimal intakeVolume;
        private LocalDate recordDate;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public java.math.BigDecimal getIntakeVolume() {
            return intakeVolume;
        }

        public void setIntakeVolume(java.math.BigDecimal intakeVolume) {
            this.intakeVolume = intakeVolume;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class IntakeRecordAdminResponse {
        private Integer intakeId;
        private Integer petId;
        private String petName;
        private java.math.BigDecimal intakeVolume;
        private LocalDate recordDate;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getIntakeId() {
            return intakeId;
        }

        public void setIntakeId(Integer intakeId) {
            this.intakeId = intakeId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public java.math.BigDecimal getIntakeVolume() {
            return intakeVolume;
        }

        public void setIntakeVolume(java.math.BigDecimal intakeVolume) {
            this.intakeVolume = intakeVolume;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class MedicationAdminRequest {
        private Integer petId;
        private String drugName;
        private String dosage;
        private String frequency;
        private String route;
        private LocalDate startDate;
        private LocalDate endDate;
        private String instructions;
        private String contraindications;
        private LocalDateTime createdAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getDrugName() {
            return drugName;
        }

        public void setDrugName(String drugName) {
            this.drugName = drugName;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public String getContraindications() {
            return contraindications;
        }

        public void setContraindications(String contraindications) {
            this.contraindications = contraindications;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class PetPlanAdminRequest {
        private Integer petId;
        private String planType;
        private String title;
        private String target;
        private String frequency;
        private LocalDate startDate;
        private LocalDate endDate;
        private String notes;
        private LocalDateTime createdAt;
        private Integer createdBy;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPlanType() {
            return planType;
        }

        public void setPlanType(String planType) {
            this.planType = planType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }
    }

    public static class PetPlanAdminResponse {
        private Integer planId;
        private Integer petId;
        private String petName;
        private String planType;
        private String title;
        private String target;
        private String frequency;
        private LocalDate startDate;
        private LocalDate endDate;
        private String notes;
        private LocalDateTime createdAt;
        private Integer createdBy;
        private String createdByName;

        public Integer getPlanId() {
            return planId;
        }

        public void setPlanId(Integer planId) {
            this.planId = planId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public String getPlanType() {
            return planType;
        }

        public void setPlanType(String planType) {
            this.planType = planType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public String getCreatedByName() {
            return createdByName;
        }

        public void setCreatedByName(String createdByName) {
            this.createdByName = createdByName;
        }
    }

    public static class ReminderAdminRequest {
        private Integer reminderTypeId;
        private Integer petId;
        private Integer userId;
        private Integer vetId;
        private String sourceTable;
        private Integer sourceRecordId;
        private String title;
        private String message;
        private LocalDate dueDate;
        private LocalDate reminderDate;
        private Boolean completed;
        private LocalDate completedDate;
        private Integer sentCount;
        private LocalDateTime lastSentAt;
        private LocalDateTime createdAt;

        public Integer getReminderTypeId() {
            return reminderTypeId;
        }

        public void setReminderTypeId(Integer reminderTypeId) {
            this.reminderTypeId = reminderTypeId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getVetId() {
            return vetId;
        }

        public void setVetId(Integer vetId) {
            this.vetId = vetId;
        }

        public String getSourceTable() {
            return sourceTable;
        }

        public void setSourceTable(String sourceTable) {
            this.sourceTable = sourceTable;
        }

        public Integer getSourceRecordId() {
            return sourceRecordId;
        }

        public void setSourceRecordId(Integer sourceRecordId) {
            this.sourceRecordId = sourceRecordId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public LocalDate getReminderDate() {
            return reminderDate;
        }

        public void setReminderDate(LocalDate reminderDate) {
            this.reminderDate = reminderDate;
        }

        public Boolean getCompleted() {
            return completed;
        }

        public void setCompleted(Boolean completed) {
            this.completed = completed;
        }

        public LocalDate getCompletedDate() {
            return completedDate;
        }

        public void setCompletedDate(LocalDate completedDate) {
            this.completedDate = completedDate;
        }

        public Integer getSentCount() {
            return sentCount;
        }

        public void setSentCount(Integer sentCount) {
            this.sentCount = sentCount;
        }

        public LocalDateTime getLastSentAt() {
            return lastSentAt;
        }

        public void setLastSentAt(LocalDateTime lastSentAt) {
            this.lastSentAt = lastSentAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class FinanceRecordAdminResponse {
        private Integer recordId;
        private Integer userId;
        private String ownerName;
        private Integer petId;
        private String petName;
        private String title;
        private String recordType;
        private Integer medicalRecordId;
        private String category;
        private java.math.BigDecimal amount;
        private LocalDate recordDate;
        private String status;
        private String notes;
        private LocalDateTime createdAt;

        public Integer getRecordId() {
            return recordId;
        }

        public void setRecordId(Integer recordId) {
            this.recordId = recordId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRecordType() {
            return recordType;
        }

        public void setRecordType(String recordType) {
            this.recordType = recordType;
        }

        public Integer getMedicalRecordId() {
            return medicalRecordId;
        }

        public void setMedicalRecordId(Integer medicalRecordId) {
            this.medicalRecordId = medicalRecordId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public java.math.BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class FinanceRecordAdminRequest {
        private Integer userId;
        private Integer petId;
        private String title;
        private String recordType;
        private Integer medicalRecordId;
        private String category;
        private java.math.BigDecimal amount;
        private LocalDate recordDate;
        private String status;
        private String notes;
        private LocalDateTime createdAt;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRecordType() {
            return recordType;
        }

        public void setRecordType(String recordType) {
            this.recordType = recordType;
        }

        public Integer getMedicalRecordId() {
            return medicalRecordId;
        }

        public void setMedicalRecordId(Integer medicalRecordId) {
            this.medicalRecordId = medicalRecordId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public java.math.BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }

        public LocalDate getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class MedicationAdminResponse {
        private Integer medicationId;
        private Integer petId;
        private String petName;
        private String drugName;
        private String dosage;
        private String frequency;
        private String route;
        private LocalDate startDate;
        private LocalDate endDate;
        private String instructions;
        private String contraindications;
        private LocalDateTime createdAt;

        public Integer getMedicationId() {
            return medicationId;
        }

        public void setMedicationId(Integer medicationId) {
            this.medicationId = medicationId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public String getDrugName() {
            return drugName;
        }

        public void setDrugName(String drugName) {
            this.drugName = drugName;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public String getContraindications() {
            return contraindications;
        }

        public void setContraindications(String contraindications) {
            this.contraindications = contraindications;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class DewormingRecordAdminRequest {
        private Integer petId;
        private Integer productId;
        private String sourceType;
        private Integer institutionId;
        private Integer vetUserId;
        private LocalDate applicationDate;
        private LocalDate nextDueDate;
        private String dosage;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public Integer getVetUserId() {
            return vetUserId;
        }

        public void setVetUserId(Integer vetUserId) {
            this.vetUserId = vetUserId;
        }

        public LocalDate getApplicationDate() {
            return applicationDate;
        }

        public void setApplicationDate(LocalDate applicationDate) {
            this.applicationDate = applicationDate;
        }

        public LocalDate getNextDueDate() {
            return nextDueDate;
        }

        public void setNextDueDate(LocalDate nextDueDate) {
            this.nextDueDate = nextDueDate;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class DewormingRecordAdminResponse {
        private Integer dewormingId;
        private Integer petId;
        private String petName;
        private Integer productId;
        private String sourceType;
        private Integer institutionId;
        private Integer vetUserId;
        private LocalDate applicationDate;
        private LocalDate nextDueDate;
        private String dosage;
        private String notes;
        private Integer recordedBy;
        private LocalDateTime createdAt;

        public Integer getDewormingId() {
            return dewormingId;
        }

        public void setDewormingId(Integer dewormingId) {
            this.dewormingId = dewormingId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public Integer getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(Integer institutionId) {
            this.institutionId = institutionId;
        }

        public Integer getVetUserId() {
            return vetUserId;
        }

        public void setVetUserId(Integer vetUserId) {
            this.vetUserId = vetUserId;
        }

        public LocalDate getApplicationDate() {
            return applicationDate;
        }

        public void setApplicationDate(LocalDate applicationDate) {
            this.applicationDate = applicationDate;
        }

        public LocalDate getNextDueDate() {
            return nextDueDate;
        }

        public void setNextDueDate(LocalDate nextDueDate) {
            this.nextDueDate = nextDueDate;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getRecordedBy() {
            return recordedBy;
        }

        public void setRecordedBy(Integer recordedBy) {
            this.recordedBy = recordedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class PetPhotoAdminRequest {
        private Integer petId;
        private String fileName;
        private String contentType;
        private String photoDataBase64;
        private Double posX;
        private Double posY;
        private LocalDateTime uploadedAt;

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getPhotoDataBase64() {
            return photoDataBase64;
        }

        public void setPhotoDataBase64(String photoDataBase64) {
            this.photoDataBase64 = photoDataBase64;
        }

        public Double getPosX() {
            return posX;
        }

        public void setPosX(Double posX) {
            this.posX = posX;
        }

        public Double getPosY() {
            return posY;
        }

        public void setPosY(Double posY) {
            this.posY = posY;
        }

        public LocalDateTime getUploadedAt() {
            return uploadedAt;
        }

        public void setUploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
        }
    }

    public static class PetPhotoAdminResponse {
        private Integer photoId;
        private Integer petId;
        private String petName;
        private String fileName;
        private String contentType;
        private LocalDateTime uploadedAt;
        private Double posX;
        private Double posY;
        private Integer sizeBytes;
        private String previewUrl;

        public Integer getPhotoId() {
            return photoId;
        }

        public void setPhotoId(Integer photoId) {
            this.photoId = photoId;
        }

        public Integer getPetId() {
            return petId;
        }

        public void setPetId(Integer petId) {
            this.petId = petId;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public LocalDateTime getUploadedAt() {
            return uploadedAt;
        }

        public void setUploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
        }

        public Double getPosX() {
            return posX;
        }

        public void setPosX(Double posX) {
            this.posX = posX;
        }

        public Double getPosY() {
            return posY;
        }

        public void setPosY(Double posY) {
            this.posY = posY;
        }

        public Integer getSizeBytes() {
            return sizeBytes;
        }

        public void setSizeBytes(Integer sizeBytes) {
            this.sizeBytes = sizeBytes;
        }

        public String getPreviewUrl() {
            return previewUrl;
        }

        public void setPreviewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
        }
    }
}

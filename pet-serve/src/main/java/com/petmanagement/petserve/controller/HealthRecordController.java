package com.petmanagement.petserve.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.petmanagement.petserve.dto.health.DewormingCreateRequest;
import com.petmanagement.petserve.common.ApiResponse;
import com.petmanagement.petserve.dto.health.MedicalRecordCreateRequest;
import com.petmanagement.petserve.dto.health.DewormingDto;
import com.petmanagement.petserve.dto.health.DewormingProductOption;
import com.petmanagement.petserve.dto.health.MedicationCreateRequest;
import com.petmanagement.petserve.dto.health.MedicalRecordDto;
import com.petmanagement.petserve.dto.health.MedicationDto;
import com.petmanagement.petserve.dto.health.ActivityDto;
import com.petmanagement.petserve.dto.health.IntakeDto;
import com.petmanagement.petserve.dto.health.TemperatureDto;
import com.petmanagement.petserve.dto.health.VaccinationCreateRequest;
import com.petmanagement.petserve.dto.health.VaccinationDto;
import com.petmanagement.petserve.dto.health.VitalCreateRequest;
import com.petmanagement.petserve.dto.health.VitalDto;
import com.petmanagement.petserve.dto.health.InstitutionOption;
import com.petmanagement.petserve.dto.health.VetOption;
import com.petmanagement.petserve.dto.health.VaccineTypeOption;
import com.petmanagement.petserve.dto.health.VetDailyStatsResponse;
import com.petmanagement.petserve.dto.health.VetOverviewStatsResponse;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.service.HealthRecordService;
import com.petmanagement.petserve.service.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/health")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;
    private final TokenService tokenService;

    @Autowired
    public HealthRecordController(HealthRecordService healthRecordService, TokenService tokenService) {
        this.healthRecordService = healthRecordService;
        this.tokenService = tokenService;
    }

    @GetMapping("/vaccinations")
    public ApiResponse<List<VaccinationDto>> vaccinations(@RequestParam(required = false) Integer petId,
                                                          @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.vaccinations(petId, uid));
    }

    @PostMapping("/vaccinations")
    public ApiResponse<VaccinationDto> createVaccination(@RequestBody @Valid VaccinationCreateRequest request,
                                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.addVaccination(request, uid));
    }

    @PutMapping("/vaccinations/{id}")
    public ApiResponse<VaccinationDto> updateVaccination(@PathVariable Integer id,
                                                         @RequestBody @Valid VaccinationCreateRequest request,
                                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.updateVaccination(id, request, uid));
    }

    @DeleteMapping("/vaccinations/{id}")
    public ApiResponse<Void> deleteVaccination(@PathVariable Integer id,
                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        healthRecordService.deleteVaccination(id, uid);
        return ApiResponse.success(null);
    }

    @GetMapping("/deworming")
    public ApiResponse<List<DewormingDto>> deworming(@RequestParam(required = false) Integer petId,
                                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.deworming(petId, uid));
    }

    @PostMapping("/deworming")
    public ApiResponse<DewormingDto> createDeworming(@RequestBody @Valid DewormingCreateRequest request,
                                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.addDeworming(request, uid));
    }

    @GetMapping("/deworming/products")
    public ApiResponse<List<DewormingProductOption>> dewormingProducts() {
        return ApiResponse.success(healthRecordService.dewormProductNames());
    }

    @DeleteMapping("/deworming/{id}")
    public ApiResponse<Void> deleteDeworm(@PathVariable Integer id,
                                          @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        healthRecordService.deleteDeworming(id, uid);
        return ApiResponse.success(null);
    }

    @GetMapping("/medical-records")
    public ApiResponse<List<MedicalRecordDto>> medicalRecords(@RequestParam(required = false) Integer petId,
                                                              @RequestParam(value = "vetUserId", required = false) Integer vetUserId,
                                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.medicalRecords(petId, uid, vetUserId));
    }

    @PostMapping("/medical-records")
    public ApiResponse<MedicalRecordDto> createMedicalRecord(@RequestBody @Valid MedicalRecordCreateRequest request,
                                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.addMedicalRecord(request, uid));
    }

    @PutMapping("/medical-records/{recordId}")
    public ApiResponse<MedicalRecordDto> updateMedicalRecord(@PathVariable Integer recordId,
                                                             @RequestBody @Valid MedicalRecordCreateRequest request,
                                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.updateMedicalRecord(recordId, request, uid));
    }

    @DeleteMapping("/medical-records/{recordId}")
    public ApiResponse<Void> deleteMedicalRecord(@PathVariable Integer recordId,
                                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        healthRecordService.deleteMedicalRecord(recordId, uid);
        return ApiResponse.success(null);
    }

    @GetMapping("/vitals")
    public ApiResponse<List<VitalDto>> vitals(@RequestParam(required = false) Integer petId,
                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.vitals(petId, uid));
    }

    @PostMapping("/vitals")
    public ApiResponse<VitalDto> createVital(@RequestBody @Valid VitalCreateRequest request,
                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.addVital(request, uid));
    }

    @PutMapping("/vitals/{id}")
    public ApiResponse<VitalDto> updateVital(@PathVariable Integer id,
                                             @RequestBody @Valid VitalCreateRequest request,
                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.updateVital(id, request, uid));
    }

    @GetMapping("/temperature")
    public ApiResponse<List<TemperatureDto>> temperatures(@RequestParam(required = false) Integer petId,
                                                          @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.temperature(petId, uid));
    }

    @GetMapping("/activity")
    public ApiResponse<List<ActivityDto>> activity(@RequestParam(required = false) Integer petId,
                                                   @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.activity(petId, uid));
    }

    @GetMapping("/intake")
    public ApiResponse<List<IntakeDto>> intake(@RequestParam(required = false) Integer petId,
                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.intake(petId, uid));
    }

    @PostMapping("/temperature")
    public ApiResponse<VitalDto> addTemperature(@RequestBody @Valid VitalCreateRequest request,
                                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.addTemperature(request, uid));
    }

    @PostMapping("/activity")
    public ApiResponse<VitalDto> addActivity(@RequestBody @Valid VitalCreateRequest request,
                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.addActivity(request, uid));
    }

    @PostMapping("/intake")
    public ApiResponse<VitalDto> addIntake(@RequestBody @Valid VitalCreateRequest request,
                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.addIntake(request, uid));
    }

    @DeleteMapping("/vitals/{id}")
    public ApiResponse<Void> deleteVital(@PathVariable Integer id,
                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        healthRecordService.deleteVital(id, uid);
        return ApiResponse.success(null);
    }

    @GetMapping("/medications")
    public ApiResponse<List<MedicationDto>> medications(@RequestParam(required = false) Integer petId,
                                                        @RequestParam(value = "vetUserId", required = false) Integer vetUserId,
                                                        @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.medications(petId, uid, vetUserId));
    }

    @PostMapping("/medications")
    public ApiResponse<MedicationDto> createMedication(@RequestBody @Valid MedicationCreateRequest request,
                                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.addMedication(request, uid));
    }

    @PutMapping("/medications/{id}")
    public ApiResponse<MedicationDto> updateMedication(@PathVariable Integer id,
                                                       @RequestBody @Valid MedicationCreateRequest request,
                                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        return ApiResponse.success(healthRecordService.updateMedication(id, request, uid));
    }

    @DeleteMapping("/medications/{id}")
    public ApiResponse<Void> deleteMedication(@PathVariable Integer id,
                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        healthRecordService.deleteMedication(id, uid);
        return ApiResponse.success(null);
    }

    @GetMapping("/institutions")
    public ApiResponse<List<InstitutionOption>> institutions() {
        return ApiResponse.success(healthRecordService.institutions());
    }

    @GetMapping("/vets")
    public ApiResponse<List<VetOption>> vets(@RequestParam(value = "institutionId", required = false) Integer institutionId) {
        return ApiResponse.success(healthRecordService.vets(institutionId));
    }

    @GetMapping("/vets/{vetUserId}/institution")
    public ApiResponse<InstitutionOption> vetInstitution(@PathVariable("vetUserId") Integer vetUserId) {
        return ApiResponse.success(healthRecordService.vetInstitution(vetUserId));
    }

    @GetMapping("/deworm-products")
    public ApiResponse<List<DewormingProductOption>> dewormProducts() {
        return ApiResponse.success(healthRecordService.dewormProducts());
    }

    @GetMapping("/vaccine-types")
    public ApiResponse<List<VaccineTypeOption>> vaccineTypes() {
        return ApiResponse.success(healthRecordService.vaccineTypes());
    }

    @GetMapping("/stats/daily")
    public ApiResponse<VetDailyStatsResponse> dailyStats(@RequestParam(value = "vetUserId", required = false) Integer vetUserId,
                                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        Integer targetVetId = vetUserId != null ? vetUserId : uid;
        return ApiResponse.success(healthRecordService.dailyStats(targetVetId));
    }

    @GetMapping("/stats/overview")
    public ApiResponse<VetOverviewStatsResponse> overviewStats(@RequestParam(value = "vetUserId", required = false) Integer vetUserId,
                                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer uid = resolveUser(authorization);
        Integer targetVetId = vetUserId != null ? vetUserId : uid;
        return ApiResponse.success(healthRecordService.overviewStats(targetVetId));
    }

    private Integer resolveUser(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return tokenService.resolve(authorization.replace("Bearer", "").trim())
                .orElseThrow(() -> new BusinessException(401, "未登录或登录已失效"));
    }
}

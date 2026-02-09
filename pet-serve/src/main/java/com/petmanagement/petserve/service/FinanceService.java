package com.petmanagement.petserve.service;

import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.finance.FinanceRecordRequest;
import com.petmanagement.petserve.dto.finance.FinanceRecordResponse;
import com.petmanagement.petserve.entity.FinanceRecord;
import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.entity.User;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.FinanceRecordRepository;
import com.petmanagement.petserve.repository.MedicalRecordRepository;
import com.petmanagement.petserve.repository.PetRepository;
import com.petmanagement.petserve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinanceService {

    private final FinanceRecordRepository financeRecordRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public FinanceService(FinanceRecordRepository financeRecordRepository, PetRepository petRepository, UserRepository userRepository, MedicalRecordRepository medicalRecordRepository) {
        this.financeRecordRepository = financeRecordRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public PageResponse<FinanceRecordResponse> list(Integer userId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageRequest = PageRequest.of(safePage - 1, safeSize,
                Sort.by(Sort.Direction.DESC, "recordDate").and(Sort.by(Sort.Direction.DESC, "createdAt")));

        Integer ownerId = requireUser(userId);
        Page<FinanceRecord> financeRecords = financeRecordRepository.findByOwner_UserId(ownerId, pageRequest);
        List<FinanceRecordResponse> records = financeRecords.stream().map(this::toResponse).collect(Collectors.toList());
        return PageResponse.of(records, financeRecords.getTotalElements(), safePage, safeSize);
    }

    @Transactional
    public FinanceRecordResponse create(FinanceRecordRequest request, Integer userId) {
        Integer ownerId = requireUser(userId);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        Pet pet = null;
        if (request.getPetId() != null) {
            pet = petRepository.findById(request.getPetId())
                    .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
            if (pet.getOwner() != null && ownerId != null && !ownerId.equals(pet.getOwner().getUserId())) {
                throw new BusinessException(403, "无权为该宠物添加费用记录");
            }
        }

        // 关联就诊记录并同步支付状态
        if (request.getMedicalRecordId() != null) {
            var medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                    .orElseThrow(() -> new BusinessException(404, "就诊记录不存在"));
            if (medicalRecord.getPet() != null && medicalRecord.getPet().getOwner() != null
                    && !ownerId.equals(medicalRecord.getPet().getOwner().getUserId())) {
                throw new BusinessException(403, "无权关联该就诊记录");
            }
        }

        FinanceRecord record = new FinanceRecord();
        record.setOwner(owner);
        record.setPet(pet);
        record.setTitle(request.getTitle());
        record.setRecordType(StringUtils.hasText(request.getRecordType()) ? request.getRecordType() : "消费");
        record.setCategory(request.getCategory());
        record.setAmount(request.getAmount());
        record.setRecordDate(request.getRecordDate());
        record.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "未报销");
        record.setNotes(request.getNotes());
        record.setCreatedAt(LocalDateTime.now());
        record.setMedicalRecordId(request.getMedicalRecordId());

        FinanceRecord saved = financeRecordRepository.save(record);

        // 同步医疗记录支付状态/金额
        if (request.getMedicalRecordId() != null) {
            var medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                    .orElseThrow(() -> new BusinessException(404, "就诊记录不存在"));
            medicalRecord.setPaymentStatus(mapPaymentStatus(record.getStatus()));
            if (medicalRecord.getCost() == null) {
                medicalRecord.setCost(record.getAmount());
            }
            medicalRecordRepository.save(medicalRecord);
        }
        return toResponse(saved);
    }

    private FinanceRecordResponse toResponse(FinanceRecord record) {
        return FinanceRecordResponse.builder()
                .recordId(record.getRecordId())
                .petId(record.getPet() != null ? record.getPet().getPetId() : null)
                .petName(record.getPet() != null ? record.getPet().getPetName() : null)
                .ownerUserId(record.getOwner() != null ? record.getOwner().getUserId() : null)
                .medicalRecordId(record.getMedicalRecordId())
                .title(record.getTitle())
                .recordType(record.getRecordType())
                .category(record.getCategory())
                .amount(record.getAmount())
                .recordDate(record.getRecordDate())
                .status(record.getStatus())
                .notes(record.getNotes())
                .build();
    }

    private String mapPaymentStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "未支付";
        }
        switch (status) {
            case "处理中":
                return "保险支付";
            case "已报销":
                return "保险支付";
            case "自费":
            case "已支付":
                return "已支付";
            default:
                return "未支付";
        }
    }

    private Integer requireUser(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return userId;
    }
}

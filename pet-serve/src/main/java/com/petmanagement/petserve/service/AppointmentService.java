package com.petmanagement.petserve.service;

import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.appointment.AppointmentRequest;
import com.petmanagement.petserve.dto.appointment.AppointmentResponse;
import com.petmanagement.petserve.entity.Appointment;
import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.entity.Reminder;
import com.petmanagement.petserve.entity.ReminderType;
import com.petmanagement.petserve.entity.Veterinarian;
import com.petmanagement.petserve.entity.User;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.AppointmentRepository;
import com.petmanagement.petserve.repository.PetRepository;
import com.petmanagement.petserve.repository.ReminderRepository;
import com.petmanagement.petserve.repository.ReminderTypeRepository;
import com.petmanagement.petserve.repository.VeterinarianRepository;
import com.petmanagement.petserve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Locale;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private static final String APPOINTMENT_SOURCE_TABLE = "appointments";
    private static final String LEGACY_APPOINTMENT_SOURCE_TABLE = "appt";
    private static final String REMINDER_TYPE_SOURCE_TABLE = "pets";

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final ReminderTypeRepository reminderTypeRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PetRepository petRepository,
                              UserRepository userRepository,
                              ReminderRepository reminderRepository,
                              ReminderTypeRepository reminderTypeRepository,
                              VeterinarianRepository veterinarianRepository,
                              JdbcTemplate jdbcTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.reminderRepository = reminderRepository;
        this.reminderTypeRepository = reminderTypeRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public PageResponse<AppointmentResponse> listUpcoming(Integer userId, Integer vetUserId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageRequest = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.ASC, "appointmentDate").and(Sort.by("appointmentTime")));

        Page<Appointment> appointments;
        if (vetUserId != null) {
            appointments = appointmentRepository.findUpcomingByVet(vetUserId, LocalDate.now(), pageRequest);
        } else {
            Integer ownerId = requireUser(userId);
            appointments = appointmentRepository.findUpcomingByUser(ownerId, LocalDate.now(), pageRequest);
        }
        List<AppointmentResponse> records = appointments.stream().map(this::toResponse).collect(Collectors.toList());
        return PageResponse.of(records, appointments.getTotalElements(), safePage, safeSize);
    }

    public PageResponse<AppointmentResponse> listAll(Integer userId, Integer vetUserId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageRequest = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "appointmentDate").and(Sort.by(Sort.Direction.DESC, "appointmentTime")));
        Page<Appointment> appointments;
        if (vetUserId != null) {
            appointments = appointmentRepository.findByVet_UserId(vetUserId, pageRequest);
        } else {
            Integer ownerId = requireUser(userId);
            appointments = appointmentRepository.findByOwner_UserId(ownerId, pageRequest);
        }
        List<AppointmentResponse> records = appointments.stream().map(this::toResponse).collect(Collectors.toList());
        return PageResponse.of(records, appointments.getTotalElements(), safePage, safeSize);
    }

    public PageResponse<AppointmentResponse> listAllForAdmin(int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 500);
        PageRequest pageRequest = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "appointmentDate").and(Sort.by(Sort.Direction.DESC, "appointmentTime")));
        Page<Appointment> appointments = appointmentRepository.findAll(pageRequest);
        List<AppointmentResponse> records = appointments.stream().map(this::toResponse).collect(Collectors.toList());
        return PageResponse.of(records, appointments.getTotalElements(), safePage, safeSize);
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request, Integer userId) {
        if (request.getAppointmentDate() == null) {
            throw new BusinessException(400, "预约日期不能为空");
        }
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        User owner = pet.getOwner();
        boolean isVetActor = userId != null && request.getVetUserId() != null && userId.equals(request.getVetUserId());
        if (userId != null && owner != null && !owner.getUserId().equals(userId) && !isVetActor) {
            throw new BusinessException(403, "无权为该宠物创建预约");
        }

        Appointment appointment = new Appointment();
        appointment.setPet(pet);
        appointment.setOwner(owner);
        appointment.setVet(resolveUserNullable(request.getVetUserId()));
        appointment.setInstitutionId(resolveInstitutionId(request.getInstitutionId(), request.getVetUserId()));
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setAppointmentType(StringUtils.hasText(request.getAppointmentType()) ? request.getAppointmentType() : "诊疗");
        appointment.setReason(request.getReason());
        appointment.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "待确认");
        appointment.setNotes(request.getNotes());

        Appointment saved = appointmentRepository.save(appointment);
        syncAppointmentReminder(saved);
        return toResponse(saved);
    }

    @Transactional
    public AppointmentResponse createByAdmin(AppointmentRequest request) {
        return create(request, null);
    }

    @Transactional
    public AppointmentResponse update(Integer appointmentId, AppointmentRequest request, Integer userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(404, "预约不存在"));
        Integer ownerId = appointment.getOwner() != null ? appointment.getOwner().getUserId() : null;
        boolean isVetActor = userId != null && appointment.getVet() != null && appointment.getVet().getUserId().equals(userId);
        if (ownerId != null && userId != null && !ownerId.equals(userId) && !isVetActor) {
            throw new BusinessException(403, "无权修改该预约");
        }
        if (request.getAppointmentDate() == null) {
            throw new BusinessException(400, "预约日期不能为空");
        }

        appointment.setVet(resolveUserNullable(request.getVetUserId()));
        appointment.setInstitutionId(resolveInstitutionId(request.getInstitutionId(), request.getVetUserId()));
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setAppointmentType(StringUtils.hasText(request.getAppointmentType()) ? request.getAppointmentType() : appointment.getAppointmentType());
        appointment.setReason(request.getReason());
        appointment.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : appointment.getStatus());
        appointment.setNotes(request.getNotes());

        Appointment updated = appointmentRepository.save(appointment);
        syncAppointmentReminder(updated);
        return toResponse(updated);
    }

    @Transactional
    public AppointmentResponse updateByAdmin(Integer appointmentId, AppointmentRequest request) {
        return update(appointmentId, request, null);
    }

    @Transactional
    public void delete(Integer appointmentId, Integer userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(404, "预约不存在"));
        Integer ownerId = appointment.getOwner() != null ? appointment.getOwner().getUserId() : null;
        Integer actorId = requireUser(userId);
        boolean isVetActor = appointment.getVet() != null && appointment.getVet().getUserId().equals(actorId);
        if (ownerId != null && !ownerId.equals(actorId) && !isVetActor) {
            throw new BusinessException(403, "无权删除该预约");
        }
        appointmentRepository.delete(appointment);
    }

    @Transactional
    public void deleteByAdmin(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(404, "预约不存在"));
        appointmentRepository.delete(appointment);
    }

    @Transactional
    public AppointmentResponse confirm(Integer appointmentId, Integer userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(404, "预约不存在"));
        Integer ownerId = appointment.getOwner() != null ? appointment.getOwner().getUserId() : null;
        Integer actorId = requireUser(userId);
        boolean isVetActor = appointment.getVet() != null && appointment.getVet().getUserId().equals(actorId);
        if (ownerId != null && !ownerId.equals(actorId) && !isVetActor) {
            throw new BusinessException(403, "无权确认该预约");
        }
        appointment.setStatus("已确认");
        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    @Transactional
    public AppointmentResponse confirmByAdmin(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(404, "预约不存在"));
        appointment.setStatus("已确认");
        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .appointmentId(appointment.getAppointmentId())
                .petId(appointment.getPet() != null ? appointment.getPet().getPetId() : null)
                .petName(appointment.getPet() != null ? appointment.getPet().getPetName() : null)
                .ownerUserId(appointment.getOwner() != null ? appointment.getOwner().getUserId() : null)
                .ownerUsername(safeUsername(appointment.getOwner()))
                .vetUserId(appointment.getVet() != null ? appointment.getVet().getUserId() : null)
                .vetUsername(safeUsername(appointment.getVet()))
                .institutionId(appointment.getInstitutionId())
                .institutionName(fetchInstitutionName(appointment.getInstitutionId()))
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .appointmentType(appointment.getAppointmentType())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    private String safeUsername(User user) {
        return user != null ? user.getUsername() : null;
    }

    private Integer requireUser(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return userId;
    }

    private User resolveUserNullable(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    private Integer resolveInstitutionId(Integer requestInstitutionId, Integer vetUserId) {
        if (requestInstitutionId != null) {
            return requestInstitutionId;
        }
        if (vetUserId == null) {
            return null;
        }
        try {
            return jdbcTemplate.queryForObject(
                    "select institution_id from veterinarians where user_id = ? limit 1",
                    Integer.class,
                    vetUserId
            );
        } catch (Exception ex) {
            return null;
        }
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

    private void syncAppointmentReminder(Appointment appointment) {
        if (appointment.getAppointmentDate() == null || appointment.getAppointmentId() == null) {
            return;
        }
        Integer ownerId = appointment.getOwner() != null ? appointment.getOwner().getUserId() : null;
        if (ownerId == null) {
            return;
        }
        if (isCancelledStatus(appointment.getStatus())) {
            findAppointmentReminder(appointment.getAppointmentId())
                    .ifPresent(reminderRepository::delete);
            return;
        }
        String typeName = StringUtils.hasText(appointment.getAppointmentType()) ? appointment.getAppointmentType() : "预约提醒";
        Integer reminderTypeId = ensureReminderType(typeName);
        Reminder reminder = findAppointmentReminder(appointment.getAppointmentId())
                .orElseGet(Reminder::new);
        reminder.setReminderType(reminderTypeRepository.findById(reminderTypeId).orElse(null));
        reminder.setPet(appointment.getPet());
        reminder.setUser(appointment.getOwner());
        reminder.setVeterinarian(resolveVetByUser(appointment.getVet()));
        reminder.setSourceTable(APPOINTMENT_SOURCE_TABLE);
        reminder.setSourceRecordId(appointment.getAppointmentId());
        reminder.setTitle(typeName + "提醒");
        reminder.setMessage(buildReminderMessage(appointment));
        reminder.setDueDate(appointment.getAppointmentDate());
        reminder.setReminderDate(appointment.getAppointmentDate());
        reminder.setCompleted(false);
        reminderRepository.save(reminder);
    }

    private Integer ensureReminderType(String typeName) {
        Optional<ReminderType> exist = reminderTypeRepository.findByTypeName(typeName);
        if (exist.isPresent()) {
            return exist.get().getReminderTypeId();
        }
        ReminderType rt = new ReminderType();
        rt.setTypeName(typeName);
        rt.setSourceTable(REMINDER_TYPE_SOURCE_TABLE);
        rt.setSourceField("appointment_date");
        rt.setAdvanceDays(1);
        rt.setTemplateMessage(typeName + "提醒：请按时到店");
        rt.setAutoGenerated(true);
        return reminderTypeRepository.save(rt).getReminderTypeId();
    }

    private Optional<Reminder> findAppointmentReminder(Integer appointmentId) {
        Optional<Reminder> reminder = reminderRepository
                .findBySourceTableAndSourceRecordId(APPOINTMENT_SOURCE_TABLE, appointmentId);
        if (reminder.isPresent()) {
            return reminder;
        }
        return reminderRepository
                .findBySourceTableAndSourceRecordId(LEGACY_APPOINTMENT_SOURCE_TABLE, appointmentId);
    }

    private boolean isCancelledStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return false;
        }
        String normalized = status.trim().toLowerCase(Locale.ROOT);
        return "已取消".equals(status) || "取消".equals(status)
                || "cancelled".equals(normalized) || "canceled".equals(normalized);
    }

    private Veterinarian resolveVetByUser(User vetUser) {
        if (vetUser == null || vetUser.getUserId() == null) {
            return null;
        }
        return veterinarianRepository.findByUser_UserId(vetUser.getUserId()).orElse(null);
    }

    private String buildReminderMessage(Appointment appointment) {
        String timePart = appointment.getAppointmentTime() != null ? " " + appointment.getAppointmentTime().toString() : "";
        String reason = StringUtils.hasText(appointment.getReason()) ? appointment.getReason() : "请按时到店";
        return "[" + appointment.getAppointmentType() + "] " + reason + timePart;
    }
}

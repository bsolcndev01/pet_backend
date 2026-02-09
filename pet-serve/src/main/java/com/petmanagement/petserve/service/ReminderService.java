package com.petmanagement.petserve.service;

import com.petmanagement.petserve.common.PageResponse;
import com.petmanagement.petserve.dto.reminder.ReminderRequest;
import com.petmanagement.petserve.dto.reminder.ReminderResponse;
import com.petmanagement.petserve.entity.Pet;
import com.petmanagement.petserve.entity.Reminder;
import com.petmanagement.petserve.entity.ReminderType;
import com.petmanagement.petserve.entity.User;
import com.petmanagement.petserve.entity.Veterinarian;
import com.petmanagement.petserve.exception.BusinessException;
import com.petmanagement.petserve.repository.PetRepository;
import com.petmanagement.petserve.repository.ReminderRepository;
import com.petmanagement.petserve.repository.ReminderTypeRepository;
import com.petmanagement.petserve.repository.VeterinarianRepository;
import com.petmanagement.petserve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderTypeRepository reminderTypeRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;

    @Autowired
    public ReminderService(ReminderRepository reminderRepository, ReminderTypeRepository reminderTypeRepository, PetRepository petRepository, UserRepository userRepository, VeterinarianRepository veterinarianRepository) {
        this.reminderRepository = reminderRepository;
        this.reminderTypeRepository = reminderTypeRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    public PageResponse<ReminderResponse> listUpcoming(Integer userId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageRequest = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.ASC, "reminderDate"));
        Integer uid = requireUserId(userId);
        Page<Reminder> reminders = reminderRepository.findUpcomingByUser(uid, LocalDate.now(), pageRequest);
        List<ReminderResponse> records = reminders.stream().map(this::toResponse).collect(Collectors.toList());
        return PageResponse.of(records, reminders.getTotalElements(), safePage, safeSize);
    }

    public PageResponse<ReminderResponse> listAll(Integer userId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageRequest = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "reminderDate"));
        Integer uid = requireUserId(userId);
        Page<Reminder> reminders = reminderRepository.findByUser_UserId(uid, pageRequest);
        List<ReminderResponse> records = reminders.stream().map(this::toResponse).collect(Collectors.toList());
        return PageResponse.of(records, reminders.getTotalElements(), safePage, safeSize);
    }

    @Transactional
    public ReminderResponse create(ReminderRequest request) {
        ReminderType type = reminderTypeRepository.findById(request.getReminderTypeId())
                .orElseThrow(() -> new BusinessException(404, "提醒类型不存在"));
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new BusinessException(404, "宠物不存在"));
        User owner = pet.getOwner();
        if (owner == null) {
            throw new BusinessException(400, "宠物缺少归属用户");
        }
        if (request.getUserId() != null && !owner.getUserId().equals(request.getUserId())) {
            throw new BusinessException(400, "用户ID必须为宠物主人");
        }
        User user = owner;
        Veterinarian veterinarian = null;
        if (request.getVetId() != null) {
            veterinarian = veterinarianRepository.findById(request.getVetId())
                    .orElseThrow(() -> new BusinessException(404, "兽医不存在"));
        }

        Reminder reminder = new Reminder();
        reminder.setReminderType(type);
        reminder.setPet(pet);
        reminder.setUser(user);
        reminder.setVeterinarian(veterinarian);
        reminder.setSourceTable(type.getSourceTable());
        reminder.setSourceRecordId(request.getSourceRecordId());
        reminder.setTitle(request.getTitle());
        reminder.setMessage(request.getMessage());
        reminder.setDueDate(request.getDueDate());
        reminder.setReminderDate(request.getReminderDate());
        reminder.setCompleted(false);
        reminder.setSentCount(0);

        Reminder saved = reminderRepository.save(reminder);
        return toResponse(saved);
    }

    private Integer requireUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return userId;
    }

    private ReminderResponse toResponse(Reminder reminder) {
        return ReminderResponse.builder()
                .reminderId(reminder.getReminderId())
                .reminderTypeId(reminder.getReminderType() != null ? reminder.getReminderType().getReminderTypeId() : null)
                .reminderTypeName(reminder.getReminderType() != null ? reminder.getReminderType().getTypeName() : null)
                .petId(reminder.getPet() != null ? reminder.getPet().getPetId() : null)
                .petName(reminder.getPet() != null ? reminder.getPet().getPetName() : null)
                .userId(reminder.getUser() != null ? reminder.getUser().getUserId() : null)
                .vetId(reminder.getVeterinarian() != null ? reminder.getVeterinarian().getVetId() : null)
                .vetName(reminder.getVeterinarian() != null && reminder.getVeterinarian().getUser() != null ? reminder.getVeterinarian().getUser().getUsername() : null)
                .title(reminder.getTitle())
                .message(reminder.getMessage())
                .dueDate(reminder.getDueDate())
                .reminderDate(reminder.getReminderDate())
                .completed(reminder.getCompleted())
                .build();
    }
}

package com.petmanagement.petserve.repository;

import com.petmanagement.petserve.entity.UserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Integer> {
    List<UserFeedback> findByUserIdOrderByCreatedAtDesc(Integer userId);
}

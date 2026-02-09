package com.petmanagement.petserve.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PasswordResetService {

    private static class ResetInfo {
        String code;
        LocalDateTime expireAt;
    }

    private final Map<String, ResetInfo> codeStore = new ConcurrentHashMap<>();

    public String generateCode(String email, int minutes) {
        ResetInfo info = new ResetInfo();
        info.code = String.format("%06d", (int) (Math.random() * 1_000_000));
        info.expireAt = LocalDateTime.now().plusMinutes(minutes);
        codeStore.put(email, info);
        return info.code;
    }

    public boolean verify(String email, String code) {
        ResetInfo info = codeStore.get(email);
        if (info == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(info.expireAt)) {
            codeStore.remove(email);
            return false;
        }
        return info.code.equals(code);
    }

    public void consume(String email) {
        codeStore.remove(email);
    }
}

package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.domain.PasswordResetToken;
import com.PedroMaia.auth_server.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final static int HOURS_TO_RESET_PASSWORD = 1;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    
    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Transactional
    public String createPasswordResetToken(User user) {
       String token = UUID.randomUUID().toString();

       PasswordResetToken passwordResetTokens = new PasswordResetToken();

       passwordResetTokens.setToken(token);
       passwordResetTokens.setUserId(user.getId());
       passwordResetTokens.setExpiryDate(LocalDateTime.now().plusHours(HOURS_TO_RESET_PASSWORD));

       passwordResetTokenRepository.save(passwordResetTokens);

       return token;
    }

    public boolean confirmPasswordResetToken(String token) {
        PasswordResetToken passwordResetTokens = passwordResetTokenRepository.findByToken(token);
        if (passwordResetTokens == null || passwordResetTokens.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        passwordResetTokenRepository.delete(passwordResetTokens);
        return true;
    }

    public boolean verifyPasswordResetToken(String token) {
        PasswordResetToken passwordResetTokens = passwordResetTokenRepository.findByToken(token);
        return passwordResetTokens != null && passwordResetTokens.getExpiryDate().isAfter(LocalDateTime.now());
    }
}

package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.domain.PasswordResetToken;
import com.PedroMaia.auth_server.event.OnPasswordResetEvent;
import com.PedroMaia.auth_server.repository.PasswordResetTokenRepository;
import com.PedroMaia.auth_server.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final static int HOURS_TO_RESET_PASSWORD = 1;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    
    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    private String createPasswordResetToken(User user) {
       String token = UUID.randomUUID().toString();

       PasswordResetToken passwordResetToken = new PasswordResetToken();

       passwordResetToken.setToken(token);
       passwordResetToken.setUserId(user.getId());
       passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(HOURS_TO_RESET_PASSWORD));

       passwordResetTokenRepository.save(passwordResetToken);

       return token;
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }

        passwordResetTokenRepository.deleteByUserId(user.getId());

        String token = createPasswordResetToken(user);
        eventPublisher.publishEvent(new OnPasswordResetEvent(user, token));
    }

    public void verifyToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        checkToken(passwordResetToken);
    }

    @Transactional
    public void confirmPasswordReset(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        checkToken(passwordResetToken);

        User user = userRepository.findById(passwordResetToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);
    }

    private void checkToken(PasswordResetToken passwordResetToken) {
        if (passwordResetToken == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");
        }
        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");
        }
    }
}

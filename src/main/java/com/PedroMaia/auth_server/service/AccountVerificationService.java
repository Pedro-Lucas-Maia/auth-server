package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.domain.VerificationToken;
import com.PedroMaia.auth_server.repository.VerificationTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountVerificationService {
    private final static int HOURS_TO_VERIFY = 24;
    private final VerificationTokenRepository verificationTokenRepository;

    public AccountVerificationService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public String createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserId(user.getId());
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(HOURS_TO_VERIFY));
        verificationTokenRepository.save(verificationToken);

        return token;
    }

    @Transactional
    public UUID verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired verification token");
        }
        verificationTokenRepository.delete(verificationToken);
        return verificationToken.getUserId();
    }

}

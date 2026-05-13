package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.dto.LoginRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import com.PedroMaia.auth_server.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;


@Service
public class LoginService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int MINUTES_OF_LOCKOUT = 15;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(noRollbackFor = ResponseStatusException.class) // don't roll back on ResponseStatusException to allow failed login attempts to be saved
    public UserResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = fetchUserOrThrow(loginRequestDTO.email());

        verifyAccountIsNotLocked(user, LocalDateTime.now());

        if (!isPasswordValid(user, loginRequestDTO.password())) {
            handleFailedAttempt(user);
        }

        return handleSuccessfulLogin(user);
    }

    private User fetchUserOrThrow(String email) throws ResponseStatusException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }

    private void verifyAccountIsNotLocked(User user, LocalDateTime now) throws ResponseStatusException {
        if (user.isLocked()) {
            if(user.getLockoutMoment() != null && user.getLockoutMoment().plusMinutes(MINUTES_OF_LOCKOUT).isBefore(now)) {
                user.setLocked(false);
                user.setFailedLoginAttempts(0);
                user.setLockoutMoment(null);
                userRepository.save(user);
            } else {
                throw new ResponseStatusException(HttpStatus.LOCKED, "Account is locked. Please try again later.");
            }
        }
    }
    private boolean isPasswordValid(User user, String requestPassword) {
        String dbPassword = user.getPassword();
        return passwordEncoder.matches(requestPassword, dbPassword);
    }

    private void handleFailedAttempt(User user) throws ResponseStatusException {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setLocked(true);
            user.setLockoutMoment(LocalDateTime.now());
        }
        userRepository.save(user);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    private UserResponseDTO handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRoleName());
    }
}

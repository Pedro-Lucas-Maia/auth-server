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
public class ProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserResponseDTO getProfile(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRoleName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    public UserResponseDTO updateProfileName(String name, String email) {
        User user = fetchUserOrThrow(email);

        if (user.getName().equals(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New name cannot be the same as the current name");
        }

        user.setName(name);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRoleName());
    }

    @Transactional
    public UserResponseDTO updateProfilePassword(String oldPassword, String newPassword, String email) {
        User user = fetchUserOrThrow(email);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is incorrect");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "New password cannot be the same");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRoleName());
    }

    @Transactional
    public void deleteProfile(String password, String email) {
        User user = fetchUserOrThrow(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password is incorrect");
        }

        userRepository.delete(user);
    }

    private User fetchUserOrThrow(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

}

package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.domain.Role;
import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.dto.RegisterRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import com.PedroMaia.auth_server.event.UserRegisteredEvent;
import com.PedroMaia.auth_server.repository.RoleRepository;
import com.PedroMaia.auth_server.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccountVerificationService accountVerificationService;

    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, ApplicationEventPublisher applicationEventPublisher, AccountVerificationService accountVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.accountVerificationService = accountVerificationService;
    }

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        checkEmailUniqueness(registerRequestDTO.email());

        Role role = findRoleOrThrow();

        User user = saveUserToDb(registerRequestDTO, role);

        publishRegisterEvent(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRoleName());
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
    }

    private Role findRoleOrThrow() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found"));
    }

    private User saveUserToDb(RegisterRequestDTO registerRequestDTO, Role role) {
        String encodedPassword = passwordEncoder.encode(registerRequestDTO.password());
        User newUser = new User();
        newUser.setName(registerRequestDTO.name());
        newUser.setEmail(registerRequestDTO.email());
        newUser.setPassword(encodedPassword);
        newUser.setRoleId(role.getId());
        newUser.setLocked(false);
        newUser.setEnabled(false);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(newUser);
        return newUser;
    }

    private void publishRegisterEvent(User user) {
        String token = accountVerificationService.createVerificationToken(user);

        UserRegisteredEvent event = new UserRegisteredEvent(user, token);

        applicationEventPublisher.publishEvent(event);
    }

    @Transactional
    public UserResponseDTO verifyToken(String token) {
        var userId = accountVerificationService.verifyToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User not found after token verification"));

        user.setEnabled(true);
        userRepository.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRoleName());
    }
}

package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.domain.Role;
import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.dto.RegisterRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import com.PedroMaia.auth_server.repository.RoleRepository;
import com.PedroMaia.auth_server.repository.UserRepository;
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

    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        checkEmailUniqueness(registerRequestDTO.email());

        Role role = findRoleOrThrow();

        User user = saveUserToDb(registerRequestDTO, role);

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
        newUser.setEnabled(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setLastLoginAt(LocalDateTime.now()); // Set lastLoginAt at the register, because register returns a token, smooth register -> login flow
        userRepository.save(newUser);
        return newUser;
    }
}

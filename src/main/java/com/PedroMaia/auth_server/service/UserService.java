package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.dto.LoginRequestDTO;
import com.PedroMaia.auth_server.dto.RegisterRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import com.PedroMaia.auth_server.repository.RoleRepository;
import com.PedroMaia.auth_server.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        String encodedPassword = passwordEncoder.encode(registerRequestDTO.password());
        User newUser = new User();
        newUser.setName(registerRequestDTO.name());
        newUser.setEmail(registerRequestDTO.email());
        newUser.setPassword(encodedPassword);
        newUser.setRole(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found")));
        userRepository.save(newUser);

        return new UserResponseDTO(newUser.getName(), newUser.getEmail(), newUser.getRole().getName());
    }

    public UserResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.email()).orElse(null);
        if (user == null || !isLoginCorrect(user, loginRequestDTO.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return new UserResponseDTO(user.getName(), user.getEmail(), user.getRole().getName());
    }

    private boolean isLoginCorrect(User user, String requestPassword) {
        String db_password = user.getPassword();
        return passwordEncoder.matches(requestPassword, db_password);
    }

    public UserResponseDTO me(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return new UserResponseDTO(user.getName(), user.getEmail(), user.getRole().getName());
    }
}

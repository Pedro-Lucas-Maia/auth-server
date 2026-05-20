package com.PedroMaia.auth_server.unitTests;


import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.dto.LoginRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import com.PedroMaia.auth_server.repository.UserRepository;
import com.PedroMaia.auth_server.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Assertions;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class LoginServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    public void testValidLogin() throws ResponseStatusException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("joaozinho@gmail.com", "12345678");
        User  user = createDefaultUser();

        when(userRepository.findByEmail("joaozinho@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(true);

        UserResponseDTO userResponseDTO = loginService.login(loginRequestDTO);

        verify(userRepository, times(1)).findByEmail(loginRequestDTO.email());
        verify(passwordEncoder, times(1)).matches(any(), eq(loginRequestDTO.password()));
        Assertions.assertEquals(userResponseDTO.email(), (user.getEmail()));
        Assertions.assertEquals(userResponseDTO.name(), (user.getName()));
        Assertions.assertEquals(userResponseDTO.role(), (user.getRoleName()));
    }

    @Test
    @DisplayName("Should throw if user don't exist in the database")
    public void testUserNotFound() throws ResponseStatusException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("joaozinho@gmail.com", "12345678");

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
    }

    @Test
    @DisplayName("Should throw if password is invalid")
    public void testInvalidPassword() throws ResponseStatusException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("joaozinho@gmail.com", "12345678");
        User  user = createDefaultUser();

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(false);

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
    }

    @Test
    @DisplayName("Should throw if account is not verified")
    public void testAccountNotVerified() throws ResponseStatusException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("joaozinho@gmail.com", "12345678");
        User user = createDefaultUser();
        user.setEnabled(false);

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
    }

    @Test
    @DisplayName("Should throw if account is locked")
    public void testAccountLocked() throws ResponseStatusException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("joaozinho@gmail.com", "12345678");
        User user = createDefaultUser();
        user.setLocked(true);

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
    }

    @Test
    @DisplayName("Should lock account after 5 failed login attempts")
    public void testAccountLockAfterFailedAttempts() throws ResponseStatusException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("joaozinho@gmail.com", "12345678");
        User user = createDefaultUser();
        user.setFailedLoginAttempts(4);

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(false);

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
    }

    private User createDefaultUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Joãozinho");
        user.setEmail("joaozinho@gmail.com");
        user.setPassword("12345678");
        user.setRoleName("USER");
        user.setLocked(false);
        user.setEnabled(true);
        user.setFailedLoginAttempts(0);
        user.setLockoutMoment(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);
        user.setLastLoginAt(null);

        return user;
    }
}

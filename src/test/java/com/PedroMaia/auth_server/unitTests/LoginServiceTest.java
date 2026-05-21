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

import java.time.LocalDateTime;
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

    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    public void getLoginRequestDTO() {
        this.loginRequestDTO = new LoginRequestDTO("joaozinho@gmail.com", "12345678");
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    public void testValidLogin() {
        User  user = createDefaultUser();

        when(userRepository.findByEmail("joaozinho@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO userResponseDTO = loginService.login(loginRequestDTO);

        verify(userRepository, times(1)).findByEmail(loginRequestDTO.email());
        verify(passwordEncoder, times(1)).matches(any(), eq(loginRequestDTO.password()));
        verify(userRepository, times(1)).save(user);
        Assertions.assertEquals(0, user.getFailedLoginAttempts());
        Assertions.assertNotNull(user.getLastLoginAt());
        Assertions.assertEquals(userResponseDTO.email(), (user.getEmail()));
        Assertions.assertEquals(userResponseDTO.name(), (user.getName()));
        Assertions.assertEquals(userResponseDTO.role(), (user.getRoleName()));
    }

    @Test
    @DisplayName("Should throw if user don't exist in the database")
    public void testUserNotFound() throws ResponseStatusException {
        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));

        verify(userRepository, times(1)).findByEmail(loginRequestDTO.email());
    }

    @Test
    @DisplayName("Should throw if password is invalid")
    public void testInvalidPassword() throws ResponseStatusException {
        User  user = createDefaultUser();

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(false);

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
    }

    @Test
    @DisplayName("Should throw if account is not verified")
    public void testAccountNotVerified() throws ResponseStatusException {
        User user = createDefaultUser();
        user.setEnabled(false);

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
    }

    @Test
    @DisplayName("Should throw if account is locked")
    public void testAccountLocked() throws ResponseStatusException {
        User user = createDefaultUser();
        user.setLocked(true);

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
    }

    @Test
    @DisplayName("Should lock account after 5 failed login attempts")
    public void testAccountLockAfterFailedAttempts() throws ResponseStatusException {
        User user = createDefaultUser();
        user.setFailedLoginAttempts(4);

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq("12345678"))).thenReturn(false);

        Assertions.assertThrows(ResponseStatusException.class, () -> loginService.login(loginRequestDTO));
        Assertions.assertTrue(user.isLocked(), "User should be locked after 5 failed attempts");
        Assertions.assertEquals(5, user.getFailedLoginAttempts());
        Assertions.assertNotNull(user.getLockoutMoment());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should unlock account and login successfully if lockout time has expired")
    public void testLockoutExpired() {
        User user = createDefaultUser();
        user.setLocked(true);
        user.setLockoutMoment(LocalDateTime.now().minusMinutes(20));

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        loginService.login(loginRequestDTO);

        Assertions.assertFalse(user.isLocked());
        Assertions.assertNull(user.getLockoutMoment());

        verify(userRepository, times(2)).save(user);
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

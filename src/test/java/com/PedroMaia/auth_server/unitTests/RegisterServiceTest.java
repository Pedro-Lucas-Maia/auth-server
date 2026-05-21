package com.PedroMaia.auth_server.unitTests;

import com.PedroMaia.auth_server.domain.Role;
import com.PedroMaia.auth_server.domain.User;
import com.PedroMaia.auth_server.dto.RegisterRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import com.PedroMaia.auth_server.event.OnUserRegisterEvent;
import com.PedroMaia.auth_server.repository.RoleRepository;
import com.PedroMaia.auth_server.repository.UserRepository;
import com.PedroMaia.auth_server.service.AccountVerificationService;
import com.PedroMaia.auth_server.service.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class RegisterServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private AccountVerificationService accountVerificationService;

    @InjectMocks
    private RegisterService registerService;

    private RegisterRequestDTO registerRequestDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void getRegisterRequestDTO() {
        this.registerRequestDTO = new RegisterRequestDTO("Joãozinho", "joaozinho@gmail.com", "12345678");
    }

    @Test
    @DisplayName("Should successfully register with valid credentials")
    public void testValidRegister () {
        User targetUser = createUser();
        when(userRepository.existsByEmail("joaozinho@gmail.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role(UUID.randomUUID(), "ROLE_USER")));
        when(passwordEncoder.encode("12345678")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(targetUser);
        when(accountVerificationService.createVerificationToken(targetUser)).thenReturn("MockedToken");

        UserResponseDTO userResponseDTO = registerService.register(registerRequestDTO);

        verify(userRepository, times(1)).existsByEmail(registerRequestDTO.email());
        verify(userRepository, times(1)).save(targetUser);
        verify(passwordEncoder, times(1)).encode(registerRequestDTO.password());
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(accountVerificationService, times(1)).createVerificationToken(targetUser);
        verify(applicationEventPublisher, times(1)).publishEvent(any(OnUserRegisterEvent.class));

        Assertions.assertEquals(registerRequestDTO.email(), userResponseDTO.email());
        Assertions.assertEquals(registerRequestDTO.name(), userResponseDTO.name());
        Assertions.assertEquals(("ROLE_USER"), userResponseDTO.role());
    }

    @Test
    @DisplayName("Should throw if email is already in use")
    public void testEmailAlreadyInUse() throws ResponseStatusException {
        when(userRepository.existsByEmail(registerRequestDTO.email())).thenReturn(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> registerService.register(registerRequestDTO));

        verify(userRepository, times(1)).existsByEmail(registerRequestDTO.email());
    }

    @Test
    @DisplayName("Should throw if default role is not found in the database")
    public void testDefaultRoleNotFound() throws ResponseStatusException {
        when(userRepository.existsByEmail(registerRequestDTO.email())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> registerService.register(registerRequestDTO));

        verify(roleRepository, times(1)).findByName("ROLE_USER");
    }

    @Test
    @DisplayName("Should validate token and enable user")
    public void testValidateTokenAndEnableUser() {
        String token = UUID.randomUUID().toString();
        User targetUser = createUser();

        when(accountVerificationService.verifyToken(token)).thenReturn(targetUser.getId());
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenReturn(targetUser);

        registerService.verifyToken(token);

        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(accountVerificationService, times(1)).verifyToken(token);
        verify(userRepository, times(1)).save(targetUser);

        Assertions.assertTrue(targetUser.isEnabled());
    }

    @Test
    @DisplayName("Should throw if can't find the user by it's id")
    public void testFindUserById() {
        String token = UUID.randomUUID().toString();
        when(accountVerificationService.verifyToken(token)).thenReturn(UUID.randomUUID());
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> registerService.verifyToken(token));

        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(accountVerificationService, times(1)).verifyToken(token);

    }

    private User createUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Joãozinho");
        user.setEmail("joaozinho@gmail.com");
        user.setPassword("12345678");
        user.setRoleName("ROLE_USER");
        user.setLocked(false);
        user.setEnabled(false);
        user.setFailedLoginAttempts(0);
        user.setLockoutMoment(null);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);
        user.setLastLoginAt(null);

        return user;
    }
}

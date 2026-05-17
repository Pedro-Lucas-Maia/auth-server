package com.PedroMaia.auth_server.controller;

import com.PedroMaia.auth_server.dto.*;
import com.PedroMaia.auth_server.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.login(loginRequestDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponseDTO.cookie())
                .body(new UserResponseDTO(authResponseDTO.id(), authResponseDTO.name(), authResponseDTO.email(), authResponseDTO.role()));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO> logout() {
        String responseCookie = authService.getCleanCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie)
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        UserResponseDTO userResponseDTO = authService.register(registerRequestDTO);

        return  ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @GetMapping("/verify")
    public ResponseEntity<UserResponseDTO> verify(@RequestParam("token") String token) {
      AuthResponseDTO authResponseDTO = authService.verifyToken(token);
      return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, authResponseDTO.cookie())
              .body(new UserResponseDTO(authResponseDTO.id(), authResponseDTO.name(), authResponseDTO.email(), authResponseDTO.role()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        authService.initiatePasswordReset(forgotPasswordRequestDTO.email());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/password-reset/validate")
    public ResponseEntity<UserResponseDTO> validatePasswordReset(@RequestParam("token") String token) {
        authService.validatePasswordResetToken(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> confirmPasswordReset(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        authService.confirmPasswordReset(resetPasswordRequestDTO.token(), resetPasswordRequestDTO.newPassword());
        return ResponseEntity.ok().build();
    }
}

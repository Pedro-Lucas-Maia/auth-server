package com.PedroMaia.auth_server.controller;

import com.PedroMaia.auth_server.dto.AuthResponseDTO;
import com.PedroMaia.auth_server.dto.LoginRequestDTO;
import com.PedroMaia.auth_server.dto.RegisterRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import com.PedroMaia.auth_server.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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
                .body(new UserResponseDTO(authResponseDTO.name(), authResponseDTO.email(), authResponseDTO.role()));
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
        AuthResponseDTO authResponseDTO = authService.register(registerRequestDTO);

        return  ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponseDTO.cookie())
                .body(new UserResponseDTO(authResponseDTO.name(), authResponseDTO.email(), authResponseDTO.role()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        AuthResponseDTO authResponseDTO = authService.me(email);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponseDTO.cookie())
                .body(new UserResponseDTO(authResponseDTO.name(), authResponseDTO.email(), authResponseDTO.role()));
    }
}

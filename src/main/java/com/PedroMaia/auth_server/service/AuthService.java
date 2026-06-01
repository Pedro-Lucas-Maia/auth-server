package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.dto.AuthResponseDTO;
import com.PedroMaia.auth_server.dto.LoginRequestDTO;
import com.PedroMaia.auth_server.dto.RegisterRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final ProfileService profileService;
    private final LoginService loginService;
    private final CookieService cookieService;
    private final RegisterService registerService;
    private final PasswordResetService passwordResetService;

    public AuthService(ProfileService profileService, LoginService loginService, CookieService cookieService, RegisterService registerService, PasswordResetService passwordResetService) {
        this.profileService = profileService;
        this.loginService = loginService;
        this.cookieService = cookieService;
        this.registerService = registerService;
        this.passwordResetService = passwordResetService;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        UserResponseDTO user = loginService.login(loginRequestDTO);
        return new AuthResponseDTO(user.id(), user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }

    public String getCleanCookie() {
        return cookieService.getCleanCookie();
    }

    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
       return registerService.register(registerRequestDTO);
    }

    public AuthResponseDTO getProfile(String email) {
        UserResponseDTO user = profileService.getProfile(email);
        return new AuthResponseDTO(user.id(), user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }

    public AuthResponseDTO verifyToken(String token) {
        UserResponseDTO userResponseDTO = registerService.verifyToken(token);
        return new AuthResponseDTO(userResponseDTO.id(), userResponseDTO.name(), userResponseDTO.email(), userResponseDTO.role(), cookieService.generateTokenCookie(userResponseDTO.email()));
    }

    public void initiatePasswordReset(String email) {
        passwordResetService.initiatePasswordReset(email);
    }

    public void validatePasswordResetToken(String token) {
        passwordResetService.verifyToken(token);
    }

    public void confirmPasswordReset(String token, String newPassword) {
        passwordResetService.confirmPasswordReset(token, newPassword);
    }
}

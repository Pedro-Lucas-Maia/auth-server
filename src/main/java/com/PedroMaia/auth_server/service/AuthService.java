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

    public AuthService(ProfileService profileService, LoginService loginService, CookieService cookieService, RegisterService registerService) {
        this.profileService = profileService;
        this.loginService = loginService;
        this.cookieService = cookieService;
        this.registerService = registerService;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        UserResponseDTO user = loginService.login(loginRequestDTO);
        return new AuthResponseDTO(user.id(), user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }

    public String getCleanCookie() {
        return cookieService.getCleanCookie();
    }

    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        UserResponseDTO user = registerService.register(registerRequestDTO);
        return new AuthResponseDTO(user.id(), user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }

    public AuthResponseDTO getProfile(String email) {
        UserResponseDTO user = profileService.getProfile(email);
        return new AuthResponseDTO(user.id(), user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }
}

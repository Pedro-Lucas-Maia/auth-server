package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.dto.AuthResponseDTO;
import com.PedroMaia.auth_server.dto.LoginRequestDTO;
import com.PedroMaia.auth_server.dto.RegisterRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final CookieService cookieService;

    public AuthService(UserService userService, CookieService cookieService) {
        this.userService = userService;
        this.cookieService = cookieService;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        UserResponseDTO user = userService.login(loginRequestDTO);
        return new AuthResponseDTO(user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }

    public String getCleanCookie() {
        return cookieService.getCleanCookie();
    }

    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        UserResponseDTO user = userService.register(registerRequestDTO);
        return new AuthResponseDTO(user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }

    public AuthResponseDTO me(String email) {
        UserResponseDTO user = userService.me(email);
        return new AuthResponseDTO(user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }
}

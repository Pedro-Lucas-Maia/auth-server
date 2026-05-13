package com.PedroMaia.auth_server.service;

import com.PedroMaia.auth_server.dto.AuthResponseDTO;
import com.PedroMaia.auth_server.dto.LoginRequestDTO;
import com.PedroMaia.auth_server.dto.RegisterRequestDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final LoginService loginService;
    private final CookieService cookieService;
    private final RegisterService registerService;

    public AuthService(UserService userService, LoginService loginService, CookieService cookieService, RegisterService registerService) {
        this.userService = userService;
        this.loginService = loginService;
        this.cookieService = cookieService;
        this.registerService = registerService;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        UserResponseDTO user = loginService.login(loginRequestDTO);
        return new AuthResponseDTO(user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }

    public String getCleanCookie() {
        return cookieService.getCleanCookie();
    }

    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        UserResponseDTO user = registerService.register(registerRequestDTO);
        return new AuthResponseDTO(user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }

    public AuthResponseDTO me(String email) {
        UserResponseDTO user = userService.me(email);
        return new AuthResponseDTO(user.name(), user.email(), user.role(), cookieService.generateTokenCookie(user.email()));
    }
}

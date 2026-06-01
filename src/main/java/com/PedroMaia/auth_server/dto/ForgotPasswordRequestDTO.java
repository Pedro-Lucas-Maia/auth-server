package com.PedroMaia.auth_server.dto;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequestDTO(@Email String email) {
}

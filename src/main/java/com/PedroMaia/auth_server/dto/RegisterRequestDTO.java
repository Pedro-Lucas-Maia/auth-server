package com.PedroMaia.auth_server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequestDTO(
        @NotBlank(message = "Name is required")
        @Length(min = 2,  max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Length(min = 8,  max = 20, message = "Password must be between 8 and 20 characters")
        String password
) {
}

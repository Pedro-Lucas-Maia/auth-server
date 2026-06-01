package com.PedroMaia.auth_server.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ResetPasswordRequestDTO(
        @NotBlank String token,

        @NotBlank
        @Length(min = 8,  max = 20, message = "New password must be between 8 and 20 characters")
        String newPassword
) {
}

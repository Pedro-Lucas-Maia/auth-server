package com.PedroMaia.auth_server.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateNameDTO(
        @NotBlank(message = "Name is required")
        @Length(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name
) {
}
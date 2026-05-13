package com.PedroMaia.auth_server.dto;

import java.util.UUID;

public record UserResponseDTO(UUID Id, String name, String email, String role) {
}

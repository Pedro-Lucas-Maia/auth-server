package com.PedroMaia.auth_server.dto;

import java.util.UUID;

public record AuthResponseDTO(UUID id, String name, String email, String role, String cookie) {
}

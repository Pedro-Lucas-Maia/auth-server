package com.PedroMaia.auth_server.dto;

public record AuthResponseDTO(String name, String email, String role, String cookie) {
}

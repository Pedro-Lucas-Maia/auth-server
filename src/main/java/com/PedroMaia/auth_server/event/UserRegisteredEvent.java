package com.PedroMaia.auth_server.event;


import com.PedroMaia.auth_server.domain.User;

public record UserRegisteredEvent (User user, String token) {
}
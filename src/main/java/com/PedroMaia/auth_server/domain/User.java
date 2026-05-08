package com.PedroMaia.auth_server.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    @Id
    private UUID id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean isLocked;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime upDateAt;
    private LocalDateTime lastLoginAt;

    public User() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpDateAt() {
        return upDateAt;
    }

    public void setUpDateAt(LocalDateTime upDateAt) {
        this.upDateAt = upDateAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}

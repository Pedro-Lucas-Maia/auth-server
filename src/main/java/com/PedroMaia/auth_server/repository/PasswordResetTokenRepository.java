package com.PedroMaia.auth_server.repository;

import com.PedroMaia.auth_server.domain.PasswordResetToken;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends ListCrudRepository<PasswordResetToken, UUID> {
    PasswordResetToken findByToken(String token);
}

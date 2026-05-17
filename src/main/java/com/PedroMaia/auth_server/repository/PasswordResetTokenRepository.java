package com.PedroMaia.auth_server.repository;

import com.PedroMaia.auth_server.domain.PasswordResetToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends ListCrudRepository<PasswordResetToken, UUID> {
    PasswordResetToken findByToken(String token);

    @Modifying
    @Query("DELETE FROM password_reset_tokens WHERE user_id = :userId")
    void deleteByUserId(UUID userId);
}

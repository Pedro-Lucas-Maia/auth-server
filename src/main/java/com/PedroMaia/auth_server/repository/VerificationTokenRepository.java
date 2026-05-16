package com.PedroMaia.auth_server.repository;

import com.PedroMaia.auth_server.domain.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, UUID> {
    VerificationToken findByToken(String token);
}

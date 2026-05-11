package com.PedroMaia.auth_server.repository;

import com.PedroMaia.auth_server.domain.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends ListCrudRepository<User, UUID> {

    @Query("""
        SELECT u.*,
               r.name AS role_name
        FROM users u
        JOIN roles r ON u.role_id = r.id
        WHERE u.email = :email
    """)
    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);
}

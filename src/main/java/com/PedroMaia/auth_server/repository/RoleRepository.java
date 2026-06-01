package com.PedroMaia.auth_server.repository;

import com.PedroMaia.auth_server.domain.Role;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends ListCrudRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}

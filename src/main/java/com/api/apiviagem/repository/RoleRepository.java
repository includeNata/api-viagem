package com.api.apiviagem.repository;

import com.api.apiviagem.model.Role;
import com.api.apiviagem.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Busca uma role pelo seu tipo.
     */
    Optional<Role> findByName(RoleType name);

    /**
     * Verifica se uma role com o tipo especificado existe.
     */
    boolean existsByName(RoleType name);

    /**
     * Deleta uma role pelo seu tipo.
     */
    void deleteByName(RoleType name);
}

package com.api.apiviagem.repository;

import com.api.apiviagem.model.RoleType;
import com.api.apiviagem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // O Spring Data JPA cria a query automaticamente a partir do nome do método

    /**
     * Busca um usuário pelo seu endereço de e-mail.
     */
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    /**
     * Busca um usuário pelo seu ID único do Google.
     */
    Optional<User> findByGoogleId(String googleId);

    // --- Métodos relacionados a Roles ---

    /**
     * Busca usuários que possuem uma role específica.
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleType")
    List<User> findByRoleType(@Param("roleType") RoleType roleType);

    /**
     * Busca usuários que são administradores.
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = 'ADMIN'")
    List<User> findAdminUsers();

    /**
     * Busca usuários que são usuários comuns.
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = 'USER'")
    List<User> findRegularUsers();

    /**
     * Verifica se um usuário possui uma role específica.
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.roles r WHERE u.id = :userId AND r.name = :roleType")
    boolean userHasRole(@Param("userId") UUID userId, @Param("roleType") RoleType roleType);

}

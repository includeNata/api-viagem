package com.api.apiviagem.repository;

import com.api.apiviagem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}

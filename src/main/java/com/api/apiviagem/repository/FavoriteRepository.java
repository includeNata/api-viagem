package com.api.apiviagem.repository;

import com.api.apiviagem.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    /**
     * Busca todos os favoritos de um usuário específico.
     */
    List<Favorite> findByUserId(UUID userId);

    /**
     * Verifica se um favorito pertence a um usuário específico.
     */
    boolean existsByIdAndUserId(UUID favoriteId, UUID userId);

    /**
     * Busca um favorito por ID e ID do usuário.
     */
    Favorite findByIdAndUserId(UUID favoriteId, UUID userId);
}

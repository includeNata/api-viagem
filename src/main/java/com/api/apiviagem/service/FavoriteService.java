package com.api.apiviagem.service;

import com.api.apiviagem.DTO.request.FavoriteRequestDTO;
import com.api.apiviagem.DTO.response.FavoriteResponseDTO;
import com.api.apiviagem.exception.ResourceNotFoundException;
import com.api.apiviagem.model.Favorite;
import com.api.apiviagem.model.User;
import com.api.apiviagem.repository.FavoriteRepository;
import com.api.apiviagem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Busca todos os favoritos de um usuário específico.
     */
    @Transactional(readOnly = true)
    public List<FavoriteResponseDTO> getFavoritesForUser(UUID userId) {
        // Verifica se o usuário existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + userId);
        }

        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(FavoriteResponseDTO::fromFavorite)
                .collect(Collectors.toList());
    }

    /**
     * Cria um novo favorito e o associa a um usuário.
     */
    @Transactional
    public FavoriteResponseDTO addFavoriteForUser(UUID userId, FavoriteRequestDTO requestDTO) {
        // Busca o usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

        // Cria o novo favorito
        Favorite favorite = new Favorite();
        favorite.setName(requestDTO.name());
        favorite.setDescription(requestDTO.description());
        favorite.setImage(requestDTO.image());
        favorite.setHref(requestDTO.href());
        favorite.setUser(user);

        // Salva o favorito
        Favorite savedFavorite = favoriteRepository.save(favorite);

        return FavoriteResponseDTO.fromFavorite(savedFavorite);
    }

    /**
     * Deleta um favorito. A lógica garante que um usuário só possa deletar os seus próprios favoritos.
     */
    @Transactional
    public void deleteFavorite(UUID userId, UUID favoriteId) {
        // Verifica se o favorito existe e pertence ao usuário
        if (!favoriteRepository.existsByIdAndUserId(favoriteId, userId)) {
            throw new ResourceNotFoundException(
                    "Favorito não encontrado com ID: " + favoriteId + " para o usuário: " + userId
            );
        }

        // Deleta o favorito
        favoriteRepository.deleteById(favoriteId);
    }

    /**
     * Busca um favorito específico de um usuário.
     */
    @Transactional(readOnly = true)
    public FavoriteResponseDTO getFavoriteByIdAndUser(UUID userId, UUID favoriteId) {
        Favorite favorite = favoriteRepository.findByIdAndUserId(favoriteId, userId);
        
        if (favorite == null) {
            throw new ResourceNotFoundException(
                    "Favorito não encontrado com ID: " + favoriteId + " para o usuário: " + userId
            );
        }

        return FavoriteResponseDTO.fromFavorite(favorite);
    }

    /**
     * Atualiza um favorito existente.
     */
    @Transactional
    public FavoriteResponseDTO updateFavorite(UUID userId, UUID favoriteId, FavoriteRequestDTO requestDTO) {
        Favorite favorite = favoriteRepository.findByIdAndUserId(favoriteId, userId);
        
        if (favorite == null) {
            throw new ResourceNotFoundException(
                    "Favorito não encontrado com ID: " + favoriteId + " para o usuário: " + userId
            );
        }

        // Atualiza os campos
        favorite.setName(requestDTO.name());
        favorite.setDescription(requestDTO.description());
        favorite.setImage(requestDTO.image());
        favorite.setHref(requestDTO.href());

        // Salva as alterações
        Favorite updatedFavorite = favoriteRepository.save(favorite);

        return FavoriteResponseDTO.fromFavorite(updatedFavorite);
    }

    /**
     * Verifica se um favorito pertence a um usuário.
     */
    @Transactional(readOnly = true)
    public boolean isFavoriteOwnedByUser(UUID userId, UUID favoriteId) {
        return favoriteRepository.existsByIdAndUserId(favoriteId, userId);
    }
}

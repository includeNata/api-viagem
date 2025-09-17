package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.request.FavoriteRequestDTO;
import com.api.apiviagem.DTO.response.FavoriteResponseDTO;
import com.api.apiviagem.exception.ResourceNotFoundException;
import com.api.apiviagem.service.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * Retorna uma lista de favoritos para o usuário especificado.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteResponseDTO>> getFavoritesForUser(@PathVariable UUID userId) {
        try {
            List<FavoriteResponseDTO> favorites = favoriteService.getFavoritesForUser(userId);
            return ResponseEntity.ok(favorites);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adiciona um novo favorito para o usuário.
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<FavoriteResponseDTO> addFavoriteForUser(
            @PathVariable UUID userId,
            @Valid @RequestBody FavoriteRequestDTO requestDTO) {
        try {
            FavoriteResponseDTO createdFavorite = favoriteService.addFavoriteForUser(userId, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFavorite);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca um favorito específico de um usuário.
     */
    @GetMapping("/{favoriteId}/user/{userId}")
    public ResponseEntity<FavoriteResponseDTO> getFavoriteByIdAndUser(
            @PathVariable UUID favoriteId,
            @PathVariable UUID userId) {
        try {
            FavoriteResponseDTO favorite = favoriteService.getFavoriteByIdAndUser(userId, favoriteId);
            return ResponseEntity.ok(favorite);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza um favorito existente.
     */
    @PutMapping("/{favoriteId}/user/{userId}")
    public ResponseEntity<FavoriteResponseDTO> updateFavorite(
            @PathVariable UUID favoriteId,
            @PathVariable UUID userId,
            @Valid @RequestBody FavoriteRequestDTO requestDTO) {
        try {
            FavoriteResponseDTO updatedFavorite = favoriteService.updateFavorite(userId, favoriteId, requestDTO);
            return ResponseEntity.ok(updatedFavorite);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deleta um favorito.
     */
    @DeleteMapping("/{favoriteId}/user/{userId}")
    public ResponseEntity<Void> deleteFavorite(
            @PathVariable UUID favoriteId,
            @PathVariable UUID userId) {
        try {
            favoriteService.deleteFavorite(userId, favoriteId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verifica se um favorito pertence a um usuário.
     */
    @GetMapping("/{favoriteId}/user/{userId}/owns")
    public ResponseEntity<Boolean> isFavoriteOwnedByUser(
            @PathVariable UUID favoriteId,
            @PathVariable UUID userId) {
        try {
            boolean owns = favoriteService.isFavoriteOwnedByUser(userId, favoriteId);
            return ResponseEntity.ok(owns);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

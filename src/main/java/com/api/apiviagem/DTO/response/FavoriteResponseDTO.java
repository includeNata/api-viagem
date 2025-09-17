package com.api.apiviagem.DTO.response;

import com.api.apiviagem.model.Favorite;

import java.time.Instant;
import java.util.UUID;

public record FavoriteResponseDTO(
        UUID id,
        String name,
        String image,
        String href,
        Instant createdAt,
        Instant updatedAt
) {
    public static FavoriteResponseDTO fromFavorite(Favorite favorite) {
        return new FavoriteResponseDTO(
                favorite.getId(),
                favorite.getName(),
                favorite.getImage(),
                favorite.getHref(),
                favorite.getCreatedAt(),
                favorite.getUpdatedAt()
        );
    }
}

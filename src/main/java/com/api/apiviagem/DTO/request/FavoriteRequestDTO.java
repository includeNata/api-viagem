package com.api.apiviagem.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FavoriteRequestDTO(
        @NotBlank(message = "Nome do favorito é obrigatório")
        String name,

        String description,
        
        String image,
        
        @NotBlank(message = "Link (href) é obrigatório")
        String href
) {
}

package com.api.apiviagem.DTO.request;

import com.api.apiviagem.model.RoleType;
import jakarta.validation.constraints.NotNull;

public record RoleRequestDTO(
        @NotNull(message = "Tipo da role é obrigatório")
        RoleType name,
        
        String description
) {
}

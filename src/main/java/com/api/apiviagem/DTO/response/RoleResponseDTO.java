package com.api.apiviagem.DTO.response;

import com.api.apiviagem.model.Role;
import com.api.apiviagem.model.RoleType;

import java.time.Instant;
import java.util.UUID;

public record RoleResponseDTO(
        UUID id,
        RoleType name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    public static RoleResponseDTO fromRole(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}

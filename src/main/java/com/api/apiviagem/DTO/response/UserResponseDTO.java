package com.api.apiviagem.DTO.response;

import com.api.apiviagem.model.Role;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(UUID id, String name, String email, String imageUrl, Set<Role> role, Instant createdAt, Instant updatedAt) {
}

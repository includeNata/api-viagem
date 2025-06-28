package com.api.apiviagem.DTO.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message,
        Integer status,
        LocalDateTime timestamp,
        String path
) {
}

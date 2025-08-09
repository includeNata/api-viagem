package com.api.apiviagem.DTO.response;

import com.api.apiviagem.model.Destiny;

import java.util.List;

public record TravelStyleResponseDTO(
        String slug,
        String styleName,
        String titlePage,
        String descriptionPage,
        List<Destiny> destinations

) {
}

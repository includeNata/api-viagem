package com.api.apiviagem.DTO.request;


import java.util.List;

public record CityRequestDTO(
        String city,
        List<String> touristSpots
) {
}

package com.api.apiviagem.DTO;


import java.util.List;

public record CityRequestDTO(
        String city,
        List<String> touristSpots
) {
}

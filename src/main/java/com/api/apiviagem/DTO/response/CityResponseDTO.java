package com.api.apiviagem.DTO.response;

import com.api.apiviagem.model.Image;

import java.util.List;

public record CityResponseDTO(
         String culture,
         String economy,
         String transportation,
         String sports,
         String parksAndRecreation,
         String state,
         String population,
         String longitude,
         String latitude,
         String description,
         List<String> standout

) {
}

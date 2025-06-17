package com.api.apiviagem.DTO;

import com.api.apiviagem.model.Image;

import java.util.List;

public record CityResponseDTO(
         String culture,
         String economy,
         String transportation,
         String sports,
         String parksAndRecreation,
         List<Image> images

) {
}

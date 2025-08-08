package com.api.apiviagem.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LowBudgetRequestDTO(
        @NotBlank String destination,
        @NotNull Integer durationInDays,
        @NotNull  Integer numberOfTravelers
) {
}

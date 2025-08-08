package com.api.apiviagem.DTO.response;

public record LowBudgetResponseDTO(
        Double hosting,
        Double meals,
        Double localTransport,
        Double attractions,
        Double totalValue,
        String coinLocal
) {
}

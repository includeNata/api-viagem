package com.api.apiviagem.DTO.response;

import java.time.Instant;

public record ApiErrorResponse(int status, String error, String message, Instant timestamp) {}
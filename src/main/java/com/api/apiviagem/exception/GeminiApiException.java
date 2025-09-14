package com.api.apiviagem.exception;

public class GeminiApiException extends RuntimeException {
    
    public GeminiApiException(String message) {
        super(message);
    }
    
    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

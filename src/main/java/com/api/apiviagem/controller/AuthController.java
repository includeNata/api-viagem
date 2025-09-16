package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.request.GoogleTokenRequest;
import com.api.apiviagem.DTO.response.ErrorResponse;
import com.api.apiviagem.model.User;
import com.api.apiviagem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestBody GoogleTokenRequest request) {
        String accessToken = request.accessToken();

        // A validação pode ser melhorada com Bean Validation (@Valid) no DTO
        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // BAD_REQUEST é mais apropriado para input inválido
                    .body(new ErrorResponse("O access token é obrigatório."));
        }

        // Delega a lógica para o serviço, que já retorna uma ResponseEntity
        return authService.loginOrRegisterWithGoogle(accessToken);
    }
}

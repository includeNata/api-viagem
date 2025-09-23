package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.request.GoogleTokenRequest;
import com.api.apiviagem.DTO.response.ErrorResponse;
import com.api.apiviagem.DTO.response.ResponseDTO;
import com.api.apiviagem.model.User;
import com.api.apiviagem.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nenhum usuário autenticado.");
        }
        String userEmail = authentication.getName();
        return authService.getUserDataByEmail(userEmail);
    }

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

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout() {
        return authService.logout();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faltando cookie de autenticação");
        }

        String cookieToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("destinify-refresh-token")) {
                cookieToken = cookie.getValue();
                break;
            }
        }

        if (cookieToken == null || cookieToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faltando token de sessão");
        }

        return authService.refreshToken(cookieToken);
    }
}

package com.api.apiviagem.controller;

import com.api.apiviagem.model.User;
import com.api.apiviagem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<User>> GetAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(authService.getUsers());
    }

    @GetMapping("/google/callback")
    public ResponseEntity<String> handleGoogleCallback(@RequestParam("access_token") String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // Use HttpStatus para clareza
                    .body("{\"error\": \"Access token ausente.\"}");
        }

        return authService.getUserInfo(accessToken);
    }
}

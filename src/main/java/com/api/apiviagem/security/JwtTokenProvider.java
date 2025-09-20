package com.api.apiviagem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.access-token.secret}")
    private String jwtAccessSecret;
    @Value("${jwt.access-token.duration}")
    private long jwtAccessExpirationDate;

    @Value("${jwt.refresh-token.secret}")
    private String jwtRefreshSecret;
    @Value("${jwt.refresh-token.duration}")
    private long jwtRefreshExpirationDate;

    // --- GERAÇÃO DE TOKENS ---
    public String generateAccessToken(Authentication authentication) {
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtAccessExpirationDate);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(accessKey())
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtRefreshExpirationDate);

        return Jwts.builder()
                .subject(username)
                // O Refresh Token não precisa das roles. Mantê-lo simples é mais seguro.
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(refreshKey())
                .compact();
    }

    // --- VALIDAÇÃO DE TOKENS ---
    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey());
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey());
    }

    // --- EXTRAÇÃO DE DADOS DOS TOKENS ---
    public String getUsernameFromAccessToken(String token) {
        return getUsernameFromToken(token, accessKey());
    }

    public String getUsernameFromRefreshToken(String token) {
        return getUsernameFromToken(token, refreshKey());
    }

    // --- MÉTODOS PRIVADOS GENÉRICOS ---
    private boolean validateToken(String token, Key key) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Validação de JWT falhou: {}", e.getMessage());
            return false;
        }
    }

    private String getUsernameFromToken(String token, Key key) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private Key accessKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    private Key refreshKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }
}
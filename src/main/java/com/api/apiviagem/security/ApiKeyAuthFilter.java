package com.api.apiviagem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${internal.api.key}")
    private String internalApiKey;

    private static final String API_KEY_HEADER = "X-API-Key";

    // Lista de rotas que são públicas do ponto de vista do servidor-servidor
    // e não precisam da X-API-Key.
    private final List<String> publicPaths = List.of(
            "/api/v1/auth/google/callback",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/api/v1/holiday",
            "/api/v1/city",
            "/api/v1/low-budget"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Verifica se a rota da requisição está na nossa lista de exceções.
        boolean isPublicPath = publicPaths.stream().anyMatch(path::startsWith);

        if (isPublicPath) {
            // Se for uma rota pública, ignora a verificação da API Key e passa para o próximo filtro.
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Se não for uma rota pública, aplica a lógica de verificação da API Key.
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (internalApiKey.equals(apiKey)) {
            // Chave válida, a requisição pode continuar.
            filterChain.doFilter(request, response);
        } else {
            // Chave inválida ou ausente, bloqueia a requisição.
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("API Key inválida ou ausente.");
            // Não chama filterChain.doFilter, interrompendo o fluxo.
        }
    }
}
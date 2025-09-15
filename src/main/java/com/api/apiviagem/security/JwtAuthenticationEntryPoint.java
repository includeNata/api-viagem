package com.api.apiviagem.security;

import com.api.apiviagem.DTO.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // Injeção de dependência do ObjectMapper
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // Headers CORS foram removidos. O filtro principal já lida com isso.

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Não Autorizado",
                "Autenticação necessária para acessar este recurso.", // Mensagem genérica e segura
                Instant.now()
        );

        // Escreve a resposta JSON de forma segura
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

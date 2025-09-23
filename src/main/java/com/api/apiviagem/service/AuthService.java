package com.api.apiviagem.service;

import com.api.apiviagem.DTO.response.RefreshResponse;
import com.api.apiviagem.DTO.response.ResponseDTO;
import com.api.apiviagem.DTO.response.SignInResponse;
import com.api.apiviagem.DTO.response.UserResponseDTO;
import com.api.apiviagem.exception.ResourceNotFoundException;
import com.api.apiviagem.model.AuthProvider;
import com.api.apiviagem.model.GetInfosGoogle;
import com.api.apiviagem.model.User;
import com.api.apiviagem.repository.UserRepository;
import com.api.apiviagem.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseCookie; // Import necessário

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    // Em AuthService.java
    public ResponseEntity<?> getUserDataByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        UserResponseDTO userResponseDTO = new UserResponseDTO(user.getId(), user.getName(),
                user.getEmail(), user.getImageUrl(),
                user.getRoles(), user.getCreatedAt(),
                user.getUpdatedAt());

        return ResponseEntity.ok(userResponseDTO);
    }

    public ResponseEntity<?> loginOrRegisterWithGoogle(String googleAccessToken) {
        try {
            // 1. Obter informações do usuário do Google
            GetInfosGoogle googleUserInfo = getUserInfoFromGoogle(googleAccessToken);

            // 2. Encontrar ou criar o usuário no nosso banco de dados
            User user = findOrCreateUser(googleUserInfo);

            // 3. Criar a autenticação manualmente (identidade já verificada pelo Google)
            // Aqui, usamos o email como principal e null para credenciais, pois não há senha.
            // As roles/authorities devem ser carregadas do nosso banco.
            Authentication authentication = createAuthentication(user);

            // 4. Gerar nosso JWT interno para o usuário
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);


            // 5. Criar os cookies de forma mais simples e direta
            ResponseCookie refreshTokenCookie = ResponseCookie.from("destinify-refresh-token", refreshToken)
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 dias
                    .sameSite(cookieSecure ? "None" : "Lax")
                    .build();

            ResponseCookie accessTokenCookie = generateAccessToken(accessToken);

            UserResponseDTO userResponseDTO = new UserResponseDTO(user.getId(), user.getName(),
                    user.getEmail(), user.getImageUrl(),
                    user.getRoles(), user.getCreatedAt(),
                    user.getUpdatedAt());

            // CORREÇÃO: Usar abordagem com lista de cookies
            List<String> cookies = List.of(
                refreshTokenCookie.toString(),
                accessTokenCookie.toString()
            );
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.put(HttpHeaders.SET_COOKIE, cookies);
            
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(userResponseDTO);

        } catch (HttpClientErrorException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token do Google inválido ou expirado.");
        } catch (Exception e) {
            // Logar o erro real aqui é importante para depuração
            // logger.error("Erro inesperado durante o login com Google: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro no servidor.");
        }
    }

    // Em AuthService.java
    public ResponseEntity<ResponseDTO> logout() {
        // Cria o cookie para limpar o refresh token
        ResponseCookie cookieRefresh = ResponseCookie.from("destinify-refresh-token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0) // Expira imediatamente
                .sameSite(cookieSecure ? "None" : "Lax")
                .build();

        // Cria o cookie para limpar o access token
        ResponseCookie cookieAccess = ResponseCookie.from("destinify-access-token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0) // Expira imediatamente
                .sameSite(cookieSecure ? "None" : "Lax")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieRefresh.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieAccess.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseDTO("Logout realizado com sucesso!"));
    }

    public ResponseEntity<RefreshResponse> refreshToken(String refreshToken) {
        try {
            String userName = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);
            User user = userRepository.findByEmail(userName).orElseThrow(() -> new ResourceNotFoundException(""));

            Authentication authentication = createAuthentication(user);
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            ResponseCookie accessTokenCookie = generateAccessToken(accessToken);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .body(new RefreshResponse(accessToken));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseCookie generateAccessToken(String accessToken){
        return ResponseCookie.from("destinify-access-token", accessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(15 * 60) // 15 minutos
                .sameSite(cookieSecure ? "None" : "Lax")
                .build();
    }

    private Authentication createAuthentication(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString())) // Mapeia cada role
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                authorities // Ou as roles reais do usuário
        );

        // Define o principal autenticado no contexto de segurança do Spring
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    private GetInfosGoogle getUserInfoFromGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GetInfosGoogle> response = restTemplate.exchange(
                GOOGLE_USERINFO_URL,
                HttpMethod.GET,
                entity,
                GetInfosGoogle.class
        );

        if (response.getBody() == null) {
            throw new IllegalStateException("Não foi possível obter informações do usuário do Google.");
        }
        return response.getBody();
    }

    private User findOrCreateUser(GetInfosGoogle googleInfo) {
        // Busca o usuário pelo e-mail de forma eficiente
        return userRepository.findByEmail(googleInfo.getEmail())
                .orElseGet(() -> {
                    // Se não encontrar, cria um novo
                    User newUser = new User();
                    newUser.setGoogleId(googleInfo.getSub());
                    newUser.setName(googleInfo.getName());
                    newUser.setEmail(googleInfo.getEmail());
                    newUser.setImageUrl(googleInfo.getPicture());
                    newUser.setAuthProvider(AuthProvider.GOOGLE);

                    User savedUser = userRepository.save(newUser);

                    // Atribui a role padrão ao novo usuário
                    userService.assignDefaultRole(savedUser);
                    return savedUser;
                });
    }
}
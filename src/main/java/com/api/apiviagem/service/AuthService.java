package com.api.apiviagem.service;

import com.api.apiviagem.DTO.response.UserResponseDTO;
import com.api.apiviagem.model.AuthProvider;
import com.api.apiviagem.model.GetInfosGoogle;
import com.api.apiviagem.model.User;
import com.api.apiviagem.repository.UserRepository;
import com.api.apiviagem.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Removido o AuthenticationManager, ele não é usado aqui.

    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    public ResponseEntity<?> loginOrRegisterWithGoogle(String googleAccessToken) {
        try {
            // 1. Obter informações do usuário do Google
            GetInfosGoogle googleUserInfo = getUserInfoFromGoogle(googleAccessToken);

            // 2. Encontrar ou criar o usuário no nosso banco de dados
            User user = findOrCreateUser(googleUserInfo);

            // 3. Criar a autenticação manualmente (identidade já verificada pelo Google)
            // Aqui, usamos o email como principal e null para credenciais, pois não há senha.
            // As roles/authorities devem ser carregadas do nosso banco.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRoles().toString())) // Ou as roles reais do usuário
            );

            // Define o principal autenticado no contexto de segurança do Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. Gerar nosso JWT interno para o usuário
            String localJwt = jwtTokenProvider.generateToken(authentication);

            // 5. Criar o cookie e retorná-lo na resposta
            ResponseCookie cookie = ResponseCookie.from("jwtToken", localJwt)
                    .httpOnly(true)
                    .secure(true) // Use 'false' apenas em ambiente de desenvolvimento com HTTP
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 dias
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new UserResponseDTO(user.getId(), user.getName(),
                            user.getEmail(), user.getImageUrl(),
                            user.getRoles(), user.getCreatedAt(),
                            user.getUpdatedAt()));

        } catch (HttpClientErrorException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token do Google inválido ou expirado.");
        } catch (Exception e) {
            // Logar o erro real aqui é importante para depuração
            // logger.error("Erro inesperado durante o login com Google: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro no servidor.");
        }
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
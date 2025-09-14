package com.api.apiviagem.service;

import com.api.apiviagem.model.AuthProvider;
import com.api.apiviagem.model.GetInfosGoogle;
import com.api.apiviagem.model.RoleType;
import com.api.apiviagem.model.User;
import com.api.apiviagem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<String> getUserInfo(String accessToken) {
        try{
            RestTemplate restTemplate = new RestTemplate();

            // Montando o Header com o Bearer Token
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken); // equivale a "Authorization: Bearer <token>"

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<GetInfosGoogle> response = restTemplate.exchange(
                    GOOGLE_USERINFO_URL,
                    HttpMethod.GET,
                    entity,
                    GetInfosGoogle.class // 👈 mapeia direto para objeto
            );

            GetInfosGoogle infos = response.getBody();
            if (infos == null) {
                return ResponseEntity.badRequest().body("Não foi possivel fazer login");
            }

            User user = userRepository.existsByEmail(infos.getEmail())
                    ? userRepository.findAll()
                    .stream()
                    .filter(u -> u.getEmail().equals(infos.getEmail()))
                    .findFirst()
                    .orElse(new User())
                    : new User();

            // Mapeia os dados do Google para a entidade User
            user.setGoogleId(infos.getSub());
            user.setName(infos.getName());
            user.setEmail(infos.getEmail());
            user.setImageUrl(infos.getPicture());
            user.setAuthProvider(AuthProvider.GOOGLE);
            
            // Salva o usuário no banco
            userRepository.save(user);
            
            // Atribui role padrão USER se o usuário não tiver nenhuma role
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                userRoleService.assignDefaultRole(user);
            }
            
            return ResponseEntity.ok().body("Login Feito com Sucesso");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Não foi possivel fazer login");
        }

    }
}

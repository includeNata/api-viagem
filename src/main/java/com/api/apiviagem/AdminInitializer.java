package com.api.apiviagem;

import com.api.apiviagem.model.AuthProvider;
import com.api.apiviagem.model.RoleType;
import com.api.apiviagem.model.User;
import com.api.apiviagem.model.Role; // Supondo que você tenha uma entidade Role
import com.api.apiviagem.repository.UserRepository;
import com.api.apiviagem.repository.RoleRepository; // Supondo que você tenha um repositório para Roles
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Profile("dev") // 👈 Importante! Só executa quando o perfil 'dev' está ativo.
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository; // Necessário para buscar a role de ADM

    // Injeta os dados do admin a partir do application-dev.properties
    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.name}")
    private String adminName;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Verificando se o usuário administrador padrão existe...");

        // A lógica que você sugeriu: verificar se o admin já existe
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            logger.info("Usuário administrador já existe. Nenhuma ação necessária.");
            return;
        }

        logger.info("Usuário administrador não encontrado. Criando novo usuário admin...");
        RoleType roleType = RoleType.ADMIN;

        // Busca a role de admin ou cria se não existir (opcional, mas robusto)
        Role adminRole = roleRepository.findByName(roleType)
                .orElseGet(() -> roleRepository.save(new Role(roleType, roleType.getDescription())));

        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        // NUNCA armazene a senha em texto plano. Sempre use o encoder.
        adminUser.setName(adminName);
        adminUser.setRoles(Set.of(adminRole));
        adminUser.setAuthProvider(AuthProvider.LOCAL);

        userRepository.save(adminUser);

        logger.info("Usuário administrador criado com sucesso. Email: {}", adminEmail);
    }
}
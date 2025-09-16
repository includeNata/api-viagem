package com.api.apiviagem.service;

import com.api.apiviagem.exception.ResourceNotFoundException;
import com.api.apiviagem.model.Role;
import com.api.apiviagem.model.RoleType;
import com.api.apiviagem.model.User;
import com.api.apiviagem.repository.RoleRepository;
import com.api.apiviagem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Atribui uma role a um usuário.
     */
    public User assignRoleToUser(UUID userId, RoleType roleType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada: " + roleType));

        user.addRole(role);
        return userRepository.save(user);
    }

    /**
     * Remove uma role de um usuário.
     */
    public User removeRoleFromUser(UUID userId, RoleType roleType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada: " + roleType));

        user.removeRole(role);
        return userRepository.save(user);
    }

    /**
     * Busca usuários por tipo de role.
     */
    public List<User> getUsersByRole(RoleType roleType) {
        return userRepository.findByRoleType(roleType);
    }

    /**
     * Busca todos os usuários.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Busca todos os usuários administradores.
     */
    public List<User> getAdminUsers() {
        return userRepository.findAdminUsers();
    }

    /**
     * Busca todos os usuários regulares.
     */
    public List<User> getRegularUsers() {
        return userRepository.findRegularUsers();
    }

    /**
     * Verifica se um usuário possui uma role específica.
     */
    public boolean userHasRole(UUID userId, RoleType roleType) {
        return userRepository.userHasRole(userId, roleType);
    }

    /**
     * Define as roles de um usuário (substitui todas as roles existentes).
     */
    public User setUserRoles(UUID userId, Set<RoleType> roleTypes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

        // Limpa as roles existentes
        user.setRoles(new java.util.HashSet<>());

        // Adiciona as novas roles
        for (RoleType roleType : roleTypes) {
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada: " + roleType));
            user.addRole(role);
        }

        return userRepository.save(user);
    }

    /**
     * Atribui a role padrão USER para novos usuários.
     */
    public User assignDefaultRole(User user) {
        Role defaultRole = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role USER não encontrada"));

        user.addRole(defaultRole);
        return userRepository.save(user);
    }

    /**
     * Busca um usuário pelo ID.
     */
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));
    }
}

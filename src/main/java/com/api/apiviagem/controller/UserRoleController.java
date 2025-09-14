package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.response.RoleResponseDTO;
import com.api.apiviagem.exception.ResourceNotFoundException;
import com.api.apiviagem.model.Role;
import com.api.apiviagem.model.RoleType;
import com.api.apiviagem.model.User;
import com.api.apiviagem.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    /**
     * Atribui uma role a um usuário.
     */
    @PostMapping("/{userId}/roles/{roleType}")
    public ResponseEntity<User> assignRoleToUser(
            @PathVariable UUID userId,
            @PathVariable RoleType roleType) {
        try {
            User user = userRoleService.assignRoleToUser(userId, roleType);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove uma role de um usuário.
     */
    @DeleteMapping("/{userId}/roles/{roleType}")
    public ResponseEntity<User> removeRoleFromUser(
            @PathVariable UUID userId,
            @PathVariable RoleType roleType) {
        try {
            User user = userRoleService.removeRoleFromUser(userId, roleType);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca usuários por tipo de role.
     */
    @GetMapping("/by-role/{roleType}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable RoleType roleType) {
        List<User> users = userRoleService.getUsersByRole(roleType);
        return ResponseEntity.ok(users);
    }

    /**
     * Busca todos os usuários administradores.
     */
    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAdminUsers() {
        List<User> adminUsers = userRoleService.getAdminUsers();
        return ResponseEntity.ok(adminUsers);
    }

    /**
     * Busca todos os usuários regulares.
     */
    @GetMapping("/regular-users")
    public ResponseEntity<List<User>> getRegularUsers() {
        List<User> regularUsers = userRoleService.getRegularUsers();
        return ResponseEntity.ok(regularUsers);
    }

    /**
     * Verifica se um usuário possui uma role específica.
     */
    @GetMapping("/{userId}/has-role/{roleType}")
    public ResponseEntity<Boolean> userHasRole(
            @PathVariable UUID userId,
            @PathVariable RoleType roleType) {
        boolean hasRole = userRoleService.userHasRole(userId, roleType);
        return ResponseEntity.ok(hasRole);
    }

    /**
     * Define as roles de um usuário (substitui todas as roles existentes).
     */
    @PutMapping("/{userId}/roles")
    public ResponseEntity<User> setUserRoles(
            @PathVariable UUID userId,
            @RequestBody Set<RoleType> roleTypes) {
        try {
            User user = userRoleService.setUserRoles(userId, roleTypes);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca as roles de um usuário específico.
     */
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleResponseDTO>> getUserRoles(@PathVariable UUID userId) {
        try {
            User user = userRoleService.getUserById(userId);
            List<RoleResponseDTO> roles = user.getRoles().stream()
                    .map(RoleResponseDTO::fromRole)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(roles);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

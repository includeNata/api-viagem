package com.api.apiviagem.service;

import com.api.apiviagem.exception.ResourceNotFoundException;
import com.api.apiviagem.model.Role;
import com.api.apiviagem.model.RoleType;
import com.api.apiviagem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Busca todas as roles cadastradas no sistema.
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Busca uma role pelo seu ID.
     */
    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada com ID: " + id));
    }

    /**
     * Busca uma role pelo seu tipo.
     */
    public Role getRoleByName(RoleType name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada com nome: " + name));
    }

    /**
     * Cadastra uma nova role no sistema.
     */
    public Role createRole(RoleType roleType, String description) {
        // Verifica se a role já existe
        if (roleRepository.existsByName(roleType)) {
            throw new IllegalArgumentException("Role já existe com o tipo: " + roleType);
        }

        Role role = new Role(roleType, description);
        return roleRepository.save(role);
    }

    /**
     * Atualiza uma role existente.
     */
    public Role updateRole(UUID id, String description) {
        Role role = getRoleById(id);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    /**
     * Deleta uma role pelo seu ID.
     */
    public void deleteRoleById(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role não encontrada com ID: " + id);
        }
        roleRepository.deleteById(id);
    }

    /**
     * Deleta uma role pelo seu tipo.
     */
    public void deleteRoleByName(RoleType name) {
        if (!roleRepository.existsByName(name)) {
            throw new ResourceNotFoundException("Role não encontrada com nome: " + name);
        }
        roleRepository.deleteByName(name);
    }

    /**
     * Verifica se uma role existe pelo seu tipo.
     */
    public boolean roleExists(RoleType name) {
        return roleRepository.existsByName(name);
    }
}

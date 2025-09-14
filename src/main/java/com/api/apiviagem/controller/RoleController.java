package com.api.apiviagem.controller;

import com.api.apiviagem.DTO.request.RoleRequestDTO;
import com.api.apiviagem.DTO.response.RoleResponseDTO;
import com.api.apiviagem.exception.ResourceNotFoundException;
import com.api.apiviagem.model.Role;
import com.api.apiviagem.model.RoleType;
import com.api.apiviagem.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Busca todas as roles cadastradas no sistema.
     */
    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleResponseDTO> responseDTOs = roles.stream()
                .map(RoleResponseDTO::fromRole)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Busca uma role pelo seu ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable UUID id) {
        try {
            Role role = roleService.getRoleById(id);
            RoleResponseDTO responseDTO = RoleResponseDTO.fromRole(role);
            return ResponseEntity.ok(responseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca uma role pelo seu tipo.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<RoleResponseDTO> getRoleByName(@PathVariable RoleType name) {
        try {
            Role role = roleService.getRoleByName(name);
            RoleResponseDTO responseDTO = RoleResponseDTO.fromRole(role);
            return ResponseEntity.ok(responseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cadastra uma nova role no sistema.
     */
    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO requestDTO) {
        try {
            Role role = roleService.createRole(requestDTO.name(), requestDTO.description());
            RoleResponseDTO responseDTO = RoleResponseDTO.fromRole(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atualiza uma role existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable UUID id, 
            @RequestBody RoleRequestDTO requestDTO) {
        try {
            Role role = roleService.updateRole(id, requestDTO.description());
            RoleResponseDTO responseDTO = RoleResponseDTO.fromRole(role);
            return ResponseEntity.ok(responseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deleta uma role pelo seu ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoleById(@PathVariable UUID id) {
        try {
            roleService.deleteRoleById(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deleta uma role pelo seu tipo.
     */
    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteRoleByName(@PathVariable RoleType name) {
        try {
            roleService.deleteRoleByName(name);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

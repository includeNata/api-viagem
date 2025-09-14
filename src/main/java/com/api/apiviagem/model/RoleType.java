package com.api.apiviagem.model;

/**
 * Enum que define os tipos de roles disponíveis no sistema.
 */
public enum RoleType {
    USER("Usuário comum do sistema"),
    ADMIN("Administrador com acesso total ao sistema");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

package com.api.apiviagem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.DataAmount;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@DataAmount // Anotação do Lombok: gera getters, setters, toString, equals, hashCode
@Entity // Marca esta classe como uma entidade JPA
@Table(name = "users") // Mapeia para a tabela "users" no banco
public class User {

    // --- Dados Essenciais (nunca serão nulos) ---

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Email // Validação para garantir que o formato do e-mail é válido
    @NotNull // Garante que o e-mail não pode ser nulo
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true) // 'name' pode ser nulo no cadastro via magic link
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // --- Dados do Provedor (podem ser nulos) ---

    @Column(name = "google_id", unique = true) // ID único do Google ('sub')
    private String googleId;

    @Column(name = "image_url")
    private String imageUrl; // URL da foto de perfil

    // --- Controle de Autenticação ---

    @NotNull
    @Enumerated(EnumType.STRING) // Salva o nome do Enum ('LOCAL', 'GOOGLE') como String no banco
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider;

    public User() {
    }

    public User(UUID id, String email, String name, Instant createdAt, Instant updatedAt, String googleId, String imageUrl, AuthProvider authProvider) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.googleId = googleId;
        this.imageUrl = imageUrl;
        this.authProvider = authProvider;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }
}

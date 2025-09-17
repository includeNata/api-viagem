# Documentação da API de Favoritos

## Visão Geral
Esta API permite aos usuários gerenciar seus favoritos genéricos. Cada favorito possui um nome, imagem opcional e um link (href). A relação é One-to-Many: um usuário pode ter muitos favoritos, e cada favorito pertence a um único usuário.

## Estrutura do Banco de Dados

### Tabela favorites
```sql
CREATE TABLE favorites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    image VARCHAR(500),
    href VARCHAR(500) NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### Relacionamento
- **User** ↔ **Favorite** (One-to-Many)
- Tabela `favorites` com chave estrangeira `user_id` referenciando `users(id)`

## Endpoints Disponíveis

### 1. Buscar Favoritos de um Usuário
```http
GET /api/v1/favorites/user/{userId}
```

**Parâmetros:**
- `userId` (UUID): ID único do usuário

**Resposta de Sucesso (200):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Google",
    "image": "https://www.google.com/favicon.ico",
    "href": "https://www.google.com",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  {
    "id": "123e4567-e89b-12d3-a456-426614174001",
    "name": "GitHub",
    "image": "https://github.com/favicon.ico",
    "href": "https://github.com",
    "createdAt": "2024-01-01T11:00:00Z",
    "updatedAt": "2024-01-01T11:00:00Z"
  }
]
```

### 2. Adicionar Favorito
```http
POST /api/v1/favorites/user/{userId}
```

**Body (JSON):**
```json
{
  "name": "Stack Overflow",
  "image": "https://stackoverflow.com/favicon.ico",
  "href": "https://stackoverflow.com"
}
```

**Resposta de Sucesso (201):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174002",
  "name": "Stack Overflow",
  "image": "https://stackoverflow.com/favicon.ico",
  "href": "https://stackoverflow.com",
  "createdAt": "2024-01-01T12:00:00Z",
  "updatedAt": "2024-01-01T12:00:00Z"
}
```

### 3. Buscar Favorito Específico
```http
GET /api/v1/favorites/{favoriteId}/user/{userId}
```

**Parâmetros:**
- `favoriteId` (UUID): ID único do favorito
- `userId` (UUID): ID único do usuário

### 4. Atualizar Favorito
```http
PUT /api/v1/favorites/{favoriteId}/user/{userId}
```

**Body (JSON):**
```json
{
  "name": "Stack Overflow - Atualizado",
  "image": "https://stackoverflow.com/favicon-new.ico",
  "href": "https://stackoverflow.com"
}
```

### 5. Deletar Favorito
```http
DELETE /api/v1/favorites/{favoriteId}/user/{userId}
```

**Resposta de Sucesso (204):**
```
Sem conteúdo
```

### 6. Verificar Propriedade do Favorito
```http
GET /api/v1/favorites/{favoriteId}/user/{userId}/owns
```

**Resposta:**
```json
true
```

## Validações

### FavoriteRequestDTO
- `name`: **Obrigatório** - Nome do favorito
- `image`: **Opcional** - URL da imagem
- `href`: **Obrigatório** - Link do favorito

### Regras de Negócio
1. **Segurança**: Um usuário só pode acessar, modificar ou deletar seus próprios favoritos
2. **Validação**: Todos os campos obrigatórios devem ser preenchidos
3. **Relacionamento**: Favoritos são automaticamente associados ao usuário correto
4. **Cascata**: Ao deletar um usuário, seus favoritos também são removidos

## Códigos de Status HTTP

- **200**: Sucesso
- **201**: Criado com sucesso
- **204**: Deletado com sucesso (sem conteúdo)
- **400**: Dados inválidos
- **404**: Usuário ou favorito não encontrado
- **500**: Erro interno do servidor

## Exemplos de Uso com cURL

### Adicionar Favorito:
```bash
curl -X POST http://localhost:8080/api/v1/favorites/user/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "YouTube",
    "image": "https://www.youtube.com/favicon.ico",
    "href": "https://www.youtube.com"
  }'
```

### Buscar Favoritos do Usuário:
```bash
curl -X GET http://localhost:8080/api/v1/favorites/user/123e4567-e89b-12d3-a456-426614174000
```

### Atualizar Favorito:
```bash
curl -X PUT http://localhost:8080/api/v1/favorites/123e4567-e89b-12d3-a456-426614174001/user/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "YouTube - Canal Principal",
    "image": "https://www.youtube.com/favicon-new.ico",
    "href": "https://www.youtube.com"
  }'
```

### Deletar Favorito:
```bash
curl -X DELETE http://localhost:8080/api/v1/favorites/123e4567-e89b-12d3-a456-426614174001/user/123e4567-e89b-12d3-a456-426614174000
```

## Arquitetura Implementada

### Camadas
1. **Model**: `Favorite.java` - Entidade JPA com relacionamento Many-to-One
2. **Repository**: `FavoriteRepository.java` - Interface JPA com métodos customizados
3. **Service**: `FavoriteService.java` - Lógica de negócio com transações
4. **Controller**: `FavoriteController.java` - Endpoints REST
5. **DTOs**: `FavoriteRequestDTO.java` e `FavoriteResponseDTO.java` - Transferência de dados

### Padrões Utilizados
- **Repository Pattern**: Para acesso a dados
- **Service Layer**: Para lógica de negócio
- **DTO Pattern**: Para transferência de dados
- **REST API**: Endpoints padronizados
- **Transaction Management**: Operações transacionais

### Relacionamentos JPA
- **@ManyToOne**: Favorite → User (LAZY loading)
- **@OneToMany**: User → Favorite (LAZY loading com cascade)
- **@JsonIgnore**: Evita loops de serialização
- **Orphan Removal**: Remove favoritos órfãos automaticamente

## Considerações de Performance

1. **Lazy Loading**: Relacionamentos configurados com LAZY para melhor performance
2. **Índices**: Recomenda-se criar índices na coluna `user_id` para consultas rápidas
3. **Paginação**: Para grandes volumes de favoritos, considere implementar paginação
4. **Cache**: Para consultas frequentes, considere implementar cache Redis

## Segurança

1. **Autorização**: Verificação de propriedade antes de operações
2. **Validação**: Bean Validation para dados de entrada
3. **Transações**: Operações atômicas para consistência
4. **Isolamento**: Usuários só acessam seus próprios dados

## Próximas Melhorias

- [ ] Implementar paginação para listagem de favoritos
- [ ] Adicionar busca/filtro por nome ou href
- [ ] Implementar cache Redis para consultas frequentes
- [ ] Adicionar categorias/tags para favoritos
- [ ] Implementar ordenação personalizada
- [ ] Adicionar logs de auditoria
- [ ] Implementar rate limiting

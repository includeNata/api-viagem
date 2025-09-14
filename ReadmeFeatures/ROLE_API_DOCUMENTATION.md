# Documentação da API de Roles

## Visão Geral
Esta API permite gerenciar roles no sistema, incluindo as roles de **USER** e **ADMIN**.

## Endpoints Disponíveis

### 1. Buscar Todas as Roles
```http
GET /api/roles
```

**Resposta de Sucesso (200):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "USER",
    "description": "Usuário comum do sistema",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  {
    "id": "123e4567-e89b-12d3-a456-426614174001",
    "name": "ADMIN",
    "description": "Administrador com acesso total ao sistema",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  }
]
```

### 2. Buscar Role por ID
```http
GET /api/roles/{id}
```

**Parâmetros:**
- `id` (UUID): ID único da role

**Resposta de Sucesso (200):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "USER",
  "description": "Usuário comum do sistema",
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

**Resposta de Erro (404):**
```json
Role não encontrada
```

### 3. Buscar Role por Nome
```http
GET /api/roles/name/{name}
```

**Parâmetros:**
- `name` (RoleType): USER ou ADMIN

**Exemplo:**
```http
GET /api/roles/name/USER
```

### 4. Cadastrar Nova Role
```http
POST /api/roles
```

**Body (JSON):**
```json
{
  "name": "USER",
  "description": "Usuário comum do sistema"
}
```

**Resposta de Sucesso (201):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "USER",
  "description": "Usuário comum do sistema",
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

**Resposta de Erro (400):**
```json
Role já existe com o tipo: USER
```

### 5. Atualizar Role
```http
PUT /api/roles/{id}
```

**Body (JSON):**
```json
{
  "name": "USER",
  "description": "Descrição atualizada"
}
```

### 6. Deletar Role por ID
```http
DELETE /api/roles/{id}
```

**Resposta de Sucesso (204):**
```
Sem conteúdo
```

### 7. Deletar Role por Nome
```http
DELETE /api/roles/name/{name}
```

**Exemplo:**
```http
DELETE /api/roles/name/USER
```

## Tipos de Role Disponíveis

- **USER**: Usuário comum do sistema
- **ADMIN**: Administrador com acesso total ao sistema

## Códigos de Status HTTP

- **200**: Sucesso
- **201**: Criado com sucesso
- **204**: Deletado com sucesso (sem conteúdo)
- **400**: Requisição inválida (role já existe, dados inválidos)
- **404**: Role não encontrada

## Exemplo de Uso com cURL

### Cadastrar Role USER:
```bash
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "USER",
    "description": "Usuário comum do sistema"
  }'
```

### Buscar Todas as Roles:
```bash
curl -X GET http://localhost:8080/api/roles
```

### Deletar Role por Nome:
```bash
curl -X DELETE http://localhost:8080/api/roles/name/USER
```

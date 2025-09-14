# Relacionamento User-Role

## Visão Geral
Este documento descreve o relacionamento Many-to-Many entre as entidades User e Role, permitindo que usuários possuam múltiplas roles e vice-versa.

## Estrutura do Relacionamento

### Tabela de Relacionamento
```sql
CREATE TABLE user_roles (
    user_id UUID REFERENCES users(id),
    role_id UUID REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

### Entidades Modificadas

#### User.java
- Adicionado campo `Set<Role> roles`
- Relacionamento Many-to-Many com Role
- Métodos utilitários para gerenciar roles

#### Role.java
- Entidade independente com enum RoleType
- Relacionamento inverso com User

## Funcionalidades Implementadas

### UserRoleService
Serviço responsável por gerenciar o relacionamento entre usuários e roles:

- `assignRoleToUser()` - Atribui role a usuário
- `removeRoleFromUser()` - Remove role de usuário
- `getUsersByRole()` - Busca usuários por tipo de role
- `getAdminUsers()` - Lista usuários administradores
- `getRegularUsers()` - Lista usuários regulares
- `userHasRole()` - Verifica se usuário possui role
- `setUserRoles()` - Define todas as roles de um usuário
- `assignDefaultRole()` - Atribui role padrão USER

### UserRepository
Novos métodos adicionados:

- `findByRoleType()` - Busca usuários por tipo de role
- `findAdminUsers()` - Busca usuários administradores
- `findRegularUsers()` - Busca usuários regulares
- `userHasRole()` - Verifica se usuário possui role

## Endpoints da API User-Role

### 1. Atribuir Role a Usuário
```http
POST /api/users/{userId}/roles/{roleType}
```

**Exemplo:**
```bash
curl -X POST http://localhost:8080/api/users/123e4567-e89b-12d3-a456-426614174000/roles/ADMIN
```

### 2. Remover Role de Usuário
```http
DELETE /api/users/{userId}/roles/{roleType}
```

### 3. Buscar Usuários por Role
```http
GET /api/users/by-role/{roleType}
```

**Exemplo:**
```bash
curl -X GET http://localhost:8080/api/users/by-role/USER
```

### 4. Listar Usuários Administradores
```http
GET /api/users/admins
```

### 5. Listar Usuários Regulares
```http
GET /api/users/regular-users
```

### 6. Verificar se Usuário Possui Role
```http
GET /api/users/{userId}/has-role/{roleType}
```

**Resposta:**
```json
true
```

### 7. Definir Roles do Usuário (Substitui todas)
```http
PUT /api/users/{userId}/roles
```

**Body:**
```json
["USER", "ADMIN"]
```

### 8. Buscar Roles de um Usuário
```http
GET /api/users/{userId}/roles
```

**Resposta:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "USER",
    "description": "Usuário comum do sistema",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  }
]
```

## Integração com AuthService

### Atribuição Automática de Role Padrão
Quando um usuário faz login via Google pela primeira vez, automaticamente recebe a role `USER`:

```java
// No AuthService.getUserInfo()
if (user.getRoles() == null || user.getRoles().isEmpty()) {
    userRoleService.assignDefaultRole(user);
}
```

## Métodos Utilitários no User

### Verificar Role
```java
boolean isAdmin = user.hasRole(RoleType.ADMIN);
boolean isUser = user.hasRole(RoleType.USER);
```

### Adicionar/Remover Role
```java
user.addRole(role);
user.removeRole(role);
```

## Exemplos de Uso

### Promover Usuário a Administrador
```bash
# 1. Criar role ADMIN se não existir
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "ADMIN", "description": "Administrador"}'

# 2. Atribuir role ADMIN ao usuário
curl -X POST http://localhost:8080/api/users/{userId}/roles/ADMIN
```

### Verificar Permissões
```java
@Service
public class PermissionService {
    
    public boolean canAccessAdminPanel(UUID userId) {
        return userRoleService.userHasRole(userId, RoleType.ADMIN);
    }
    
    public boolean canAccessUserPanel(UUID userId) {
        return userRoleService.userHasRole(userId, RoleType.USER);
    }
}
```

## Códigos de Status HTTP

- **200**: Sucesso
- **404**: Usuário ou Role não encontrado
- **400**: Dados inválidos

## Considerações de Segurança

1. **Validação de Permissões**: Sempre verificar roles antes de permitir acesso a funcionalidades administrativas
2. **Role Padrão**: Novos usuários recebem automaticamente a role USER
3. **Auditoria**: Considerar implementar logs de alterações de roles
4. **Hierarquia**: Roles podem ser expandidas para incluir hierarquias (ex: SUPER_ADMIN)

## Próximos Passos

1. Implementar validação de permissões em controllers
2. Adicionar logs de auditoria para mudanças de roles
3. Criar middleware de autenticação/autorização
4. Implementar cache para consultas frequentes de roles

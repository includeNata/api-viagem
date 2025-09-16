# Documentação das Features

Esta pasta contém a documentação detalhada das funcionalidades implementadas no projeto API Viagem.

## 📁 Estrutura da Documentação

### 🔐 Sistema de Roles
- **[ROLE_API_DOCUMENTATION.md](./ROLE_API_DOCUMENTATION.md)** - Documentação completa da API de gerenciamento de roles
- **[USER_ROLE_RELATIONSHIP.md](./USER_ROLE_RELATIONSHIP.md)** - Documentação do relacionamento entre usuários e roles

## 🚀 Features Implementadas

### 1. Sistema de Roles
- ✅ Modelo Role com enum RoleType (USER, ADMIN)
- ✅ CRUD completo para roles
- ✅ Endpoints REST para gerenciamento
- ✅ Validações e tratamento de exceções

### 2. Relacionamento User-Role
- ✅ Relacionamento Many-to-Many entre User e Role
- ✅ Atribuição automática de role padrão (USER) para novos usuários
- ✅ Endpoints para gerenciar roles de usuários
- ✅ Métodos utilitários para verificação de permissões

## 📋 Como Usar

### Criando Roles
```bash
# Criar role USER
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "USER", "description": "Usuário comum"}'

# Criar role ADMIN
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "ADMIN", "description": "Administrador"}'
```

### Gerenciando Roles de Usuários
```bash
# Promover usuário a administrador
curl -X POST http://localhost:8080/api/users/{userId}/roles/ADMIN

# Verificar se usuário é admin
curl -X GET http://localhost:8080/api/users/{userId}/has-role/ADMIN

# Listar usuários administradores
curl -X GET http://localhost:8080/api/users/admins
```

## 🔧 Arquitetura

### Camadas Implementadas
- **Model**: Role, RoleType, relacionamento com User
- **Repository**: RoleRepository, métodos adicionais no UserRepository
- **Service**: RoleService, UserRoleService
- **Controller**: RoleController, UserRoleController
- **DTO**: RoleRequestDTO, RoleResponseDTO

### Padrões Utilizados
- **Repository Pattern**: Para acesso a dados
- **Service Layer**: Para lógica de negócio
- **DTO Pattern**: Para transferência de dados
- **REST API**: Endpoints padronizados

## 📊 Banco de Dados

### Tabelas Criadas
- `roles` - Armazena as roles do sistema
- `user_roles` - Tabela de relacionamento Many-to-Many

### Relacionamentos
- User ↔ Role (Many-to-Many)
- Tabela intermediária: user_roles

## 🔒 Segurança

### Implementações de Segurança
- Validação de dados com Bean Validation
- Tratamento de exceções personalizadas
- Verificação de permissões antes de operações
- Atribuição automática de role padrão

### Próximos Passos de Segurança
- [ ] Implementar middleware de autenticação
- [ ] Adicionar logs de auditoria
- [ ] Implementar cache para consultas de roles
- [ ] Criar sistema de hierarquia de roles

## 📈 Próximas Features

- [ ] Sistema de permissões granular
- [ ] Middleware de autorização
- [ ] Logs de auditoria para mudanças de roles
- [ ] Interface web para gerenciamento de roles
- [ ] Sistema de convites para roles administrativas

---

**Última atualização**: $(date)
**Versão**: 1.0.0

# 📝 Task Manager API

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)

> API REST para gerenciamento de tarefas, construída com **Java 21**, **Spring Boot 4.1.1** e **PostgreSQL**.

---

## ✨ Funcionalidades

- ✅ **CRUD completo** de tarefas (Criar, Listar, Buscar, Atualizar, Deletar)
- ✅ **Lançamentos financeiros** de receitas e despesas
- ✅ **Categorias financeiras** reutilizáveis
- ✅ **Resumo financeiro por período** com receitas, despesas e saldo
- ✅ **Cadastro e login** com senha protegida por BCrypt e token JWT
- ✅ **Isolamento de dados** por usuário autenticado
- ✅ **Valores monetários** com precisão decimal usando `BigDecimal`
- ✅ **Filtro de lançamentos** por tipo (`RECEITA` ou `DESPESA`)
- ✅ **Validação** de dados com Bean Validation
- ✅ **Tratamento de erros** profissional com respostas JSON
- ✅ **Filtro por status** (PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA)
- ✅ **Datas automáticas** de criação e atualização
- ✅ **Migrações Flyway** para controle de versão do banco
- ✅ **CORS** configurado para comunicação com frontend

---

## 🛠️ Tecnologias

### Backend
- **Java 21** — Linguagem principal
- **Spring Boot 4.1.1** — Framework web
- **Spring Data JPA** — Persistência de dados
- **Spring Validation** — Validação de dados
- **PostgreSQL 17+** — Banco de dados relacional
- **Flyway** — Migrações de banco de dados
- **Lombok** — Redução de boilerplate
- **Maven** — Gerenciamento de dependências

---

## 🚀 Como rodar localmente

### Pré-requisitos
- Java 21+
- Maven (opcional, o Maven Wrapper já está incluído)
- PostgreSQL 17+ (ou Docker)

### 1. Clone o repositório
```bash
git clone https://github.com/GustavoBenAbraham/task-manager-api.git
cd task-manager-api
```

### 2. Configure o banco de dados
Crie um banco chamado `taskmanager` no PostgreSQL:
```sql
CREATE DATABASE taskmanager;
```

Ou use Docker:
```bash
docker run --name taskmanager-db -e POSTGRES_DB=taskmanager -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=defina_uma_senha -p 5432:5432 -d postgres:17
```

### 3. Configure as credenciais
Edite `src/main/resources/application.properties` com as credenciais do seu PostgreSQL:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
```

No PowerShell, defina a senha antes de executar a aplicação:
```powershell
$env:DB_PASSWORD = "SUA_SENHA_DO_POSTGRES"
$env:JWT_SECRET = "SUA_CHAVE_BASE64_FORTE"
```

Não publique a senha real no GitHub. Para ambientes compartilhados ou de produção, use variáveis de ambiente ou um gerenciador de segredos.

### 4. Execute o projeto
No Windows:
```powershell
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:
```bash
./mvnw spring-boot:run
```

O servidor iniciará em `http://localhost:8080`

### Frontend

O dashboard React fica em `frontend/` e usa Vite.

Pré-requisito: Node.js 20+ e npm.

```bash
cd frontend
npm install
npm run dev
```

O frontend ficará disponível em `http://localhost:5173`.

### 5. Execute os testes
```bash
./mvnw test
```

No Windows PowerShell, use `./mvnw.cmd test`.

---

## 🔗 API Endpoints

### Documentação Interativa
A API possui documentação interativa com Swagger UI disponível em:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Tarefas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/tasks` | Criar nova tarefa |
| GET | `/api/v1/tasks` | Listar todas as tarefas |
| GET | `/api/v1/tasks/paginado` | Listar tarefas com paginação (page, size, sort) |
| GET | `/api/v1/tasks/{id}` | Buscar tarefa por ID |
| GET | `/api/v1/tasks/status/{status}` | Filtrar por status (`PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`) |
| GET | `/api/v1/tasks/status/{status}/paginado` | Filtrar por status com paginação |
| PUT | `/api/v1/tasks/{id}` | Atualizar tarefa |
| DELETE | `/api/v1/tasks/{id}` | Deletar tarefa |

### Lançamentos financeiros

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/lancamentos` | Criar receita ou despesa |
| GET | `/api/v1/lancamentos` | Listar lançamentos por data |
| GET | `/api/v1/lancamentos/{id}` | Buscar lançamento por ID |
| GET | `/api/v1/lancamentos/tipo/{tipo}` | Filtrar por tipo |
| PUT | `/api/v1/lancamentos/{id}` | Atualizar lançamento |
| DELETE | `/api/v1/lancamentos/{id}` | Deletar lançamento |

### Contas financeiras

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/contas` | Criar conta |
| GET | `/api/v1/contas` | Listar contas |
| GET | `/api/v1/contas/{id}` | Buscar conta por ID |
| PUT | `/api/v1/contas/{id}` | Atualizar conta |
| PATCH | `/api/v1/contas/{id}/desativar` | Desativar conta |

### Categorias financeiras

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/categorias` | Criar categoria |
| GET | `/api/v1/categorias` | Listar categorias |
| GET | `/api/v1/categorias/{id}` | Buscar categoria por ID |
| PUT | `/api/v1/categorias/{id}` | Atualizar categoria |
| PATCH | `/api/v1/categorias/{id}/desativar` | Desativar categoria |

### Dashboard financeiro

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/dashboard/resumo?inicio=2026-09-01&fim=2026-09-30` | Resumo do período |

Resposta:

```json
{
    "inicio": "2026-09-01",
    "fim": "2026-09-30",
    "totalReceitas": 3000.00,
    "totalDespesas": 1250.50,
    "saldo": 1749.50
}
```

### Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/auth/register` | Criar usuário |
| POST | `/api/v1/auth/login` | Autenticar e obter token JWT |

Os demais endpoints exigem o token retornado no login:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

Defina `JWT_SECRET` com uma chave Base64 forte antes de iniciar a aplicação.

Exemplo de lançamento:

```json
{
    "descricao": "Supermercado",
    "valor": 250.75,
    "tipo": "DESPESA",
    "data": "2026-09-20",
    "categoria": "Alimentação",
    "categoriaId": 1,
    "contaId": 1,
    "observacao": "Compras do mês"
}
```

---

## 📋 Exemplos de uso

### Criar tarefa (POST)
```json
{
    "titulo": "Estudar Spring Boot",
    "descricao": "Revisar conceitos de JPA e DTOs",
    "status": "PENDENTE"
}
```

### Resposta
```json
{
    "id": 1,
    "titulo": "Estudar Spring Boot",
    "descricao": "Revisar conceitos de JPA e DTOs",
    "status": "PENDENTE",
    "dataCriacao": "2026-08-26T12:00:00",
    "dataAtualizacao": "2026-08-26T12:00:00"
}
```

---

## 🏗️ Arquitetura

```
src/main/java/com/gustavo/taskmanager/
├── config/         # Configurações (CORS)
├── controller/     # Endpoints REST
├── dto/            # Objetos de transferência de dados
├── exception/      # Tratamento de erros
├── model/          # Entidades do banco
├── repository/     # Acesso a dados
└── service/        # Lógica de negócio
```

---

## 📚 O que aprendi com este projeto

- ✅ Arquitetura em camadas (Controller, Service, Repository)
- ✅ DTOs para separação entre modelo e API
- ✅ Validação com Bean Validation (@Valid, @NotBlank, @Size)
- ✅ Tratamento global de exceções com @RestControllerAdvice
- ✅ Enumerações no banco de dados (@Enumerated)
- ✅ Migrações de banco com Flyway
- ✅ Configuração de CORS para frontend
- ✅ Integração com PostgreSQL

---

## 🎯 Próximos passos

- [ ] Criar categorias financeiras e vinculá-las aos lançamentos
- [ ] Implementar dashboard com saldo, receitas e despesas por período
- [ ] Adicionar filtros por data e paginação nos lançamentos
- [ ] Criar metas e orçamentos mensais
- [x] Adicionar autenticação e autorização inicial com JWT
- [ ] Criar planos e assinaturas de acesso
- [ ] Integrar gateway de pagamento e webhooks
- [ ] Liberar recursos conforme o status da assinatura
- [ ] Ampliar testes unitários, de integração e dos endpoints HTTP
- [ ] Restringir CORS e externalizar configurações sensíveis
- [ ] Containerizar a aplicação com Docker
- [ ] Configurar CI/CD com GitHub Actions
- [ ] Fazer deploy na nuvem

---

## 👨‍💻 Autor

**Gustavo Ben Abraham**

[![GitHub](https://img.shields.io/badge/GitHub-@GustavoBenAbraham-181717?style=flat&logo=github)](https://github.com/GustavoBenAbraham)
[![ORCID](https://img.shields.io/badge/ORCID-0009--0002--8023--217X-A6CE39?style=flat&logo=orcid)](https://orcid.org/0009-0002-8023-217X)

---

> Projeto desenvolvido para fins de aprendizado e portfólio.

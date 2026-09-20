# 📝 Task Manager API

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)

> API REST para gerenciamento de tarefas, construída com **Java 21**, **Spring Boot 4.1.1** e **PostgreSQL**.

---

## ✨ Funcionalidades

- ✅ **CRUD completo** de tarefas (Criar, Listar, Buscar, Atualizar, Deletar)
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
spring.datasource.password=SUA_SENHA_DO_POSTGRES
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

### 5. Execute os testes
```bash
./mvnw test
```

No Windows PowerShell, use `./mvnw.cmd test`.

---

## 🔗 API Endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/tasks` | Criar nova tarefa |
| GET | `/api/v1/tasks` | Listar todas as tarefas |
| GET | `/api/v1/tasks/{id}` | Buscar tarefa por ID |
| GET | `/api/v1/tasks/status/{status}` | Filtrar por status (`PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`) |
| PUT | `/api/v1/tasks/{id}` | Atualizar tarefa |
| DELETE | `/api/v1/tasks/{id}` | Deletar tarefa |

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

- [ ] Adicionar autenticação JWT
- [ ] Ampliar testes unitários e de integração
- [ ] Expor busca por título na API
- [ ] Adicionar paginação nos endpoints de listagem
- [ ] Containerização com Docker
- [ ] CI/CD com GitHub Actions
- [ ] Deploy na nuvem (Render)

---

## 👨‍💻 Autor

**Gustavo Ben Abraham**

[![GitHub](https://img.shields.io/badge/GitHub-@GustavoBenAbraham-181717?style=flat&logo=github)](https://github.com/GustavoBenAbraham)
[![ORCID](https://img.shields.io/badge/ORCID-0009--0002--8023--217X-A6CE39?style=flat&logo=orcid)](https://orcid.org/0009-0002-8023-217X)

---

> Projeto desenvolvido para fins de aprendizado e portfólio.

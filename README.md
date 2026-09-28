# DinDin de Giro

Aplicação web para organização financeira pessoal, com cadastro de usuários, autenticação JWT, API REST e dashboard em React.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![React](https://img.shields.io/badge/React-18-149ECA?style=for-the-badge&logo=react&logoColor=white)](https://react.dev/)

## Aplicação publicada

- **Site:** [task-manager-api-wmx2.onrender.com](https://task-manager-api-wmx2.onrender.com)
- **Health check:** [`/api/v1/health`](https://task-manager-api-wmx2.onrender.com/api/v1/health)

O serviço e o frontend foram verificados após o deploy. Em planos que suspendem serviços inativos, a primeira abertura pode demorar enquanto a aplicação inicia.

## Funcionalidades implementadas

- Cadastro e login de usuários com senhas protegidas por BCrypt e autenticação JWT.
- Separação de tarefas e dados financeiros por usuário autenticado.
- API para gerenciar tarefas, contas, categorias e lançamentos financeiros.
- Dashboard com resumo de receitas, despesas, saldo, contas e lançamentos recentes.
- Persistência PostgreSQL com migrações Flyway.
- Build Docker que reúne o frontend React e a API Spring Boot no mesmo serviço.

### Limites atuais

- A interface permite cadastrar, entrar e consultar o dashboard. Os controles de navegação e o botão “Novo lançamento” ainda são visuais; operações de criação e edição financeira estão disponíveis pela API, mas ainda não têm formulários na interface.
- Cada conta gerencia os próprios dados. Ainda não existe um painel administrativo para listar ou administrar contas de outros usuários.
- A API não inclui Swagger/OpenAPI neste momento.

## Tecnologias

- Java 21, Spring Boot, Spring Security, Spring Data JPA e Bean Validation
- PostgreSQL e Flyway
- JWT com JJWT e BCrypt
- React 18, Vite e lucide-react
- Maven e Docker Compose

## Executar localmente com Docker

### Pré-requisitos

- Docker Desktop em execução com suporte a containers Linux
- Docker Compose v2 ou posterior
- PowerShell no Windows para gerar automaticamente os segredos locais abaixo

Na raiz do repositório, crie um arquivo `.env` local com credenciais aleatórias. O arquivo é ignorado pelo Git:

```powershell
if (-not (Test-Path .env)) {
    $random = [Security.Cryptography.RandomNumberGenerator]::Create()
    $dbBytes = New-Object byte[] 24
    $jwtBytes = New-Object byte[] 32
    $random.GetBytes($dbBytes)
    $random.GetBytes($jwtBytes)
    @(
        "DB_PASSWORD=$([Convert]::ToBase64String($dbBytes))"
        "JWT_SECRET=$([Convert]::ToBase64String($jwtBytes))"
    ) | Set-Content -Path .env -Encoding Ascii
    $random.Dispose()
}

docker compose up --build -d
```

Abra [http://localhost:8080](http://localhost:8080). Para acompanhar a inicialização, use `docker compose logs -f app`. Para parar os serviços, use `docker compose down`; o volume do PostgreSQL é mantido.

O `.env` guarda os segredos para reutilizar o banco entre reinicializações locais. Não o envie ao GitHub nem use esses valores em produção.

### Executar o frontend com Vite (opcional)

Com os serviços Docker ativos, o frontend também pode rodar separadamente para desenvolvimento:

```powershell
cd frontend
npm ci
npm run dev
```

Abra [http://localhost:5173](http://localhost:5173). O Vite encaminha chamadas `/api` para a API local na porta 8080.

## API

As rotas de autenticação e health check são públicas. As demais exigem um JWT no cabeçalho `Authorization: Bearer <token>`.

| Recurso | Rotas e métodos |
| --- | --- |
| Autenticação | `POST /api/v1/auth/register`, `POST /api/v1/auth/login` (públicas) |
| Health | `GET /api/v1/health` (pública) |
| Tarefas | `GET` e `POST /api/v1/tasks`; `GET`, `PUT` e `DELETE /api/v1/tasks/{id}`; filtros por status e paginação |
| Contas | `GET` e `POST /api/v1/contas`; `GET` e `PUT /api/v1/contas/{id}`; `PATCH /api/v1/contas/{id}/desativar` |
| Categorias | `GET` e `POST /api/v1/categorias`; `GET` e `PUT /api/v1/categorias/{id}`; `PATCH /api/v1/categorias/{id}/desativar` |
| Lançamentos | `GET` e `POST /api/v1/lancamentos`; `GET`, `PUT` e `DELETE /api/v1/lancamentos/{id}`; filtro por tipo |
| Dashboard | `GET /api/v1/dashboard/resumo?inicio=AAAA-MM-DD&fim=AAAA-MM-DD` |

Exemplo de cadastro:

```json
{
  "nome": "Maria",
  "email": "maria@example.com",
  "senha": "uma-senha-com-8-caracteres"
}
```

Exemplo de lançamento:

```json
{
  "descricao": "Supermercado",
  "valor": 250.75,
  "tipo": "DESPESA",
  "data": "2026-09-20",
  "categoria": "Alimentação",
  "contaId": 1,
  "observacao": "Compras do mês"
}
```

## Publicar no Render

O [Dockerfile](Dockerfile) compila o React e a API na mesma imagem. Para configurar um novo serviço ou revisar o existente, siga [DEPLOY_RENDER.md](DEPLOY_RENDER.md). Não coloque senhas, URLs de banco com credenciais ou chaves JWT no repositório.

Variáveis principais do serviço:

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET` (chave Base64 com pelo menos 32 bytes aleatórios)

## Testes

Execute a suíte automatizada com:

```powershell
.\mvnw.cmd test
```

No Linux/macOS, use `./mvnw test`.

## Estrutura do projeto

```text
src/main/java/com/gustavo/taskmanager/
├── config/       # Segurança e CORS
├── controller/   # Rotas REST
├── dto/          # Contratos de entrada e saída
├── exception/    # Erros HTTP
├── model/        # Entidades e enums
├── repository/   # Persistência JPA
└── service/      # Regras de negócio

frontend/         # Aplicação React/Vite
src/main/resources/db/migration/  # Migrações Flyway
```

## Autor

**Gustavo Ben Abraham**

[GitHub](https://github.com/GustavoBenAbraham) · [ORCID](https://orcid.org/0009-0002-8023-217X)

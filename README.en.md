# DinDin de Giro

[Português](README.md) | **English**

A web application for self-employed people, Brazilian individual micro-entrepreneurs (MEIs) and small businesses to organize their finances. Personal and business financial spaces are kept separate.

The repository retains its original name, `task-manager-api`, while the product has evolved into DinDin de Giro.

## Published application

- [Website](https://task-manager-api-wmx2.onrender.com)
- [Health check](https://task-manager-api-wmx2.onrender.com/api/v1/health)

The hosting service may take time to respond after inactivity.

## Features

- Registration/login with BCrypt password hashing and JWT authentication.
- Personal financial space and an optional business space; a Brazilian business tax ID (CNPJ) is optional.
- Accounts, categories and entries isolated by financial space.
- Invitations to a financial manager, accepted through the invited person's account.
- Business owners and authorized managers share business data; managers do not receive access to the owner's personal space.
- Owners can revoke access. Records track their creator and last editor.
- Monthly dashboard and account/category/entry management.
- Planned payables/receivables with due dates, editing/cancellation while pending and settlement into a financial account.
- Settling a planned item creates one realized transaction. Pending amounts do not alter actual balances or realized income/expense totals.
- Cash-flow summaries separate opening balance, realized values, pending forecasts and projected balance.
- Flyway migrations and a Docker image serving React and Spring Boot together.

## Current limits

Invitations are manually shared links that expire after seven days; email delivery is not implemented. Technical platform administration is not implemented. Creator/last-editor fields are not a complete immutable audit log. Bank reconciliation, advanced reports and Swagger/OpenAPI are not included.

## Technologies

Java 21, Spring Boot 4.1.1, Spring Security, Spring Data JPA, Bean Validation, PostgreSQL 17, Flyway, JJWT, BCrypt, React 18, Vite, lucide-react, Maven and Docker Compose.

## Run with Docker

Requires Docker Desktop running Linux containers, Docker Compose v2+ and PowerShell for the local-secret setup below. From the repository root:

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

Open [http://localhost:8080](http://localhost:8080). Follow startup with `docker compose logs -f app`. Stop with `docker compose down`; the PostgreSQL volume remains.

The local `.env` is ignored by Git. Do not publish it or reuse its secrets in production.

### Optional Vite frontend

With Docker services running:

```powershell
cd frontend
npm ci
npm run dev
```

Open `http://localhost:5173`. Vite proxies `/api` to port 8080.

## API

Except authentication and health, routes require `Authorization: Bearer <token>`. Financial calls use `X-Espaco-Financeiro-Id` to select a space; omitting it selects the authenticated user's personal space. Route and field names remain in Portuguese.

| Resource | Routes |
| --- | --- |
| Authentication | POST `/api/v1/auth/register`, POST `/api/v1/auth/login` |
| Health | GET `/api/v1/health` |
| Tasks | GET/POST `/api/v1/tasks`; GET/PUT/DELETE `/api/v1/tasks/{id}` |
| Spaces | GET/POST `/api/v1/espacos` |
| Access | GET `/api/v1/espacos/{id}/acessos`; DELETE `/api/v1/espacos/{id}/acessos/{usuarioId}` |
| Invitations | POST `/api/v1/espacos/{id}/convites`; POST `/api/v1/convites/aceitar` |
| Planned items | GET/POST `/api/v1/contas-previstas`; PUT `/{id}`, POST `/{id}/liquidar`, PATCH `/{id}/cancelar` under that path |
| Accounts | GET/POST `/api/v1/contas`; GET/PUT `/{id}`, PATCH `/{id}/desativar` |
| Categories | GET/POST `/api/v1/categorias`; GET/PUT `/{id}`, PATCH `/{id}/desativar` |
| Entries | GET/POST `/api/v1/lancamentos`; GET/PUT/DELETE `/{id}` |
| Dashboard | GET `/api/v1/dashboard/resumo?inicio=YYYY-MM-DD&fim=YYYY-MM-DD` |
| Cash flow | GET `/api/v1/dashboard/fluxo-caixa?inicio=YYYY-MM-DD&fim=YYYY-MM-DD` |

Create a business space with `nome`, `tipo: "NEGOCIO"` and optional `cnpj`. Invitations can only be accepted by an authenticated account matching the invited email.

## Learning context

The project provides practice modeling financial business rules, isolating user/workspace data, implementing JWT authentication, managing database migrations and integrating a React client with a Java API. Planned versus realized values and invitation permissions are examples of rules that go beyond basic CRUD.

## Tests, structure and deployment

```powershell
.\mvnw.cmd test
```

Linux/macOS: `./mvnw test`. Tests were not rerun for this documentation update.

- `src/main/java/com/gustavo/taskmanager/`: API, rules and persistence.
- `src/main/resources/db/migration/`: Flyway migrations.
- `frontend/`: React/Vite client.
- [Product plan](docs/PLANO_PRODUTO.md): decisions and product stages.
- [Render deployment guide](DEPLOY_RENDER.md): deployment instructions, currently in Portuguese.

The Dockerfile builds React and the API into one image. Main production variables are `SPRING_PROFILES_ACTIVE=prod`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` and `JWT_SECRET` (Base64 encoding at least 32 random bytes). Back up the database before schema changes.

## Author and license

[Gustavo Ben Abraham](https://github.com/GustavoBenAbraham) · [ORCID](https://orcid.org/0009-0002-8023-217X)

Licensed under [MIT](LICENSE).

# DinDin de Giro

Aplicação web para organizar as finanças de pessoas autônomas, MEIs e pequenos negócios. O produto também permite manter um espaço pessoal separado do negócio.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![React](https://img.shields.io/badge/React-18-149ECA?style=for-the-badge&logo=react&logoColor=white)](https://react.dev/)

## Aplicação publicada

- **Site:** [task-manager-api-wmx2.onrender.com](https://task-manager-api-wmx2.onrender.com)
- **Health check:** [`/api/v1/health`](https://task-manager-api-wmx2.onrender.com/api/v1/health)

O serviço pode levar alguns instantes para responder após um período sem uso, conforme o plano de hospedagem.

## Funcionalidades

- Cadastro e login com senhas protegidas por BCrypt e autenticação JWT.
- Espaço pessoal e criação de um espaço para negócio ou atividade informal; CNPJ é opcional.
- Contas, categorias e lançamentos separados por espaço financeiro.
- Convite da pessoa responsável pelo negócio para uma gestora financeira, com aceite pela conta da própria gestora.
- Proprietária e gestora autorizada consultam e editam os mesmos dados do negócio. A gestora não recebe acesso ao espaço pessoal.
- A proprietária pode revogar o acesso da gestora.
- Cada conta, categoria e lançamento registra quem criou o dado e quem fez a última alteração.
- Dashboard mensal, gestão de contas e categorias, e cadastro, edição e exclusão de lançamentos.
- Contas previstas a pagar e a receber com vencimento, categoria, edição/cancelamento enquanto pendentes e liquidação em uma conta financeira.
- Liquidar uma conta prevista cria uma única movimentação realizada; valores pendentes não alteram saldos nem o resumo de receitas/despesas realizadas.
- Resumo mensal de fluxo de caixa mostra saldo inicial, valores realizados, previsões pendentes e saldo projetado em campos separados.
- PostgreSQL com migrações Flyway. Docker reúne frontend React e API Spring Boot.

### Limites desta etapa

- Convites são compartilhados manualmente por link e expiram em sete dias; o sistema ainda não envia e-mails.
- Administração técnica da plataforma é diferente do papel de gestora financeira e ainda não foi implementada.
- Há registro de criador e última pessoa que alterou, mas ainda não existe um histórico imutável de todas as alterações.
- O módulo diferencia valores previstos de movimentações realizadas. Conciliação bancária e relatórios avançados ficam para etapas futuras.
- A API ainda não inclui Swagger/OpenAPI.

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
- PowerShell no Windows para gerar os segredos locais abaixo

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

Abra [http://localhost:8080](http://localhost:8080). Para acompanhar a inicialização, use `docker compose logs -f app`. Para parar, use `docker compose down`; o volume do PostgreSQL é mantido.

O `.env` guarda os segredos locais. Não o envie ao GitHub nem use esses valores em produção.

### Executar o frontend com Vite (opcional)

Com os serviços Docker ativos:

```powershell
cd frontend
npm ci
npm run dev
```

Abra [http://localhost:5173](http://localhost:5173). O Vite encaminha chamadas `/api` para a API local na porta 8080.

## API

Todas as rotas, exceto autenticação e health check, exigem `Authorization: Bearer <token>`. Nas chamadas financeiras, `X-Espaco-Financeiro-Id` seleciona o espaço. Se o cabeçalho não for enviado, a API usa o espaço pessoal da pessoa autenticada.

| Recurso | Rotas e métodos |
| --- | --- |
| Autenticação | `POST /api/v1/auth/register`, `POST /api/v1/auth/login` (públicas) |
| Health | `GET /api/v1/health` (pública) |
| Tarefas | `GET` e `POST /api/v1/tasks`; `GET`, `PUT` e `DELETE /api/v1/tasks/{id}`; filtros por status e paginação |
| Espaços | `GET` e `POST /api/v1/espacos` |
| Acessos | `GET /api/v1/espacos/{id}/acessos`; `DELETE /api/v1/espacos/{id}/acessos/{usuarioId}` |
| Convites | `POST /api/v1/espacos/{id}/convites`; `POST /api/v1/convites/aceitar` |
| Contas previstas | `GET` e `POST /api/v1/contas-previstas`; `PUT /api/v1/contas-previstas/{id}`; `POST /api/v1/contas-previstas/{id}/liquidar`; `PATCH /api/v1/contas-previstas/{id}/cancelar` |
| Contas | `GET` e `POST /api/v1/contas`; `GET` e `PUT /api/v1/contas/{id}`; `PATCH /api/v1/contas/{id}/desativar` |
| Categorias | `GET` e `POST /api/v1/categorias`; `GET` e `PUT /api/v1/categorias/{id}`; `PATCH /api/v1/categorias/{id}/desativar` |
| Lançamentos | `GET` e `POST /api/v1/lancamentos`; `GET`, `PUT` e `DELETE /api/v1/lancamentos/{id}`; filtro por tipo |
| Dashboard | `GET /api/v1/dashboard/resumo?inicio=AAAA-MM-DD&fim=AAAA-MM-DD` |
| Fluxo de caixa | `GET /api/v1/dashboard/fluxo-caixa?inicio=AAAA-MM-DD&fim=AAAA-MM-DD` |

Para criar um espaço de negócio, envie `nome`, `tipo: "NEGOCIO"` e, opcionalmente, `cnpj` para `POST /api/v1/espacos`. O convite só pode ser aceito por uma conta autenticada com o mesmo e-mail convidado.

## Publicar no Render

O [Dockerfile](Dockerfile) compila o React e a API na mesma imagem. Para configurar ou revisar o serviço, siga [DEPLOY_RENDER.md](DEPLOY_RENDER.md). As migrações Flyway são executadas na inicialização; faça backup do banco antes de publicar alterações de esquema. Não coloque senhas, URLs de banco com credenciais ou chaves JWT no repositório.

Variáveis principais: `SPRING_PROFILES_ACTIVE=prod`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` e `JWT_SECRET` (Base64 com pelo menos 32 bytes aleatórios).

## Testes

```powershell
.\mvnw.cmd test
```

No Linux/macOS, use `./mvnw test`.

## Estrutura

```text
src/main/java/com/gustavo/taskmanager/  # API, regras de negócio e persistência
src/main/resources/db/migration/        # Migrações Flyway
frontend/                               # Aplicação React/Vite
docs/PLANO_PRODUTO.md                   # Decisões e etapas do produto
```

## Autor

**Gustavo Ben Abraham**

[GitHub](https://github.com/GustavoBenAbraham) · [ORCID](https://orcid.org/0009-0002-8023-217X)

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).

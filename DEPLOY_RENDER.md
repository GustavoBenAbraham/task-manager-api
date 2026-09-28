# Deploy no Render usando Docker

O Dockerfile da raiz compila o frontend React e a API Spring Boot na mesma imagem. Assim, o site e os endpoints ficam no mesmo domínio e o navegador não precisa de uma URL separada para a API.

## 1. Criar o banco PostgreSQL

No painel do Render, crie um PostgreSQL e copie os dados da conexão **Internal**: host, porta, nome do banco, usuário e senha. A instância da aplicação e o banco devem estar na mesma região.

## 2. Criar o serviço web

Crie um Web Service conectado ao repositório e configure:

- **Runtime:** Docker
- **Dockerfile Path:** `./Dockerfile`
- **Health Check Path:** `/api/v1/health`

Não configure comandos Maven de build ou start; o Dockerfile faz o build das duas partes e inicia a aplicação.

## 3. Definir variáveis de ambiente

No serviço web, adicione:

| Variável | Valor |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://HOST:PORT/NOME_DO_BANCO` usando o host e a porta internos do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | usuário informado pelo Render |
| `SPRING_DATASOURCE_PASSWORD` | senha informada pelo Render |
| `JWT_SECRET` | chave aleatória em Base64 com pelo menos 32 bytes |

Gere uma chave localmente no PowerShell com:

```powershell
$random = [Security.Cryptography.RandomNumberGenerator]::Create()
$keyBytes = New-Object byte[] 32
$random.GetBytes($keyBytes)
[Convert]::ToBase64String($keyBytes)
$random.Dispose()
```

Guarde o valor exibido como segredo no painel do Render e não o adicione ao Git.

Se hospedar o frontend em outro domínio, configure `APP_CORS_ALLOWED_ORIGINS` com a origem exata, por exemplo `https://meu-site.exemplo.com`. Para o frontend incluído nesta imagem, não precisa configurar CORS.

## 4. Publicar e conferir

Faça o deploy pelo painel e abra a URL gerada pelo Render. A página inicial deve mostrar o formulário do DinDin; `/api/v1/health` deve retornar `status: UP`. Cadastre uma conta e entre para conferir a conexão com o banco.

O plano e os limites de disponibilidade e armazenamento dependem das opções vigentes na sua conta Render. Consulte os valores no painel antes de escolher o banco e o serviço.

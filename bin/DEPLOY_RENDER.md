# Deploy Gratuito no Render

## Por que Render?
- ✅ 100% Gratuito
- ✅ PostgreSQL grátis incluído
- ✅ Deploy automático do GitHub
- ✅ SSL/HTTPS grátis
- ✅ Perfecto para Java/Spring Boot

## Passo a Passo

### 1. Criar Conta Render
1. Acesse: https://render.com
2. Clique em "Sign Up"
3. Faça login com GitHub (mais fácil)

### 2. Criar Web Service
1. Após login, clique em "New +"
2. Selecione "Web Service"
3. Conecte seu GitHub
4. Selecione o repositório: `GustavoBenAbraham/task-manager-api`
5. Render vai detectar que é Java/Maven automaticamente

### 3. Configurar Build
Render vai detectar automaticamente:
- **Build Command**: `./mvnw clean package -DskipTests`
- **Start Command**: `java -jar target/taskmanager-0.0.1-SNAPSHOT.jar`

Se não detectar, configure manualmente.

### 4. Criar PostgreSQL Database
1. No mesmo projeto Render, clique em "New +"
2. Selecione "PostgreSQL"
3. Nome: `taskmanager-db`
4. Plano: Free (gratuito)
5. Clique em "Create Database"

### 5. Configurar Variáveis de Ambiente
No Web Service, vá em "Environment":

```
DB_PASSWORD = Hellen20!
JWT_SECRET = bXlTdXBlclNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlc011Y2hMb25nZXJUaGFuMjU2Qml0cw==
SPRING_DATASOURCE_URL = jdbc:postgresql://<databases-internal-url>:5432/taskmanager
SPRING_DATASOURCE_USERNAME = <username do banco>
SPRING_PROFILES_ACTIVE = prod
```

**Importante:** Render vai fornecer a "Internal Database URL" automática.
Use ela no SPRING_DATASOURCE_URL.

### 6. Deploy Automático
Render vai fazer deploy automático quando você fizer push no GitHub.

### 7. Acessar a Aplicação
Render vai fornecer uma URL como:
`https://taskmanager-api.onrender.com`

## Limitações do Plano Gratuito
- App "hiberna" após 15min sem uso (acorda em ~30s)
- 512MB RAM (suficiente para sua aplicação)
- PostgreSQL gratuito (1GB armazenamento)

## Monitoramento
- No Dashboard do Render, monitore logs
- Acompanhe métricas de uso
- PostgreSQL tem console grátis

## Suporte
Render tem excelente documentação e suporte gratuito.

## Alternativa: Heroku Eco
Se preferir Heroku, o processo é similar:
1. Criar conta Heroku
2. Criar app Heroku
3. Conectar GitHub
4. Adicionar PostgreSQL Hobby Dev
5. Configurar variáveis de ambiente
6. Deploy automático

Heroku é um pouco mais complexo mas também tem plano gratuito.

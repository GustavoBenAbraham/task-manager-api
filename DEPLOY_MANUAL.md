# Deploy Manual no Azure App Service

## Passo a Passo via Portal do Azure

### 1. Acessar Portal do Azure
- Acesse: https://portal.azure.com
- Faça login com sua conta Microsoft

### 2. Criar Resource Group
1. No portal, procure por "Resource Groups"
2. Clique em "+ Criar"
3. Nome: `taskmanager-rg`
4. Região: Escolha a mais próxima (ex: Brazil South)
5. Clique em "Revisar + criar" e depois "Criar"

### 3. Criar App Service Plan
1. Procure por "App Service Plans"
2. Clique em "+ Criar"
3. Assinatura: Sua assinatura
4. Resource Group: `taskmanager-rg`
5. Nome: `taskmanager-plan`
6. Sistema Operacional: Linux
7. Região: Mesma do Resource Group
8. SKU e tamanho: B1 (Basic) - cerca de $13/mês
9. Clique em "Revisar + criar" e depois "Criar"

### 4. Criar Web App
1. Procure por "App Services"
2. Clique em "+ Criar"
3. Assinatura: Sua assinatura
4. Resource Group: `taskmanager-rg`
5. Nome: `taskmanager-api-unique` (use um nome único)
6. Publicar: Código
7. Runtime stack: Java 21
8. Sistema Operacional: Linux
9. Plano: `taskmanager-plan` (criado anteriormente)
10. Clique em "Revisar + criar" e depois "Criar"

### 5. Configurar Banco de Dados
1. Procure por "Azure Database for PostgreSQL"
2. Crie um servidor PostgreSQL
3. Configure as credenciais (salve a senha!)
4. Crie o banco de dados `taskmanager`

### 6. Configurar a Web App
1. Acesse o Web App criado
2. Vá em "Configuração" → "Variáveis de ambiente"
3. Adicione as variáveis:
   ```
   DB_PASSWORD = sua_senha_postgres
   JWT_SECRET = sua_chave_base64_forte
   SPRING_DATASOURCE_URL = jdbc:postgresql://seu-server.postgres.database.azure.com:5432/taskmanager
   SPRING_DATASOURCE_USERNAME = seu_usuario_postgres
   SPRING_PROFILES_ACTIVE = prod
   ```

### 7. Build do JAR Localmente
No seu projeto:
```bash
cd C:\Users\AMDOCS\GitHub\task-manager-api
.\mvnw.cmd clean package -DskipTests
```

### 8. Deploy do JAR
1. No Web App, vá em "Deployment Center"
2. Conecte ao GitHub
3. Selecione seu repositório `GustavoBenAbraham/task-manager-api`
4. Configure o build:
   - Runtime: Java 21
   - Start command: `java -jar target/taskmanager-0.0.1-SNAPSHOT.jar`
5. Habilite o CI/CD automático

### 9. Monitorar Deploy
1. O Azure vai fazer build automático
2. Monitore em "Deployment Center" → "Logs"
3. Quando o deploy terminar, acesse a URL do site

### 10. Testar a Aplicação
A URL será algo como: `https://taskmanager-api-unique.azurewebsites.net`

---

## Alternativa Mais Rápida: Railway

Se quiser algo mais rápido, considere Railway:

1. Acesse: https://railway.app
2. Criar conta (GitHub login)
3. "New Project" → "Deploy from GitHub repo"
4. Selecione `GustavoBenAbraham/task-manager-api`
5. Adicionar PostgreSQL service
6. Configurar variáveis de ambiente
7. Deploy automático em poucos minutos

Custo estimado: ~$5/mês para plano básico

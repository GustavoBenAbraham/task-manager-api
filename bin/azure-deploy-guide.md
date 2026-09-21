# Azure App Service Deploy Guide

## Opções de Deploy

### Opção 1: Azure App Service - Web App for Containers (Recomendado)

1. **Criar Azure Container Registry (ACR)**
```bash
# Via Azure Portal ou Azure CLI
az acr create --resource-group taskmanager-rg --name taskmanageracr --sku Basic
az acr login --name taskmanageracr
```

2. **Build e push da imagem Docker**
```bash
# Tag da imagem
docker tag taskmanager:latest taskmanageracr.azurecr.io/taskmanager:latest

# Push para ACR
docker push taskmanageracr.azurecr.io/taskmanager:latest
```

3. **Criar Web App for Containers**
```bash
az webapp create \
  --resource-group taskmanager-rg \
  --name taskmanager-api \
  --plan taskmanager-plan \
  --sku B1 \
  --is-linux \
  --deployment-container-image-name taskmanageracr.azurecr.io/taskmanager:latest
```

### Opção 2: Azure App Service - Deploy via FTP (Simpler)

1. **Criar Web App Java**
```bash
az webapp create \
  --resource-group taskmanager-rg \
  --name taskmanager-api \
  --plan taskmanager-plan \
  --sku B1 \
  --runtime "JAVA|21-java21"
```

2. **Build do JAR**
```bash
mvn clean package -DskipTests
```

3. **Deploy via FTP**
- Baixe as credenciais FTP do Azure Portal
- Use FileZilla ou similar para upload do JAR
- O arquivo deve ser renomeado para `app.jar`

### Opção 3: Via Portal do Azure (Manual)

1. **Acesse portal.azure.com**
2. **Criar Resource Group**: taskmanager-rg
3. **Criar App Service Plan**: B1 (Basic)
4. **Criar Web App**: Java 21 Runtime
5. **Configuration**: Adicionar variáveis de ambiente
6. **Deployment Center**: Conectar ao GitHub e habilitar CI/CD

## Variáveis de Ambiente Necessárias

```bash
DB_PASSWORD=sua_senha_postgres
JWT_SECRET=sua_chave_base64_forte
SPRING_PROFILES_ACTIVE=prod
```

## Banco de Dados

### Azure Database for PostgreSQL
```bash
az postgres db create \
  --resource-group taskmanager-rg \
  --server-name taskmanager-postgres \
  --name taskmanager
```

## Custos Estimados

- **App Service B1**: ~$13-20/mês
- **PostgreSQL**: ~$15-25/mês
- **Total**: ~$30-45/mês

## Próximos Passos

1. Criar conta Azure (se não tiver)
2. Criar Resource Group
3. Escolher método de deploy
4. Configurar banco de dados
5. Configurar variáveis de ambiente
6. Deploy da aplicação

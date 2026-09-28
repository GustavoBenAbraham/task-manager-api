# Deploy manual

O artefato recomendado para publicar o projeto completo é a imagem Docker produzida pelo [Dockerfile](Dockerfile). Ela compila o frontend e o backend juntos; publicar somente o JAR Maven não inclui a interface.

Para publicar no Render, siga [DEPLOY_RENDER.md](DEPLOY_RENDER.md). Em outro provedor que aceite Docker, configure o serviço para construir o Dockerfile da raiz, definir as variáveis de ambiente listadas nesse guia e encaminhar tráfego HTTP para a porta `8080`.

Use um PostgreSQL acessível pelo serviço e configure `SPRING_PROFILES_ACTIVE=prod`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` e `JWT_SECRET`. Gere uma chave Base64 aleatória com pelo menos 32 bytes e cadastre-a no gerenciador de segredos do provedor. Não publique credenciais no Git.

# ── Stage 1: build do frontend (React + Vite) ─────────────────────────────────
FROM node:20-alpine AS frontend-build
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build
# O artefato fica em /frontend/dist

# ── Stage 2: build do backend (Spring Boot + Maven) ───────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY pom.xml .
# Baixa dependências em camada separada para cache mais eficiente
RUN mvn dependency:go-offline -q
COPY src ./src
# Copia o build do frontend para ser servido como recurso estático pelo Spring
COPY --from=frontend-build /frontend/dist ./src/main/resources/static
RUN mvn clean package -DskipTests

# ── Stage 3: imagem de runtime ────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/taskmanager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

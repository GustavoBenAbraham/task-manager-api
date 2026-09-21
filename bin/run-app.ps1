# Script para configurar e rodar a aplicação
$env:JAVA_HOME = "C:\Users\AMDOCS\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
$env:PATH = "C:\Users\AMDOCS\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin;" + $env:PATH

# Configurações do banco de dados
$env:DB_PASSWORD = "Hellen20!"
$env:JWT_SECRET = "bXlTdXBlclNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlc011Y2hMb25nZXJUaGFuMjU2Qml0cw=="

Write-Host "=== Configurações ==="
Write-Host "Java Home: $env:JAVA_HOME"
Write-Host "DB Password: configurado"
Write-Host "JWT Secret: configurado"
Write-Host ""

Write-Host "=== Iniciando aplicação ==="
Write-Host "A API estará disponível em: http://localhost:8080"
Write-Host "Swagger UI: http://localhost:8080/swagger-ui.html"
Write-Host ""

& ".\mvnw.cmd" spring-boot:run

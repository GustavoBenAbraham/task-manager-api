# Configure as credenciais localmente por variáveis de ambiente ou informe-as aqui.
if (-not $env:DB_PASSWORD) {
    $securePassword = Read-Host "Senha do PostgreSQL local" -AsSecureString
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $securePassword).Password
}
if (-not $env:JWT_SECRET) {
    $random = [Security.Cryptography.RandomNumberGenerator]::Create()
    $keyBytes = New-Object byte[] 32
    $random.GetBytes($keyBytes)
    $env:JWT_SECRET = [Convert]::ToBase64String($keyBytes)
    $random.Dispose()
    Write-Host "Chave JWT temporária criada para esta execução local."
}

$env:SPRING_PROFILES_ACTIVE = "dev"
Write-Host "Iniciando aplicação em http://localhost:8080"
& ".\mvnw.cmd" spring-boot:run

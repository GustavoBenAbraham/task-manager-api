$env:PGPASSWORD = "postgres"
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -c "CREATE DATABASE taskmanager;" 2>$null
Write-Host "Banco de dados taskmanager criado (ou já existe)"

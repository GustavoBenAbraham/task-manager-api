$env:JAVA_HOME = "C:\Users\AMDOCS\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
$env:PATH = "C:\Users\AMDOCS\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin;" + $env:PATH
Write-Host "Java version:"
& "C:\Users\AMDOCS\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin\java.exe" -version
Write-Host "Executando testes..."
& ".\mvnw.cmd" test

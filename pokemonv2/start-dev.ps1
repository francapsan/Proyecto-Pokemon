Write-Host "🚀 Arrancando el Simulador Pokémon..." -ForegroundColor Yellow

Write-Host "Iniciando Backend (Spring Boot) en una nueva ventana..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot'; mvn spring-boot:run"

Start-Sleep -Seconds 3
Write-Host "Iniciando Frontend (React) en otra ventana..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot\frontend'; npm run dev"
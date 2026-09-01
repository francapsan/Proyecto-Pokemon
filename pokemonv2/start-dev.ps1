#requires -Version 5.1
[CmdletBinding()]
param(
    [int]$BackendPort = 8080,
    [int]$BackendWaitSeconds = 60
)

$ErrorActionPreference = 'Stop'

Write-Host "Arrancando el Simulador Pokemon..." -ForegroundColor Yellow

# Prerequisitos
foreach ($tool in 'mvn', 'npm') {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
        Write-Host "ERROR: '$tool' no está en PATH." -ForegroundColor Red
        exit 1
    }
}

$frontendPath = Join-Path $PSScriptRoot 'frontend'
if (-not (Test-Path (Join-Path $frontendPath 'package.json'))) {
    Write-Host "ERROR: no se encuentra $frontendPath\package.json" -ForegroundColor Red
    exit 1
}

Write-Host "Iniciando Backend (Spring Boot) en una nueva ventana..." -ForegroundColor Green
Start-Process powershell -ArgumentList @(
    '-NoExit', '-NoProfile', '-Command',
    "Set-Location '$PSScriptRoot'; mvn spring-boot:run"
)

Write-Host "Esperando a que el backend responda en http://localhost:$BackendPort ..." -ForegroundColor DarkGray
$deadline = (Get-Date).AddSeconds($BackendWaitSeconds)
$ready = $false
while ((Get-Date) -lt $deadline) {
    try {
        $tcp = New-Object System.Net.Sockets.TcpClient
        $iar = $tcp.BeginConnect('127.0.0.1', $BackendPort, $null, $null)
        if ($iar.AsyncWaitHandle.WaitOne(500)) {
            $tcp.EndConnect($iar)
            $tcp.Close()
            $ready = $true
            break
        }
        $tcp.Close()
    } catch {
        # Ignorar y reintentar
    }
    Start-Sleep -Milliseconds 500
}

if ($ready) {
    Write-Host "Backend levantado en :$BackendPort" -ForegroundColor Green
} else {
    Write-Host "AVISO: el backend no respondió en $BackendWaitSeconds s; arranco el frontend igualmente." -ForegroundColor Yellow
}

Write-Host "Iniciando Frontend (React) en otra ventana..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    '-NoExit', '-NoProfile', '-Command',
    "Set-Location '$frontendPath'; npm run dev"
)

Write-Host "Listo. Backend: http://localhost:$BackendPort  |  Frontend: (mira la ventana de Vite)" -ForegroundColor Yellow

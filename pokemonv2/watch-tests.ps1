. (Join-Path $PSScriptRoot 'test-helpers.ps1')

$script:lastActionTime = [DateTime]::MinValue
$script:actionLock = New-Object System.Object
$debounceMillis = 1500

$watcher = New-Object System.IO.FileSystemWatcher -Property @{
    Path = Join-Path $PSScriptRoot 'src/main/java/com/pokemon';
    Filter = '*.java';
    IncludeSubdirectories = $true;
    EnableRaisingEvents = $true
}

$action = {
    
    $currentTime = Get-Date
    if (($currentTime - $script:lastActionTime).TotalMilliseconds -lt $debounceMillis) {
        return
    }
    lock ($script:actionLock) {
        $script:lastActionTime = $currentTime
    }

    Write-Log "Cambio detectado en '$($EventArgs.Name)'. Iniciando ciclo de test..." "Info" "Watcher"
    mvn clean compile -q
    if ($LASTEXITCODE -ne 0) {
        Write-Log "Error de compilación. Por favor, revisa tu código." "Error" "Watcher"
        return
    }

    Write-Log "Generando y fusionando tests..." "Info" "Watcher"
    powershell -File (Join-Path $PSScriptRoot 'merge-tests.ps1')
    if ($LASTEXITCODE -ne 0) {
        Write-Log "Error al generar o fusionar tests. Revisa 'merge-tests.ps1'." "Error" "Watcher"
        return
    }

    Write-Log "Ejecutando todos los tests..." "Info" "Watcher"
    mvn test
    if ($LASTEXITCODE -ne 0) {
        Write-Log "¡Algunos tests fallaron! Revisa los resultados." "Error" "Watcher"
    } else {
        Write-Log "Todos los tests pasaron correctamente." "Success" "Watcher"
    }
}

Register-ObjectEvent $watcher Changed -Action $action
Register-ObjectEvent $watcher Created -Action $action
Register-ObjectEvent $watcher Renamed -Action $action

Write-Log "Vigilando cambios ... Ctrl+C para parar" "Success" "Watcher"
while($true) { Start-Sleep 1 }
#requires -Version 5.1
<#
.SYNOPSIS
    Watcher que recompila y ejecuta los tests cada vez que cambia un .java.

.DESCRIPTION
    Escucha cambios en `src/**/*.java`. Con un debounce configurable
    lanza `mvn -q clean test`. Sirve para hacer TDD local sin depender
    de generadores externos.

.EXAMPLE
    ./watch-tests.ps1
    ./watch-tests.ps1 -DebounceMillis 2000
#>
[CmdletBinding()]
param(
    [int]$DebounceMillis = 1500,
    [string]$SourceRelativePath = 'src'
)

. (Join-Path $PSScriptRoot 'test-helpers.ps1')

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Log "Maven ('mvn') no está en PATH. Aborto." "Error" "Watcher"
    exit 1
}

$watchPath = Join-Path $PSScriptRoot $SourceRelativePath
if (-not (Test-Path $watchPath)) {
    Write-Log "No existe la ruta a vigilar: $watchPath" "Error" "Watcher"
    exit 1
}

$watcher = New-Object System.IO.FileSystemWatcher -Property @{
    Path                  = $watchPath
    Filter                = '*.java'
    IncludeSubdirectories = $true
    EnableRaisingEvents   = $true
    NotifyFilter          = [System.IO.NotifyFilters]'FileName, LastWrite, Size'
}

# Cola thread-safe: los eventos se encolan y el bucle principal los procesa
# de forma serializada (evita 'lock', que no existe en PowerShell).
$queue = [System.Collections.Concurrent.ConcurrentQueue[string]]::new()

$enqueueAction = {
    $q = $Event.MessageData
    $q.Enqueue($EventArgs.Name)
}

$subscriptions = @(
    Register-ObjectEvent -InputObject $watcher -EventName Changed -Action $enqueueAction -MessageData $queue
    Register-ObjectEvent -InputObject $watcher -EventName Created -Action $enqueueAction -MessageData $queue
    Register-ObjectEvent -InputObject $watcher -EventName Renamed -Action $enqueueAction -MessageData $queue
)

function Invoke-TestCycle {
    param([string]$Trigger)

    Write-Log "Cambio detectado en '$Trigger'. Ejecutando 'mvn clean test'..." "Info" "Watcher"

    mvn -q -B -ntp clean test
    if ($LASTEXITCODE -ne 0) {
        Write-Log "Algún test falló o la compilación se rompió." "Error" "Watcher"
    } else {
        Write-Log "Todos los tests pasaron correctamente." "Success" "Watcher"
    }
}

Write-Log "Vigilando '$watchPath' (debounce ${DebounceMillis}ms). Ctrl+C para parar." "Success" "Watcher"

try {
    $lastRun = [DateTime]::MinValue
    while ($true) {
        Start-Sleep -Milliseconds 200

        $latestName = $null
        $item = $null
        while ($queue.TryDequeue([ref]$item)) {
            $latestName = $item
        }

        if ($null -ne $latestName) {
            $now = Get-Date
            if (($now - $lastRun).TotalMilliseconds -ge $DebounceMillis) {
                $lastRun = $now
                Invoke-TestCycle -Trigger $latestName
            }
        }
    }
}
finally {
    foreach ($s in $subscriptions) { Unregister-Event -SubscriptionId $s.Id -ErrorAction SilentlyContinue }
    $watcher.Dispose()
    Write-Log "Watcher detenido." "Info" "Watcher"
}

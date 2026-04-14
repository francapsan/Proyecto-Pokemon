. (Join-Path $PSScriptRoot 'test-helpers.ps1')
$watcher = New-Object System.IO.FileSystemWatcher -Property @{
    Path = Join-Path $PSScriptRoot 'src/main/java/com/pokemon'; Filter = '*.java'; IncludeSubdirectories = $true; EnableRaisingEvents = $true
}

$action = { 
    Write-Log "Cambio detectado en $($EventArgs.Name)" "Info" "Watcher"
    mvn clean compile -q
    powershell -File (Join-Path $PSScriptRoot 'merge-tests.ps1')
}

Register-ObjectEvent $watcher Changed -Action $action
Register-ObjectEvent $watcher Created -Action $action
Write-Log "Vigilando cambios... Ctrl+C para parar" "Success" "Watcher"
while($true) { Start-Sleep 1 }
```"
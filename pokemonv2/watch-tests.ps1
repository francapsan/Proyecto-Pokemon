# Watch Java source files and run test generation automatically on changes.
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File .\watch-tests.ps1

$sourceDir = Join-Path $PSScriptRoot 'src/main/java/com/pokemon'
$scriptToRun = Join-Path $PSScriptRoot 'merge-tests.ps1'
$debounceMs = 1000
$script:timer = $null
$script:running = $false

function Run-GenerateTests {
    if ($script:running) { return }
    $script:running = $true
    try {
        Write-Host "[watch-tests] Generating tests for project changes..." -ForegroundColor Cyan
        Push-Location $PSScriptRoot
        try {
            & mvn clean compile
            if ($LASTEXITCODE -ne 0) {
                Write-Host "[watch-tests] Maven compile failed with exit code $LASTEXITCODE." -ForegroundColor Red
                return
            }
            & powershell -NoProfile -ExecutionPolicy Bypass -File $scriptToRun
            if ($LASTEXITCODE -ne 0) {
                Write-Host "[watch-tests] merge-tests.ps1 failed with exit code $LASTEXITCODE." -ForegroundColor Red
                return
            }
            Write-Host "[watch-tests] Test generation finished." -ForegroundColor Green
        } finally {
            Pop-Location
        }
    } catch {
        Write-Host "[watch-tests] Error: $_" -ForegroundColor Red
    } finally {
        $script:running = $false
    }
}

function Debounced-Run {
    if ($script:timer) {
        $script:timer.Stop()
        $script:timer.Dispose()
        $script:timer = $null
    }

    $script:timer = New-Object System.Timers.Timer($debounceMs)
    $script:timer.AutoReset = $false
    $script:timer.Add_Elapsed({
        $script:timer.Stop()
        $script:timer.Dispose()
        $script:timer = $null
        Run-GenerateTests
    })
    $script:timer.Start()
}

if (-not (Test-Path $sourceDir)) {
    Write-Host "[watch-tests] Source directory not found: $sourceDir" -ForegroundColor Red
    exit 1
}

$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = $sourceDir
$watcher.Filter = '*.java'
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

$action = {
    param($sender, $e)
    Write-Host "[watch-tests] Detected $($e.ChangeType): $($e.FullPath)" -ForegroundColor Yellow
    Debounced-Run
}

Register-ObjectEvent $watcher Changed -Action $action | Out-Null
Register-ObjectEvent $watcher Created -Action $action | Out-Null
Register-ObjectEvent $watcher Renamed -Action $action | Out-Null

Write-Host "[watch-tests] Watching Java files under $sourceDir... Press Ctrl+C to stop." -ForegroundColor Green
while ($true) {
    Start-Sleep -Seconds 1
}

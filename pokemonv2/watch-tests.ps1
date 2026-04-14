# Watch Java source files and run test generation automatically on changes.
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File .\watch-tests.ps1

# Source helper functions
. (Join-Path $PSScriptRoot 'test-helpers.ps1')

# Configuration
$sourceDir = Join-Path $PSScriptRoot 'src/main/java/com/pokemon'
$scriptToRun = Join-Path $PSScriptRoot 'merge-tests.ps1'
$debounceMs = 1000
$script:timer = $null
$script:running = $false
$Prefix = "watch-tests"

# Run test generation
function Run-GenerateTests {
    if ($script:running) {
        Write-Log "Test generation already in progress, skipping..." "Warning" $Prefix
        return
    }
    
    $script:running = $true
    try {
        Write-Log "Generating tests for project changes..." "Info" $Prefix
        Push-Location $PSScriptRoot
        try {
            & mvn clean compile -q
            if ($LASTEXITCODE -ne 0) {
                Write-Log "Maven compile failed with exit code $LASTEXITCODE" "Error" $Prefix
                return
            }
            
            & powershell -NoProfile -ExecutionPolicy Bypass -File $scriptToRun
            if ($LASTEXITCODE -ne 0) {
                Write-Log "merge-tests.ps1 failed with exit code $LASTEXITCODE" "Error" $Prefix
                return
            }
            
            Write-Log "Test generation finished successfully" "Success" $Prefix
        } finally {
            Pop-Location
        }
    } catch {
        Write-Log "Error: $_" "Error" $Prefix
    } finally {
        $script:running = $false
    }
}

# Debounce function to prevent excessive runs
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

# Validate source directory
if (-not (Test-Path $sourceDir)) {
    Write-Log "Source directory not found: $sourceDir" "Error" $Prefix
    exit 1
}

# Validate merge-tests.ps1 script
if (-not (Test-Path $scriptToRun)) {
    Write-Log "merge-tests.ps1 not found at $scriptToRun" "Error" $Prefix
    exit 1
}

# Set up file system watcher
$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = $sourceDir
$watcher.Filter = '*.java'
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

# Handle file changes
$action = {
    param($sender, $e)
    Write-Log "Detected $($e.ChangeType): $($e.Name)" "Info" $Prefix
    Debounced-Run
}

# Register event handlers
Register-ObjectEvent $watcher Changed -Action $action | Out-Null
Register-ObjectEvent $watcher Created -Action $action | Out-Null
Register-ObjectEvent $watcher Renamed -Action $action | Out-Null

# Start watching
Write-Log "Watching Java files under $sourceDir... Press Ctrl+C to stop" "Success" $Prefix
try {
    while ($true) {
        Start-Sleep -Seconds 1
    }
} finally {
    $watcher.EnableRaisingEvents = $false
    $watcher.Dispose()
    if ($script:timer) {
        $script:timer.Stop()
        $script:timer.Dispose()
    }
    Write-Log "Watch script terminated" "Info" $Prefix
}
# Watch Java source files and run test generation automatically on changes.
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File .\watch-tests.ps1

# Source helper functions
. (Join-Path $PSScriptRoot 'test-helpers.ps1')

# Configuration
$sourceDir = Join-Path $PSScriptRoot 'src/main/java/com/pokemon'
$scriptToRun = Join-Path $PSScriptRoot 'merge-tests.ps1'
$debounceMs = 1000
$script:timer = $null
$script:running = $false
$Prefix = "watch-tests"

# Run test generation
function Run-GenerateTests {
    if ($script:running) {
        Write-Log "Test generation already in progress, skipping..." "Warning" $Prefix
        return
    }
    
    $script:running = $true
    try {
        Write-Log "Generating tests for project changes..." "Info" $Prefix
        Push-Location $PSScriptRoot
        try {
            & mvn clean compile -q
            if ($LASTEXITCODE -ne 0) {
                Write-Log "Maven compile failed with exit code $LASTEXITCODE" "Error" $Prefix
                return
            }
            
            & powershell -NoProfile -ExecutionPolicy Bypass -File $scriptToRun
            if ($LASTEXITCODE -ne 0) {
                Write-Log "merge-tests.ps1 failed with exit code $LASTEXITCODE" "Error" $Prefix
                return
            }
            
            Write-Log "Test generation finished successfully" "Success" $Prefix
        } finally {
            Pop-Location
        }
    } catch {
        Write-Log "Error: $_" "Error" $Prefix
    } finally {
        $script:running = $false
    }
}

# Debounce function to prevent excessive runs
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

# Validate source directory
if (-not (Test-Path $sourceDir)) {
    Write-Log "Source directory not found: $sourceDir" "Error" $Prefix
    exit 1
}

# Validate merge-tests.ps1 script
if (-not (Test-Path $scriptToRun)) {
    Write-Log "merge-tests.ps1 not found at $scriptToRun" "Error" $Prefix
    exit 1
}

# Set up file system watcher
$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = $sourceDir
$watcher.Filter = '*.java'
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

# Handle file changes
$action = {
    param($sender, $e)
    Write-Log "Detected $($e.ChangeType): $($e.Name)" "Info" $Prefix
    Debounced-Run
}

# Register event handlers
Register-ObjectEvent $watcher Changed -Action $action | Out-Null
Register-ObjectEvent $watcher Created -Action $action | Out-Null
Register-ObjectEvent $watcher Renamed -Action $action | Out-Null

# Start watching
Write-Log "Watching Java files under $sourceDir... Press Ctrl+C to stop" "Success" $Prefix
try {
    while ($true) {
        Start-Sleep -Seconds 1
    }
} finally {
    $watcher.EnableRaisingEvents = $false
    $watcher.Dispose()
    if ($script:timer) {
        $script:timer.Stop()
        $script:timer.Dispose()
    }
    Write-Log "Watch script terminated" "Info" $Prefix
}

# PowerShell test helper functions for EvoSuite test generation

# Get changed Java files from git (including untracked files)
function Get-ChangedJavaFiles {
    param(
        [string]$SourcePath = "src/main/java/com/pokemon"
    )
    
    $changedFiles = git diff --diff-filter=ACMRT HEAD --name-only -- $SourcePath 2>$null
    $untrackedFiles = git ls-files --others --exclude-standard -- $SourcePath 2>$null
    
    $allFiles = @()
    if ($changedFiles) { $allFiles += $changedFiles }
    if ($untrackedFiles) { $allFiles += $untrackedFiles }
    
    return $allFiles | Where-Object { $_ -like '*.java' } | Select-Object -Unique
}

# Extract class name from file path
function Get-ClassNameFromPath {
    param(
        [string]$FilePath
    )
    
    $className = [System.IO.Path]::GetFileNameWithoutExtension($FilePath)
    return "com.pokemon.$className"
}

# Extract test methods from generated test file
function Get-TestMethods {
    param(
        [string]$TestFileContent
    )
    
    $pattern = '@Test[\s\S]*?(?=\s+@Test|\s+public\s+class|\s*}\s*$)'
    $matches = [regex]::Matches($testFileContent, $pattern) | ForEach-Object { $_.Value.Trim() }
    
    return $matches | Where-Object { $_ -match '@Test' }
}

# Merge test methods into AppTest.java
function Merge-TestsIntoAppTest {
    param(
        [string[]]$TestMethods,
        [string]$AppTestPath = "src/test/java/com/pokemon/AppTest.java"
    )
    
    if ($TestMethods.Count -eq 0) {
        return $false
    }
    
    if (-not (Test-Path $AppTestPath)) {
        Write-Host "Error: AppTest.java not found at $AppTestPath" -ForegroundColor Red
        return $false
    }
    
    $appTestContent = Get-Content $AppTestPath -Raw
    
    # Remove the closing brace
    $appTestContent = $appTestContent -replace '\}\s*$', ''
    
    # Add new tests
    foreach ($method in $TestMethods) {
        $appTestContent += "`n`n    $method"
    }
    
    # Add closing brace
    $appTestContent += "`n}"
    
    # Write back
    Set-Content $AppTestPath $appTestContent
    
    return $true
}

# Log message with timestamp
function Write-Log {
    param(
        [string]$Message,
        [ValidateSet("Info", "Warning", "Error", "Success")]
        [string]$Level = "Info",
        [string]$Prefix = ""
    )
    
    $timestamp = Get-Date -Format "HH:mm:ss"
    $colors = @{
        "Info"    = "Cyan"
        "Warning" = "Yellow"
        "Error"   = "Red"
        "Success" = "Green"
    }
    
    $color = $colors[$Level]
    $prefixStr = if ($Prefix) { "[$Prefix] " } else { "" }
    
    Write-Host "$prefixStr$timestamp - $Message" -ForegroundColor $color
}

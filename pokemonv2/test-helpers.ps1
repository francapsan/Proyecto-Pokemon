function Get-ChangedJavaFiles {
    param([string]$SourcePath = "src/main/java/com/pokemon")
    $changed = git diff --diff-filter=ACMRT HEAD --name-only -- $SourcePath 2>$null
    $untracked = git ls-files --others --exclude-standard -- $SourcePath 2>$null
    return ($changed + $untracked) | Where-Object { $_ -like '*.java' } | Select-Object -Unique
}

function Get-ClassNameFromPath {
    param([string]$FilePath)
    return "com.pokemon.$([System.IO.Path]::GetFileNameWithoutExtension($FilePath))"
}

function Get-TestMethods {
    param([string]$Content)
    return [regex]::Matches($Content, '@Test[\s\S]*?(?=\s+@Test|\s+public\s+class|\s*}\s*$)') | ForEach-Object { $_.Value.Trim() } | Where-Object { $_ -match '@Test' }
}

function Merge-Tests {
    param([string[]]$Methods, [string]$Path = "src/test/java/com/pokemon/EvoSuiteTest.java")
    if ($Methods.Count -eq 0) { return $false }
    
    # Crear archivo si no existe con estructura básica
    if (-not (Test-Path $Path)) {
        'package com.pokemon;' + "`n" + 'import org.junit.Test;' + "`n" + 'import static org.junit.Assert.*;' + "`n`n" + 'public class EvoSuiteTest {' + "`n" + '}' | Set-Content $Path
    }

    $content = Get-Content $Path -Raw
    $content = $content -replace '\}\s*$', ''

    foreach ($m in $Methods) {
        if ($m -match 'public\s+void\s+(\w+)') {
            $name = $matches[1]
            if ($content -notmatch $name) { $content += "`n`n    $m" }
        }
    }
    $content += "`n}"
    Set-Content $Path $content
    return $true
}

function Write-Log {
    param([string]$Msg, [string]$Level = "Info", [string]$Prefix = "")
    $colors = @{"Info"="Cyan";"Warning"="Yellow";"Error"="Red";"Success"="Green"}
    Write-Host "[$Prefix] $(Get-Date -Format 'HH:mm:ss') - $Msg" -ForegroundColor $colors[$Level]
}
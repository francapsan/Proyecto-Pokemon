# PowerShell script to generate and merge EvoSuite tests for changed classes

# Source helper functions
. (Join-Path $PSScriptRoot 'test-helpers.ps1')

# Configuration
$SourcePath = "src/main/java/com/pokemon"
$TestsDir = "evosuite-tests"
$Prefix = "merge-tests"

# Get changed Java files
$changedFiles = Get-ChangedJavaFiles -SourcePath $SourcePath

if ($changedFiles.Count -eq 0) {
    Write-Log "No changed or new Java files detected" "Info" $Prefix
    exit 0
}

Write-Log "Found $($changedFiles.Count) changed file(s)" "Info" $Prefix

foreach ($file in $changedFiles) {
    $fullClassName = Get-ClassNameFromPath -FilePath $file
    $className = [System.IO.Path]::GetFileNameWithoutExtension($file)
    
    Write-Log "Generating tests for $fullClassName" "Info" $Prefix
    
    try {
        # Run EvoSuite Maven plugin
        & mvn evosuite:generate -DtargetClass=$fullClassName -q
        
        if ($LASTEXITCODE -ne 0) {
            Write-Log "Maven EvoSuite generation failed for $fullClassName (exit code: $LASTEXITCODE)" "Warning" $Prefix
            continue
        }
        
        # Find the generated test file
        $testFile = Join-Path $TestsDir "com/pokemon/${className}_ESTest.java"
        
        if (-not (Test-Path $testFile)) {
            Write-Log "No test file generated for $className" "Warning" $Prefix
            continue
        }
        
        # Read and extract test methods
        $testContent = Get-Content $testFile -Raw
        $testMethods = Get-TestMethods -TestFileContent $testContent
        
        if ($testMethods.Count -eq 0) {
            Write-Log "No test methods found in generated file for $className" "Warning" $Prefix
            continue
        }
        
        # Merge tests into AppTest.java
        if (Merge-TestsIntoAppTest -TestMethods $testMethods) {
            Write-Log "Added $($testMethods.Count) test(s) for $className to AppTest.java" "Success" $Prefix
        } else {
            Write-Log "Failed to merge tests for $className" "Error" $Prefix
        }
        
    } catch {
        Write-Log "Exception while processing $fullClassName : $_" "Error" $Prefix
    }
}

# Clean up evosuite-tests folder
if (Test-Path $TestsDir) {
    Write-Log "Cleaning up $TestsDir directory" "Info" $Prefix
    Remove-Item $TestsDir -Recurse -Force
}

Write-Log "Test generation and merge completed" "Success" $Prefix
# PowerShell script to generate and merge EvoSuite tests for changed classes

# Get changed Java files from git since last commit, including untracked files
$changedFiles = git diff --diff-filter=ACMRT HEAD --name-only -- src/main/java/com/pokemon
$untrackedFiles = git ls-files --others --exclude-standard -- src/main/java/com/pokemon
$allFiles = @()
if ($changedFiles) { $allFiles += $changedFiles }
if ($untrackedFiles) { $allFiles += $untrackedFiles }
$changedFiles = $allFiles | Where-Object { $_ -like '*.java' } | Select-Object -Unique

if ($changedFiles.Count -gt 0) {
    foreach ($file in $changedFiles) {
        # Extract class name from path
        $className = [System.IO.Path]::GetFileNameWithoutExtension($file)
        $fullClassName = "com.pokemon.$className"

        Write-Host "Generating tests for $fullClassName"

        # Run EvoSuite Maven plugin for this class
        mvn evosuite:generate -DtargetClass=$fullClassName -q

        # Find the generated test file
        $testFile = "evosuite-tests/com/pokemon/${className}_ESTest.java"
        if (Test-Path $testFile) {
            # Read the generated test file
            $testContent = Get-Content $testFile -Raw

            # Extract test methods using regex
            $testMethods = [regex]::Matches($testContent, '@Test[\s\S]*?(?=\s+@Test|\s+public\s+class|\s*}\s*$)') | ForEach-Object { $_.Value.Trim() }

            # Append to AppTest.java
            $appTestFile = "src/test/java/com/pokemon/AppTest.java"
            $appTestContent = Get-Content $appTestFile -Raw

            # Remove the closing brace
            $appTestContent = $appTestContent -replace '\}\s*$', ''

            # Add new tests
            foreach ($method in $testMethods) {
                if ($method -match '@Test') {
                    $appTestContent += "`n`n    $method"
                }
            }

            # Add closing brace
            $appTestContent += "`n}"

            # Write back
            Set-Content $appTestFile $appTestContent

            Write-Host "Added tests for $className to AppTest.java"
        } else {
            Write-Host "No test file generated for $className"
        }
    }

    # Clean up evosuite-tests folder
    if (Test-Path "evosuite-tests") {
        Remove-Item "evosuite-tests" -Recurse -Force
    }
} else {
    Write-Host "No changed or new Java files detected."
}
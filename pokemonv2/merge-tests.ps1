# PowerShell script to generate and merge EvoSuite tests for changed classes

# Get changed Java files from git since last commit
$changedFiles = git diff HEAD --name-only src/main/java/com/pokemon/*.java

if ($changedFiles) {
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
    Write-Host "No changes detected in source files."
}
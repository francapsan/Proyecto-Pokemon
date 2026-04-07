# PowerShell script to generate tests for changed classes using EvoSuite

# Get changed Java files from git since last commit
$changedFiles = git diff HEAD --name-only src/main/java/com/pokemon/*.java

if ($changedFiles) {
    foreach ($file in $changedFiles) {
        # Extract class name from path
        $className = [System.IO.Path]::GetFileNameWithoutExtension($file)
        $fullClassName = "com.pokemon.$className"

        Write-Host "Generating tests for $fullClassName"

        # Run EvoSuite to generate tests (assuming evosuite.jar is in the project root or PATH)
        # Download EvoSuite standalone from https://github.com/EvoSuite/evosuite/releases
        # Place evosuite.jar in the project root
        java -jar evosuite.jar -class $fullClassName -projectCP target/classes

        # Find the generated test file
        $testFile = "evosuite-tests/com/pokemon/${className}_ESTest.java"
        if (Test-Path $testFile) {
            # Read the generated test file
            $testContent = Get-Content $testFile -Raw

            # Extract test methods using regex (improved)
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
} else {
    Write-Host "No changed files detected."
}
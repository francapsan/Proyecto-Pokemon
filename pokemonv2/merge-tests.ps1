. (Join-Path $PSScriptRoot 'test-helpers.ps1')
$changed = Get-ChangedJavaFiles
if ($changed.Count -eq 0) { Write-Log "Sin cambios detectados" "Info" "Merge"; exit 0 }

foreach ($file in $changed) {
    $fullClass = Get-ClassNameFromPath $file
    Write-Log "Generando para $fullClass" "Info" "Merge"
    mvn evosuite:generate -DtargetClass=$fullClass -q
    
    $genFile = "evosuite-tests/com/pokemon/$([System.IO.Path]::GetFileNameWithoutExtension($file))_ESTest.java"
    if (Test-Path $genFile) {
        $methods = Get-TestMethods (Get-Content $genFile -Raw)
        if (Merge-Tests -Methods $methods) { Write-Log "Tests sincronizados para $fullClass" "Success" "Merge" }
    }
}
if (Test-Path "evosuite-tests") { Remove-Item "evosuite-tests" -Recurse -Force }
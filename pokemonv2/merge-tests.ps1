. (Join-Path $PSScriptRoot 'test-helpers.ps1')
$changed = Get-ChangedJavaFiles
if ($changed.Count -eq 0) { Write-Log "Sin cambios detectados" "Info" "Merge"; exit 0 }

$classNames = $changed | ForEach-Object { Get-ClassNameFromPath $_ }
$targetClasses = $classNames -join ","
Write-Log "Generando tests para: $($classNames -join ', ')" "Info" "Merge"
mvn evosuite:generate -DtargetClass=$targetClasses -q # 2>&1 | Out-Null # Puedes quitar -q para ver más detalles si hay errores
if ($LASTEXITCODE -ne 0) {
    Write-Log "EvoSuite falló al generar tests para $targetClasses. Asegúrate de que las clases son compilables y válidas." "Error" "Merge"
    exit 1
}

foreach ($file in $changed) {
    $genFile = "evosuite-tests/com/pokemon/$([System.IO.Path]::GetFileNameWithoutExtension($file))_ESTest.java"
    if (Test-Path $genFile) {
        $methods = Get-TestMethods (Get-Content $genFile -Raw)
        if (Merge-Tests -Methods $methods) { 
            Write-Log "Tests sincronizados para $(Get-ClassNameFromPath $file)" "Success" "Merge" 
        }
    }
}
if (Test-Path "evosuite-tests") { Remove-Item "evosuite-tests" -Recurse -Force }
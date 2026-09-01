#requires -Version 5.1

<#
.SYNOPSIS
    Utilidades PowerShell compartidas por los scripts del proyecto.
#>

function Write-Log {
    param(
        [Parameter(Mandatory)][string]$Msg,
        [ValidateSet('Info', 'Warning', 'Error', 'Success')][string]$Level = 'Info',
        [string]$Prefix = ''
    )
    $colors = @{ 'Info' = 'Cyan'; 'Warning' = 'Yellow'; 'Error' = 'Red'; 'Success' = 'Green' }
    Write-Host ("[{0}] {1} - {2}" -f $Prefix, (Get-Date -Format 'HH:mm:ss'), $Msg) -ForegroundColor $colors[$Level]
}

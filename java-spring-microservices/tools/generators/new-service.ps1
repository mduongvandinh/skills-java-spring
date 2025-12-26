<#
.SYNOPSIS
    Creates a new microservice from template

.DESCRIPTION
    Generates a new microservice with proper structure and configurations

.PARAMETER Name
    Name of the service (e.g., "user", "order", "payment")

.PARAMETER Type
    Type of service: core, event, gateway, bff

.PARAMETER Port
    Port number for the service (default: auto-assign)

.EXAMPLE
    .\new-service.ps1 -Name "user" -Type "core"
    .\new-service.ps1 -Name "notification" -Type "event" -Port 8084
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$Name,

    [Parameter(Mandatory=$true)]
    [ValidateSet("core", "event", "gateway", "bff")]
    [string]$Type,

    [int]$Port = 0
)

$ErrorActionPreference = "Stop"

# Configuration
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $ScriptDir)
$TemplateDir = Join-Path $ScriptDir "templates\$Type-service"
$TargetDir = Join-Path $ProjectRoot "services\$Name-service"

# Convert name to PascalCase
$NamePascal = ($Name -split '-' | ForEach-Object { $_.Substring(0,1).ToUpper() + $_.Substring(1) }) -join ''

# Auto-assign port if not specified
if ($Port -eq 0) {
    $ExistingPorts = Get-ChildItem "$ProjectRoot\services\*\src\main\resources\application.yml" -ErrorAction SilentlyContinue |
        ForEach-Object {
            $content = Get-Content $_ -Raw
            if ($content -match 'port:\s*(\d+)') { [int]$Matches[1] }
        } | Sort-Object -Descending

    $Port = if ($ExistingPorts) { $ExistingPorts[0] + 1 } else { 8081 }
}

Write-Host "Creating $Name-service from $Type template..." -ForegroundColor Cyan
Write-Host "  Target: $TargetDir" -ForegroundColor Gray
Write-Host "  Port: $Port" -ForegroundColor Gray

# Check if template exists
if (-not (Test-Path $TemplateDir)) {
    Write-Error "Template not found: $TemplateDir"
    exit 1
}

# Check if service already exists
if (Test-Path $TargetDir) {
    Write-Error "Service already exists: $TargetDir"
    exit 1
}

# Copy template
Write-Host "Copying template..." -ForegroundColor Yellow
Copy-Item -Path $TemplateDir -Destination $TargetDir -Recurse

# Replace placeholders in files
Write-Host "Replacing placeholders..." -ForegroundColor Yellow
$filesToProcess = Get-ChildItem -Path $TargetDir -Recurse -File

foreach ($file in $filesToProcess) {
    $content = Get-Content $file.FullName -Raw -ErrorAction SilentlyContinue
    if ($content) {
        $content = $content -replace '\{\{SERVICE_NAME\}\}', $Name
        $content = $content -replace '\{\{SERVICE_NAME_PASCAL\}\}', $NamePascal
        $content = $content -replace '808X', $Port
        Set-Content -Path $file.FullName -Value $content -NoNewline
    }
}

# Rename package directories
$oldPackage = Join-Path $TargetDir "src\main\java\com\company\template"
$newPackage = Join-Path $TargetDir "src\main\java\com\company\$Name"
if (Test-Path $oldPackage) {
    Rename-Item -Path $oldPackage -NewName $Name
}

# Rename Application class
$oldAppClass = Join-Path $newPackage "TemplateServiceApplication.java"
$newAppClass = Join-Path $newPackage "${NamePascal}ServiceApplication.java"
if (Test-Path $oldAppClass) {
    Rename-Item -Path $oldAppClass -NewName "${NamePascal}ServiceApplication.java"
}

# Update parent pom.xml to include new module
$parentPom = Join-Path $ProjectRoot "pom.xml"
$parentContent = Get-Content $parentPom -Raw
if ($parentContent -notmatch "services/$Name-service") {
    $moduleEntry = "        <module>services/$Name-service</module>`n    </modules>"
    $parentContent = $parentContent -replace '</modules>', $moduleEntry
    Set-Content -Path $parentPom -Value $parentContent -NoNewline
    Write-Host "Added module to parent pom.xml" -ForegroundColor Green
}

Write-Host ""
Write-Host "Service created successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "  1. cd services\$Name-service"
Write-Host "  2. Review and update pom.xml dependencies"
Write-Host "  3. Create database: $Name`_db"
Write-Host "  4. Run: mvn spring-boot:run"
Write-Host ""

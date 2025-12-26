# =============================================================================
# Start Local Development Environment (Windows PowerShell)
# =============================================================================
# Usage:
#   .\scripts\start-local.ps1                    # Start with PostgreSQL only
#   $env:ENABLE_REDIS="true"; .\scripts\start-local.ps1   # With Redis
#   $env:ENABLE_KAFKA="true"; .\scripts\start-local.ps1   # With Kafka
# =============================================================================

param(
    [switch]$NoDocker,
    [switch]$Redis,
    [switch]$Kafka,
    [switch]$RabbitMQ,
    [switch]$Full
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Spring Boot Local Development Starter" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is available
$dockerAvailable = $null -ne (Get-Command docker -ErrorAction SilentlyContinue)

if (-not $NoDocker -and $dockerAvailable) {
    Write-Host "[1/3] Starting infrastructure with Docker..." -ForegroundColor Yellow

    # Always start PostgreSQL
    docker compose up -d postgres

    # Check environment variables or flags
    if ($Redis -or $env:ENABLE_REDIS -eq "true" -or $Full) {
        Write-Host "      Starting Redis..." -ForegroundColor Gray
        docker compose --profile with-redis up -d
    }

    if ($Kafka -or $env:ENABLE_KAFKA -eq "true" -or $Full) {
        Write-Host "      Starting Kafka..." -ForegroundColor Gray
        docker compose --profile with-kafka up -d
    }

    if ($RabbitMQ -or $env:ENABLE_RABBITMQ -eq "true") {
        Write-Host "      Starting RabbitMQ..." -ForegroundColor Gray
        docker compose --profile with-rabbitmq up -d
    }

    Write-Host "[2/3] Waiting for services to be ready..." -ForegroundColor Yellow
    Start-Sleep -Seconds 5

} else {
    if (-not $dockerAvailable) {
        Write-Host "[!] Docker not found. Make sure services are running locally." -ForegroundColor Red
    }
    Write-Host "[1/3] Skipping Docker (NoDocker mode)..." -ForegroundColor Yellow
    Write-Host "[2/3] Assuming local services are running..." -ForegroundColor Yellow
}

# Build module arguments
$moduleArgs = @()
if ($Redis -or $env:ENABLE_REDIS -eq "true" -or $Full) {
    $moduleArgs += "--modules.redis.enabled=true"
}
if ($Kafka -or $env:ENABLE_KAFKA -eq "true" -or $Full) {
    $moduleArgs += "--modules.kafka.enabled=true"
}
if ($RabbitMQ -or $env:ENABLE_RABBITMQ -eq "true") {
    $moduleArgs += "--modules.rabbitmq.enabled=true"
}

Write-Host "[3/3] Starting Spring Boot application..." -ForegroundColor Yellow
Write-Host ""

if ($moduleArgs.Count -gt 0) {
    $argsString = $moduleArgs -join " "
    Write-Host "      Enabled modules: $argsString" -ForegroundColor Gray
    mvn spring-boot:run "-Dspring-boot.run.profiles=local" "-Dspring-boot.run.arguments=$argsString"
} else {
    mvn spring-boot:run "-Dspring-boot.run.profiles=local"
}

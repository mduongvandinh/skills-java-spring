#!/bin/bash
# =============================================================================
# Start Local Development Environment (Linux/macOS)
# =============================================================================
# Usage:
#   ./scripts/start-local.sh                     # Start with PostgreSQL only
#   ENABLE_REDIS=true ./scripts/start-local.sh   # With Redis
#   ENABLE_KAFKA=true ./scripts/start-local.sh   # With Kafka
#   ./scripts/start-local.sh --full              # All modules
#   ./scripts/start-local.sh --no-docker         # Without Docker
# =============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Parse arguments
NO_DOCKER=false
FULL=false
while [[ $# -gt 0 ]]; do
    case $1 in
        --no-docker)
            NO_DOCKER=true
            shift
            ;;
        --full)
            FULL=true
            ENABLE_REDIS=true
            ENABLE_KAFKA=true
            shift
            ;;
        --redis)
            ENABLE_REDIS=true
            shift
            ;;
        --kafka)
            ENABLE_KAFKA=true
            shift
            ;;
        --rabbitmq)
            ENABLE_RABBITMQ=true
            shift
            ;;
        *)
            shift
            ;;
    esac
done

echo -e "${CYAN}========================================"
echo -e " Spring Boot Local Development Starter"
echo -e "========================================${NC}"
echo ""

# Check if Docker is available
if command -v docker &> /dev/null && [ "$NO_DOCKER" = false ]; then
    echo -e "${YELLOW}[1/3] Starting infrastructure with Docker...${NC}"

    # Always start PostgreSQL
    docker compose up -d postgres

    # Optional services
    if [ "$ENABLE_REDIS" = "true" ]; then
        echo -e "      Starting Redis..."
        docker compose --profile with-redis up -d
    fi

    if [ "$ENABLE_KAFKA" = "true" ]; then
        echo -e "      Starting Kafka..."
        docker compose --profile with-kafka up -d
    fi

    if [ "$ENABLE_RABBITMQ" = "true" ]; then
        echo -e "      Starting RabbitMQ..."
        docker compose --profile with-rabbitmq up -d
    fi

    echo -e "${YELLOW}[2/3] Waiting for services to be ready...${NC}"
    sleep 5

else
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}[!] Docker not found. Make sure services are running locally.${NC}"
    fi
    echo -e "${YELLOW}[1/3] Skipping Docker (no-docker mode)...${NC}"
    echo -e "${YELLOW}[2/3] Assuming local services are running...${NC}"
fi

# Build module arguments
MODULE_ARGS=""
if [ "$ENABLE_REDIS" = "true" ]; then
    MODULE_ARGS="$MODULE_ARGS --modules.redis.enabled=true"
fi
if [ "$ENABLE_KAFKA" = "true" ]; then
    MODULE_ARGS="$MODULE_ARGS --modules.kafka.enabled=true"
fi
if [ "$ENABLE_RABBITMQ" = "true" ]; then
    MODULE_ARGS="$MODULE_ARGS --modules.rabbitmq.enabled=true"
fi

echo -e "${YELLOW}[3/3] Starting Spring Boot application...${NC}"
echo ""

if [ -n "$MODULE_ARGS" ]; then
    echo -e "      Enabled modules:$MODULE_ARGS"
    mvn spring-boot:run -Dspring-boot.run.profiles=local \
        -Dspring-boot.run.arguments="$MODULE_ARGS"
else
    mvn spring-boot:run -Dspring-boot.run.profiles=local
fi

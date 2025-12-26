# =============================================================================
# Makefile for Spring Boot Application
# =============================================================================
# Usage:
#   make dev          - Run without Docker (services installed locally)
#   make dev-docker   - Run with Docker infrastructure
#   make dev-redis    - Run with Redis enabled
#   make dev-kafka    - Run with Kafka enabled
#   make dev-full     - Run with all modules enabled
#   make test         - Run all tests
#   make build        - Build the application
#   make clean        - Clean up
# =============================================================================

.PHONY: dev dev-docker dev-redis dev-kafka dev-rabbitmq dev-full test build clean docker-up docker-down help

# Default target
.DEFAULT_GOAL := help

# =============================================================================
# Development Commands
# =============================================================================

## Run without Docker (requires services installed locally)
dev:
	mvn spring-boot:run -Dspring-boot.run.profiles=local

## Run with Docker infrastructure only (PostgreSQL)
dev-docker:
	docker compose up -d postgres
	@echo "Waiting for PostgreSQL to be ready..."
	@sleep 5
	mvn spring-boot:run -Dspring-boot.run.profiles=local

## Run with Redis enabled
dev-redis:
	docker compose up -d postgres
	docker compose --profile with-redis up -d
	@echo "Waiting for services to be ready..."
	@sleep 5
	mvn spring-boot:run -Dspring-boot.run.profiles=local \
		-Dspring-boot.run.arguments="--modules.redis.enabled=true"

## Run with Kafka enabled
dev-kafka:
	docker compose up -d postgres
	docker compose --profile with-kafka up -d
	@echo "Waiting for services to be ready..."
	@sleep 10
	mvn spring-boot:run -Dspring-boot.run.profiles=local \
		-Dspring-boot.run.arguments="--modules.kafka.enabled=true"

## Run with RabbitMQ enabled
dev-rabbitmq:
	docker compose up -d postgres
	docker compose --profile with-rabbitmq up -d
	@echo "Waiting for services to be ready..."
	@sleep 5
	mvn spring-boot:run -Dspring-boot.run.profiles=local \
		-Dspring-boot.run.arguments="--modules.rabbitmq.enabled=true"

## Run with all modules enabled
dev-full:
	docker compose up -d postgres
	docker compose --profile with-redis --profile with-kafka up -d
	@echo "Waiting for services to be ready..."
	@sleep 10
	mvn spring-boot:run -Dspring-boot.run.profiles=local \
		-Dspring-boot.run.arguments="--modules.redis.enabled=true --modules.kafka.enabled=true"

# =============================================================================
# Docker Commands
# =============================================================================

## Start all Docker containers (infrastructure only)
docker-up:
	docker compose up -d postgres
	@echo "PostgreSQL is running on port 5432"

## Start with Redis
docker-up-redis:
	docker compose --profile with-redis up -d

## Start with Kafka
docker-up-kafka:
	docker compose --profile with-kafka up -d

## Start with RabbitMQ
docker-up-rabbitmq:
	docker compose --profile with-rabbitmq up -d

## Start full Docker deployment (including app)
docker-up-full:
	docker compose --profile with-redis --profile with-kafka --profile with-app up -d

## Stop all Docker containers
docker-down:
	docker compose --profile with-redis --profile with-kafka --profile with-rabbitmq --profile with-app down

## Stop and remove volumes
docker-clean:
	docker compose --profile with-redis --profile with-kafka --profile with-rabbitmq --profile with-app down -v

# =============================================================================
# Build Commands
# =============================================================================

## Run all tests
test:
	mvn test

## Run integration tests
test-integration:
	mvn verify -Pfull-stack

## Build the application (minimal profile)
build:
	mvn clean package -DskipTests -Pminimal

## Build with all modules
build-full:
	mvn clean package -DskipTests -Pfull-stack

## Build Docker image
docker-build:
	mvn clean package -DskipTests -Pfull-stack
	docker build -t myapp:latest .

# =============================================================================
# Database Commands
# =============================================================================

## Run Flyway migration
db-migrate:
	mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/myapp \
		-Dflyway.user=postgres -Dflyway.password=postgres

## Clean and migrate database
db-clean:
	mvn flyway:clean flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/myapp \
		-Dflyway.user=postgres -Dflyway.password=postgres

# =============================================================================
# Utility Commands
# =============================================================================

## Clean build artifacts
clean:
	mvn clean
	docker compose down -v 2>/dev/null || true

## Check module status
status:
	@echo "Checking Docker containers..."
	@docker compose ps
	@echo ""
	@echo "Checking application health..."
	@curl -s http://localhost:8080/actuator/health 2>/dev/null || echo "Application not running"

## View logs
logs:
	docker compose logs -f

## View application logs
logs-app:
	docker compose logs -f app

# =============================================================================
# Help
# =============================================================================

## Show this help message
help:
	@echo "Usage: make [target]"
	@echo ""
	@echo "Development:"
	@echo "  dev           Run without Docker (services installed locally)"
	@echo "  dev-docker    Run with Docker infrastructure (PostgreSQL)"
	@echo "  dev-redis     Run with Redis enabled"
	@echo "  dev-kafka     Run with Kafka enabled"
	@echo "  dev-rabbitmq  Run with RabbitMQ enabled"
	@echo "  dev-full      Run with all modules enabled"
	@echo ""
	@echo "Docker:"
	@echo "  docker-up          Start PostgreSQL"
	@echo "  docker-up-redis    Start with Redis"
	@echo "  docker-up-kafka    Start with Kafka"
	@echo "  docker-up-full     Start full stack"
	@echo "  docker-down        Stop all containers"
	@echo "  docker-clean       Stop and remove volumes"
	@echo ""
	@echo "Build:"
	@echo "  test           Run all tests"
	@echo "  build          Build minimal profile"
	@echo "  build-full     Build with all modules"
	@echo "  docker-build   Build Docker image"
	@echo ""
	@echo "Utility:"
	@echo "  clean          Clean build artifacts"
	@echo "  status         Check service status"
	@echo "  logs           View Docker logs"

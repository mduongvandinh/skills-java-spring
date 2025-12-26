# Java Spring Boot Skills

A modular Spring Boot application template with optional components and TDD/Spec-First development practices.

## Features

- **Modular Architecture**: Enable/disable components via Maven profiles
- **Database**: PostgreSQL with JPA/Hibernate + Flyway
- **Caching**: Redis (optional)
- **Messaging**: Kafka or RabbitMQ (optional)
- **Security**: JWT + OAuth2 (optional)
- **Testing**: TDD with Mockito + Testcontainers
- **Spec-First**: OpenSpec integration

## Quick Start

### Without Docker

```bash
# Ensure PostgreSQL is running locally
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### With Docker

```bash
# Start PostgreSQL
docker compose up -d postgres

# Run application
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Full Stack

```bash
# Start all services
docker compose --profile with-redis --profile with-kafka up -d

# Run with all modules
mvn spring-boot:run -Dspring-boot.run.profiles=local \
  -Dspring-boot.run.arguments="--modules.redis.enabled=true --modules.kafka.enabled=true"
```

## Module Selection

| Module | Enable Flag | Docker Profile |
|--------|-------------|----------------|
| Redis | `-Dmodule.redis.enabled=true` | `--profile with-redis` |
| Kafka | `-Dmodule.kafka.enabled=true` | `--profile with-kafka` |
| RabbitMQ | `-Dmodule.rabbitmq.enabled=true` | `--profile with-rabbitmq` |
| OAuth2 | `-Dmodule.oauth2.enabled=true` | N/A |

## Development

### Using Make

```bash
make dev          # Run without Docker
make dev-docker   # Run with Docker infrastructure
make dev-redis    # With Redis
make dev-kafka    # With Kafka
make dev-full     # All modules
make test         # Run tests
make clean        # Clean up
```

### Using Scripts

```bash
# Linux/macOS
./scripts/start-local.sh
./scripts/start-local.sh --redis
./scripts/start-local.sh --full

# Windows PowerShell
.\scripts\start-local.ps1
.\scripts\start-local.ps1 -Redis
.\scripts\start-local.ps1 -Full
```

## Project Structure

```
src/
├── main/
│   ├── java/com/company/app/
│   │   ├── config/           # Configuration
│   │   ├── controller/       # REST controllers
│   │   ├── service/          # Business logic
│   │   ├── repository/       # Data access
│   │   ├── domain/           # Entities
│   │   ├── dto/              # DTOs
│   │   └── exception/        # Exceptions
│   └── resources/
│       ├── application.yml
│       └── db/migration/     # Flyway
└── test/
    └── java/                 # Tests

openspec/
├── AGENTS.md                 # AI instructions
├── specs/                    # Specifications
└── changes/                  # Proposals
```

## Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /api/system/modules` | Module status |
| `GET /api/system/health` | Health check |
| `GET /actuator/health` | Actuator health |

## Testing

```bash
# All tests
mvn test

# Specific test
mvn test -Dtest=UserServiceTest

# Integration tests
mvn verify
```

## AI IDE Integration

This project includes configuration files for popular AI coding assistants:

| AI Tool | Config File | Auto-loaded |
|---------|-------------|-------------|
| Claude Code | `CLAUDE.md` | ✅ Yes |
| Cursor | `.cursorrules` | ✅ Yes |
| Windsurf | `.windsurfrules` | ✅ Yes |
| GitHub Copilot | `.github/copilot-instructions.md` | ✅ Yes |
| Continue.dev | `.continuerc.json` | ✅ Yes |

Just open the project in your preferred IDE and the AI will automatically follow the coding conventions.

## Documentation

- [QUICKSTART.md](QUICKSTART.md) - Get running in 5 minutes
- [SKILL.md](SKILL.md) - Main skill documentation
- [CLAUDE.md](CLAUDE.md) - Claude Code instructions
- [openspec/AGENTS.md](openspec/AGENTS.md) - OpenSpec AI instructions
- [skills/](skills/) - Individual skill definitions

## License

MIT

# AGENTS.md

This project follows [OpenSpec](https://github.com/Fission-AI/OpenSpec) for spec-driven development.

## Workflow

### Reading Specifications

Before implementing any feature, read the relevant specs:

```
openspec/specs/{domain}/spec.md
```

### Proposing Changes

1. Create a change proposal in `openspec/changes/{feature-name}/`
2. Add `proposal.md` with rationale
3. Add `tasks.md` with implementation checklist
4. Add `specs/` with delta specifications

### Implementation Rules

1. **Read spec first** - Understand requirements before coding
2. **Follow conventions** - Use patterns defined in specs
3. **Update specs** - If implementation differs, propose spec changes
4. **Test against spec** - Scenarios in specs are test cases

## Project Structure

```
java-spring-microservices/
├── openspec/
│   ├── specs/              # Current specifications
│   │   ├── services/       # Service specifications
│   │   ├── events/         # Event flow specifications
│   │   └── api/            # API contract specifications
│   └── changes/            # Proposed changes
├── services/               # Microservice implementations
├── shared/                 # Shared libraries
└── infrastructure/         # Docker, K8s configs
```

## Service Types

| Type | Spec Location | Template |
|------|---------------|----------|
| Core Service | `openspec/specs/services/core.md` | `tools/generators/templates/core-service` |
| Event Service | `openspec/specs/services/event.md` | `tools/generators/templates/event-service` |
| Gateway Service | `openspec/specs/services/gateway.md` | `tools/generators/templates/gateway-service` |
| BFF Service | `openspec/specs/services/bff.md` | `tools/generators/templates/bff-service` |

## Quick Commands

```powershell
# Create new service
.\tools\generators\new-service.ps1 -Name "{name}" -Type "{type}"

# Start infrastructure
cd infrastructure\docker && docker-compose up -d

# Build all
mvn clean package
```

## References

- Architecture: `docs/architecture/overview.md`
- Event Flows: `docs/architecture/event-flows.md`
- API Contracts: `shared/api-contracts/`

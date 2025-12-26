# AGENTS.md

This project follows [OpenSpec](https://github.com/Fission-AI/OpenSpec) for **spec-first development**.

> "Agree on WHAT to build BEFORE writing any code"

## Spec-First Workflow

```
┌─────────┐    ┌─────────────┐    ┌───────────┐    ┌──────┐    ┌─────────┐
│  Draft  │───▶│Review/Align │───▶│ Implement │───▶│ Ship │───▶│ Archive │
│proposal │    │  with AI    │    │  tasks    │    │      │    │  specs  │
└─────────┘    └─────────────┘    └───────────┘    └──────┘    └─────────┘
```

### 1. Draft Proposal (Before Coding!)

When you need a new feature or service, **DON'T start coding**. First create:

```
openspec/changes/{feature-name}/
├── proposal.md    # WHY - Rationale and scope
├── tasks.md       # HOW - Implementation checklist
└── specs/         # WHAT - Delta specifications (ADDED/MODIFIED/REMOVED)
```

### 2. Review & Align

Discuss the proposal with AI assistant:
- Is the scope clear?
- Which services are affected?
- What events need to be published/consumed?
- Does it conflict with existing specs?

### 3. Implement

Only after alignment, follow `tasks.md` to implement.

### 4. Ship & Archive

After implementation:
- Merge delta specs into `openspec/specs/`
- Archive the change folder

## Project Structure

```
java-spring-microservices/
├── openspec/
│   ├── specs/              # Source of truth (current state)
│   │   ├── services/       # Service type patterns
│   │   ├── events/         # Event flow specifications
│   │   └── api/            # API contract standards
│   └── changes/            # Proposed changes (spec-first)
├── services/               # Microservice implementations
├── shared/                 # Shared libraries
└── infrastructure/         # Docker, K8s configs
```

## Spec-First Example: Adding New Service

### Step 1: Create Proposal

```
openspec/changes/add-inventory-service/
├── proposal.md      # Why we need inventory service
├── tasks.md         # Implementation steps
└── specs/
    ├── services/inventory.md    # Service specification
    └── events/inventory-events.md  # New events
```

### Step 2: Review with AI

Ask AI to review:
- Does inventory-service follow core service pattern?
- Are events properly defined?
- Integration with order-service clear?

### Step 3: Implement (after approval)

```powershell
.\tools\generators\new-service.ps1 -Name "inventory" -Type "core"
```

## Service Types

| Type | Spec Location | When to Use |
|------|---------------|-------------|
| Core | `specs/services/core.md` | Business logic with database |
| Event | `specs/services/event.md` | Kafka consumer only |
| Gateway | `specs/services/gateway.md` | API Gateway |
| BFF | `specs/services/bff.md` | Frontend aggregator |

## Quick Commands

```powershell
# Create new service (AFTER spec approval)
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
- Service Specs: `openspec/specs/services/`

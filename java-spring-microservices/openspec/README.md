# OpenSpec - Java Spring Microservices

This project follows [OpenSpec](https://github.com/Fission-AI/OpenSpec) for spec-driven development.

## Structure

```
openspec/
├── README.md           # This file
├── specs/              # Current specifications (source of truth)
│   ├── services/       # Service type specifications
│   │   ├── core.md     # Core service pattern
│   │   ├── event.md    # Event service pattern
│   │   ├── gateway.md  # Gateway service pattern
│   │   └── bff.md      # BFF service pattern
│   ├── events/         # Event flow specifications
│   │   └── event-flows.md
│   └── api/            # API contract specifications
│       └── contracts.md
└── changes/            # Proposed changes (PRs)
    └── {feature}/
        ├── proposal.md
        ├── tasks.md
        └── specs/      # Delta specs
```

## How to Use

### 1. Read Specs Before Implementing

Before writing code, read the relevant specification:

```
# For a new core service
Read: openspec/specs/services/core.md

# For event handling
Read: openspec/specs/events/event-flows.md

# For API design
Read: openspec/specs/api/contracts.md
```

### 2. Propose Changes

To change existing behavior:

1. Create folder: `openspec/changes/{feature-name}/`
2. Add `proposal.md` with rationale
3. Add `tasks.md` with implementation checklist
4. Add `specs/` with delta specifications

Example:
```
openspec/changes/add-payment-retry/
├── proposal.md      # Why we need payment retry
├── tasks.md         # Implementation steps
└── specs/
    └── events/
        └── event-flows.md  # MODIFIED section for retry logic
```

### 3. Spec Format

Specifications use this format:

```markdown
### Requirement: {Name}

{Description of the requirement}

#### Scenario: {Scenario name}

Given {precondition}
When {action}
Then {expected result}

```code
{example code}
```
```

### 4. Delta Format

Changes use these markers:

```markdown
## ADDED Requirements
{new requirements}

## MODIFIED Requirements
{changed requirements with before/after}

## REMOVED Requirements
{requirements being removed}
```

## Spec Files

| File | Description |
|------|-------------|
| [services/core.md](specs/services/core.md) | Core service with database |
| [services/event.md](specs/services/event.md) | Kafka consumer service |
| [services/gateway.md](specs/services/gateway.md) | API Gateway |
| [services/bff.md](specs/services/bff.md) | Backend for Frontend |
| [events/event-flows.md](specs/events/event-flows.md) | Kafka topics and flows |
| [api/contracts.md](specs/api/contracts.md) | REST API standards |

## Integration with AI Tools

The root [AGENTS.md](../AGENTS.md) file provides workflow instructions for AI coding assistants that support the AGENTS.md convention (Amp, Jules, etc.).

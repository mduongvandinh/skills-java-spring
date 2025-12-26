# OpenSpec - Java Spring Skills (Monolithic)

This project follows [OpenSpec](https://github.com/Fission-AI/OpenSpec) for spec-driven development.

## Structure

```
openspec/
├── README.md              # This file
├── specs/                 # Current specifications (source of truth)
│   ├── architecture/      # Package structure, layers
│   │   └── layers.md
│   ├── api/               # REST API standards
│   │   └── rest.md
│   └── testing/           # Testing patterns
│       └── tdd.md
└── changes/               # Proposed changes (PRs)
    └── {feature}/
        ├── proposal.md
        ├── tasks.md
        └── specs/
```

## How to Use

### 1. Read Specs Before Implementing

```
# For architecture decisions
Read: openspec/specs/architecture/layers.md

# For API design
Read: openspec/specs/api/rest.md

# For testing
Read: openspec/specs/testing/tdd.md
```

### 2. Propose Changes

To change existing behavior:

1. Create folder: `openspec/changes/{feature-name}/`
2. Add `proposal.md` with rationale
3. Add `tasks.md` with implementation checklist
4. Add `specs/` with delta specifications

### 3. Spec Format

```markdown
### Requirement: {Name}

{Description}

#### Scenario: {Scenario name}

Given {precondition}
When {action}
Then {expected result}
```

## Spec Files

| File | Description |
|------|-------------|
| [architecture/layers.md](specs/architecture/layers.md) | Package structure & layers |
| [api/rest.md](specs/api/rest.md) | REST API standards |
| [testing/tdd.md](specs/testing/tdd.md) | TDD patterns with Mockito |

# Java Spring Boot Skill Sets

Bộ skill tổng hợp cho AI Coding Assistants, giúp tạo ra code Java Spring Boot chất lượng cao.

## Tổng quan

| Skill Set | Mô tả | Phù hợp với |
|-----------|-------|-------------|
| [java-spring-skills](java-spring-skills/) | Monolithic application | Team nhỏ, MVP, CRUD apps |
| [java-spring-microservices](java-spring-microservices/) | Microservices architecture | Team lớn, scaling độc lập |

## Chọn bộ skill nào?

### Bảng so sánh nhanh

| Tiêu chí | java-spring-skills | java-spring-microservices |
|----------|:------------------:|:-------------------------:|
| **Team size** | 1-5 developers | > 5 developers |
| **Infrastructure** | VM / Simple hosting | Kubernetes / Docker Swarm |
| **DevOps maturity** | Basic CI/CD | Advanced (K8s, Service Mesh) |
| **Database** | Single DB | DB per service |
| **Message Queue** | Optional | Required (Kafka) |
| **Monitoring** | Basic (Actuator) | Distributed tracing |
| **Deploy complexity** | 1 artifact | Multiple services |
| **Development speed** | Nhanh hơn ban đầu | Chậm hơn ban đầu |
| **Scaling** | Vertical (scale cả app) | Horizontal (scale từng service) |
| **Cost** | Thấp | Cao hơn |

### Decision Flowchart

```
                         ┌────────────────────────┐
                         │  Đánh giá dự án của bạn │
                         └───────────┬────────────┘
                                     │
        ┌────────────────────────────┼────────────────────────────┐
        │                            │                            │
        ▼                            ▼                            ▼
┌───────────────┐          ┌─────────────────┐          ┌─────────────────┐
│   TEAM SIZE   │          │ INFRASTRUCTURE  │          │   REQUIREMENTS  │
├───────────────┤          ├─────────────────┤          ├─────────────────┤
│ > 5 devs?     │          │ Có K8s/Docker   │          │ Scale độc lập?  │
│ Multi-team?   │          │ Swarm?          │          │ Fault isolation?│
│ Độc lập deploy│          │ Có Kafka?       │          │ Multi-domain?   │
│ từng team?    │          │ DevOps mature?  │          │ High traffic?   │
└───────┬───────┘          └────────┬────────┘          └────────┬────────┘
        │                           │                            │
        └───────────────────────────┼────────────────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
                    ▼                               ▼
        ┌─────────────────────┐         ┌─────────────────────┐
        │   ≥ 2 câu trả lời   │         │   < 2 câu trả lời   │
        │       là YES        │         │       là YES        │
        └──────────┬──────────┘         └──────────┬──────────┘
                   │                               │
                   ▼                               ▼
        ┌─────────────────────┐         ┌─────────────────────┐
        │ java-spring-        │         │ java-spring-skills  │
        │ microservices       │         │    (Monolithic)     │
        └─────────────────────┘         └─────────────────────┘
```

### Dùng java-spring-skills khi:

**Team & Organization:**
- Team 1-5 developers
- Single team ownership
- Startup giai đoạn đầu, MVP

**Infrastructure:**
- Deploy trên VM hoặc simple hosting
- Chưa có Kubernetes/Docker orchestration
- Chưa có message queue (Kafka/RabbitMQ)
- DevOps team nhỏ hoặc chưa có

**Requirements:**
- CRUD application
- Traffic vừa phải
- Không cần scale từng module riêng
- Muốn time-to-market nhanh

### Dùng java-spring-microservices khi:

**Team & Organization:**
- Team > 5 developers
- Multiple teams, mỗi team own 1-2 services
- Cần deploy độc lập giữa các team

**Infrastructure:**
- Đã có Kubernetes hoặc Docker Swarm
- Đã có Kafka/message queue
- Có DevOps team với experience về container orchestration
- Có budget cho infrastructure phức tạp hơn

**Requirements:**
- High traffic, cần scale horizontal
- Fault isolation (1 service fail không ảnh hưởng cả hệ thống)
- Multiple domains với business logic phức tạp
- Polyglot persistence (mỗi service có thể dùng DB khác nhau)

---

## java-spring-skills (Monolithic)

```
java-spring-skills/
├── src/main/java/com/company/app/
│   ├── domain/           # Entities, Repositories
│   ├── application/      # Services, DTOs
│   ├── infrastructure/   # JPA, External APIs
│   └── interfaces/       # Controllers
├── src/test/             # Unit + Integration tests
└── pom.xml               # Maven build
```

### Quick Start

```powershell
cd java-spring-skills

# Build
mvn clean compile

# Test (18 tests)
mvn test

# Run
mvn spring-boot:run
```

### Tính năng

- DDD package structure
- TDD với Mockito
- Maven profiles (dev, prod, docker)
- Optional Docker support
- Spring Data JPA + Flyway
- Bean Validation
- Global Exception Handling

---

## java-spring-microservices

```
java-spring-microservices/
├── services/                    # Microservices
│   ├── user-service/
│   ├── order-service/
│   └── gateway-service/
├── shared/                      # Shared Libraries
│   ├── common-lib/              # DTOs, Exceptions
│   ├── event-lib/               # Domain Events
│   └── api-contracts/           # OpenAPI specs
├── infrastructure/
│   ├── docker/                  # Docker Compose
│   └── kubernetes/              # K8s manifests
├── tools/generators/            # Service generator
└── openspec/                    # Specifications
```

### Quick Start

```powershell
cd java-spring-microservices

# 1. Build shared libraries
mvn install -pl shared/common-lib,shared/event-lib -DskipTests

# 2. Start infrastructure
cd infrastructure\docker
docker-compose up -d

# 3. Create new service
cd ..\..
.\tools\generators\new-service.ps1 -Name "product" -Type "core"

# 4. Run service
cd services\product-service
mvn spring-boot:run
```

### Service Types

| Type | Mô tả | Command |
|------|-------|---------|
| Core | Business logic + DB | `new-service.ps1 -Name "x" -Type "core"` |
| Event | Kafka consumer | `new-service.ps1 -Name "x" -Type "event"` |
| Gateway | API Gateway | Copy từ templates |
| BFF | Backend for Frontend | `new-service.ps1 -Name "x" -Type "bff"` |

### Architecture

```
                    ┌─────────────┐
                    │   Gateway   │ :8080
                    └──────┬──────┘
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
    ┌────────────┐  ┌────────────┐  ┌────────────┐
    │    User    │  │   Order    │  │  Payment   │
    │  Service   │  │  Service   │  │  Service   │
    └─────┬──────┘  └─────┬──────┘  └─────┬──────┘
          │               │               │
          └───────────────┴───────────────┘
                          │
                    ┌─────▼─────┐
                    │   Kafka   │
                    └───────────┘
```

---

## AI IDE Support

Cả 2 bộ skill đều hỗ trợ các AI IDE:

| IDE | File |
|-----|------|
| Claude Code | `CLAUDE.md` |
| Cursor | `.cursorrules` |
| Windsurf | `.windsurfrules` |
| GitHub Copilot | `.github/copilot-instructions.md` |
| Continue.dev | `.continuerc.json` |

### OpenSpec (Spec-First Development)

Cả 2 bộ skill đều hỗ trợ [OpenSpec](https://github.com/Fission-AI/OpenSpec) - **viết spec trước, code sau**:

```
┌─────────┐    ┌─────────────┐    ┌───────────┐    ┌──────┐    ┌─────────┐
│  Draft  │───▶│Review/Align │───▶│ Implement │───▶│ Ship │───▶│ Archive │
│proposal │    │  with AI    │    │  tasks    │    │      │    │  specs  │
└─────────┘    └─────────────┘    └───────────┘    └──────┘    └─────────┘
```

**Cấu trúc chung:**
```
openspec/
├── specs/           # Source of truth (current state)
└── changes/         # Proposed changes (spec-first)
    └── {feature}/
        ├── proposal.md   # WHY - Rationale
        ├── tasks.md      # HOW - Checklist
        └── specs/        # WHAT - Delta (ADDED/MODIFIED/REMOVED)
```

**Ví dụ có sẵn:**
- Monolithic: `java-spring-skills/openspec/changes/example-user-registration/`
- Microservices: `java-spring-microservices/openspec/changes/example-add-inventory-service/`

---

## Technology Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.2, Spring Cloud 2023 |
| Database | PostgreSQL 16 |
| Messaging | Apache Kafka (microservices) |
| Caching | Redis |
| Build | Maven 3.9+ |
| Testing | JUnit 5, Mockito |
| Container | Docker |
| Orchestration | Kubernetes |

---

## Yêu cầu hệ thống

- Java 21+ ([Download](https://adoptium.net/))
- Maven 3.9+ ([Download](https://maven.apache.org/download.cgi))
- Docker Desktop ([Download](https://www.docker.com/products/docker-desktop/)) - Optional cho monolithic, Required cho microservices

---

## Documentation

### java-spring-skills
- [SKILL.md](java-spring-skills/SKILL.md) - Skill overview
- [QUICKSTART.md](java-spring-skills/QUICKSTART.md) - Getting started

### java-spring-microservices
- [SKILL.md](java-spring-microservices/SKILL.md) - Skill overview
- [QUICKSTART.md](java-spring-microservices/QUICKSTART.md) - Getting started
- [Architecture Overview](java-spring-microservices/docs/architecture/overview.md)
- [Event Flows](java-spring-microservices/docs/architecture/event-flows.md)
- [OpenSpec](java-spring-microservices/openspec/README.md)

---

## License

MIT License

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Running the Application
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Using system Maven
mvn spring-boot:run

# Build and run JAR
./mvnw clean package
java -jar target/bank-simulator-1.0.0.jar
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AccountServiceTest

# Run single test method
./mvnw test -Dtest=AccountServiceTest#createAccount_Success
```

### Building
```bash
# Clean build
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

## Application Architecture

### Core Design Principles
- **Completely In-Memory**: Uses `ConcurrentHashMap` for data storage, no database
- **Thread-Safe Operations**: `AtomicLong` for ID generation, `synchronized` methods for transactions
- **Data Reset by Design**: All data is lost on server restart (intentional behavior)

### Key Components

#### Data Layer
- `Account.java`: Entity with thread-safe deposit/withdraw operations using `synchronized`
- `Transaction.java`: Immutable transaction records with `AtomicLong` ID generation
- Data stored in `AccountService` using `ConcurrentHashMap<Long, Account>`

#### Business Logic
- `AccountService.java`: Core business logic, manages the in-memory account storage
- All account operations (create, deposit, withdraw) are centralized here
- Transaction history is maintained within each `Account` object

#### Web Layer
- `AccountController.java`: Spring MVC controller handling all web requests
- Thymeleaf templates in `src/main/resources/templates/`:
  - `accounts.html`: Account listing page
  - `account-form.html`: Account creation form
  - `account-detail.html`: Account details with transaction forms

### Concurrency Handling
- `ConcurrentHashMap` ensures thread-safe account storage
- `Account` methods use `synchronized` for balance modifications
- `AtomicLong` generators ensure unique IDs across threads
- Transaction lists are defensively copied when returned

### Configuration
- **Port**: 9090 (configured in `application.properties`)
- **Thymeleaf**: Cache disabled for development
- **Tech Stack**: Java 17, Spring Boot 3.2.0, Maven, Bootstrap 5

### Testing Strategy
- Unit tests focus on `AccountService` business logic
- Integration tests verify Spring context loading
- Key test scenarios: account creation, deposits, withdrawals, insufficient balance handling

## 프로젝트 문서

코드 생성 및 개발 시 다음 문서들을 참조하여 일관성을 유지하세요:

### 📋 API 문서 (docs/api.md)
- 웹 엔드포인트 명세와 파라미터
- AccountService 메서드 시그니처 
- 에러 처리 패턴

### 🏗️ 아키텍처 문서 (docs/architecture.md)  
- 동시성 제어 방식 (ConcurrentHashMap, synchronized, AtomicLong)
- 계층화 아키텍처 (Controller → Service → Entity)
- 데이터 모델과 관계

### 🔧 개발 가이드 (docs/development-guide.md)
- 코딩 표준 (명명 규칙, 패키지 구조)
- 테스트 작성 패턴 (AAA 패턴, 네이밍 규칙)
- 새로운 기능 개발 절차

**중요**: 새로운 코드 작성 시 이 문서들의 패턴과 규칙을 따라 일관성을 유지하세요.
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

# Run single test method (Java 표준 명명 규칙 적용)
./mvnw test -Dtest=AccountServiceTest#createAccount_WithValidInput_Success
```

### Building
```bash
# Clean build
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests

# Code quality check with build
./mvnw clean verify
```

## Application Architecture

### Core Design Principles
- **Completely In-Memory**: Uses `ConcurrentHashMap` for data storage, no database
- **Thread-Safe Operations**: `AtomicLong` for ID generation, `synchronized` methods for transactions
- **Data Reset by Design**: All data is lost on server restart (intentional behavior)
- **Java Coding Standards**: Follows Google Java Style Guide and eGovFramework standards
- **Clean Architecture**: Layered architecture (Controller → Service → Domain)

### Key Components

#### Data Layer
- `Account.java`: Entity with thread-safe deposit/withdraw operations using `synchronized`
- `Transaction.java`: Immutable transaction records with `AtomicLong` ID generation
- Data stored in `AccountService` using `ConcurrentHashMap<Long, Account>`

#### Business Logic
- `AccountService.java`: Core business logic, manages the in-memory account storage
- All account operations (create, deposit, withdraw, transfer) are centralized here
- Transaction history is maintained within each `Account` object
- Deadlock prevention using ordered locking mechanism for transfers

#### Web Layer
- `AccountController.java`: Spring MVC controller handling all web requests
- Thymeleaf templates in `src/main/resources/templates/`:
  - `accounts.html`: Account listing page
  - `account-form.html`: Account creation form
  - `account-detail.html`: Account details with transaction forms (includes dropdown transfer)

### Concurrency Handling
- `ConcurrentHashMap` ensures thread-safe account storage
- `Account` methods use `synchronized` for balance modifications
- `AtomicLong` generators ensure unique IDs across threads
- Transaction lists are defensively copied when returned
- Transfer operations use ordered locking (by account ID) to prevent deadlocks

### Configuration
- **Port**: 9090 (configured in `application.properties`)
- **Thymeleaf**: Cache disabled for development
- **Tech Stack**: Java 17, Spring Boot 3.2.0, Maven, Bootstrap 5

### Testing Strategy
- Unit tests focus on `AccountService` business logic
- Integration tests verify Spring context loading
- Test naming convention: `methodName_condition_expectedResult`
- Key test scenarios: account creation, deposits, withdrawals, transfers, insufficient balance handling
- Use Given-When-Then pattern for test structure

## 📋 Java Coding Standards

### Naming Conventions
- **Classes**: PascalCase (예: `AccountService`, `UserController`)
- **Methods**: camelCase, 동사로 시작 (예: `createAccount`, `findUserById`)
- **Variables**: camelCase (예: `userName`, `totalAmount`)
- **Constants**: CONSTANT_CASE (예: `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`)
- **Packages**: 소문자, 점 구분 (예: `com.example.bank.service`)

### Code Style
- **Encoding**: UTF-8 필수
- **Indentation**: Space 4칸 (Tab 사용 금지)
- **Line Length**: 100자 권장, 120자 최대
- **Braces**: K&R 스타일 사용

```java
// ✅ Correct style
if (condition) {
    doSomething();
} else {
    doSomethingElse();
}
```

### Import Rules
1. Java standard libraries
2. Third-party libraries
3. Project internal packages
4. static imports (last)

### Logging Standards
- Use SLF4J with Logback
- Parameterized messages for performance
- Mask sensitive information in logs
- Appropriate log levels (TRACE, DEBUG, INFO, WARN, ERROR)

```java
// ✅ Correct logging
LOGGER.info("사용자 로그인 성공: userId={}, loginTime={}", userId, LocalDateTime.now());

// ❌ Avoid string concatenation
LOGGER.info("사용자 로그인 성공: " + userId + ", " + LocalDateTime.now());
```

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
- 개발 환경 설정 및 워크플로우
- 메서드 주석 표준 (Javadoc)
- 새로운 기능 개발 절차

### 📝 Java 코딩 표준 (docs/coding-standards.md)
- 명명 규칙 (클래스, 메서드, 변수, 상수)
- 코딩 스타일 (K&R 브레이스, 공백 사용, Import 순서)
- 클래스 구조 및 예외 처리 표준

### 🏛️ Spring Boot 표준 (docs/spring-boot-standards.md)
- 프로젝트 구조 및 패키지 명명
- Spring 어노테이션 사용법 (@Service, @Controller, @RestController)
- Configuration 클래스 작성법
- 보안 코딩 및 성능 최적화

### 🧪 테스트 표준 (docs/testing-standards.md)
- 테스트 구조 및 명명 규칙
- Given-When-Then 패턴 적용
- 단위/통합/시스템 테스트 작성법
- Mock 객체 사용 및 테스트 커버리지

### 📏 코드 품질 관리 (docs/code-quality-standards.md)
- 정적 분석 도구 설정 (Checkstyle, PMD, SpotBugs, SonarQube)
- IDE 설정 및 포맷터 규칙
- Claude Code 개발 체크리스트
- 품질 지표 및 지속적인 품질 관리

**중요**: 새로운 코드 작성 시 이 문서들의 패턴과 규칙을 따라 일관성을 유지하세요. 특히 다음 사항들을 준수하세요:

- **명명 규칙**: PascalCase 클래스명, camelCase 메서드/변수명, CONSTANT_CASE 상수명
- **Javadoc 주석**: 모든 public 메서드에 필수, 파라미터/반환값/예외 상황 명시
- **동시성 제어**: AtomicLong ID 생성, synchronized 메서드, ConcurrentHashMap 사용
- **테스트 코드**: `methodName_condition_expectedResult` 패턴, AAA 구조 적용
- **코드 품질**: 정적 분석 도구 통과, 테스트 커버리지 80% 이상 유지
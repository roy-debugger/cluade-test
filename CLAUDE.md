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

## 🔥 Claude Code 필수 개발 워크플로우

> **중요**: 모든 코드 작성/수정 작업 시 아래 워크플로우를 **반드시** 따르세요. 사용자가 별도로 언급하지 않아도 자동으로 수행해야 합니다.

### 📋 코드 작성 전 필수 체크리스트

**1. 개발 표준 종합 재확인 (필수)**
- [ ] **모든 docs/ 파일 표준 검토 완료**: 다음 8개 파일의 모든 표준 확인
  - [ ] `docs/coding-standards.md`: 명명 규칙, 코딩 스타일, 클래스 구조, Javadoc 표준
  - [ ] `docs/development-guide.md`: Javadoc 예시, 개발 워크플로우, 새 기능 개발 절차  
  - [ ] `docs/spring-boot-standards.md`: 어노테이션 사용법, 프로젝트 구조, 보안 코딩
  - [ ] `docs/architecture.md`: 동시성 제어, 데이터 모델, 아키텍처 패턴
  - [ ] `docs/testing-standards.md`: 테스트 명명 규칙, AAA 패턴, Mock 사용법
  - [ ] `docs/code-quality-standards.md`: 정적 분석, IDE 설정, 품질 지표
  - [ ] `docs/api.md`: 엔드포인트 명세, 파라미터 처리, 에러 핸들링
  - [ ] `docs/README.md`: 문서 활용법 및 업데이트 가이드
- [ ] **기존 코드 패턴 분석**: 현재 코드베이스와의 일관성 확인
- [ ] **표준 충돌 사항 식별**: 여러 문서 간 모순되는 표준이 있는지 확인

**2. 구현 계획 수립**  
- [ ] TodoWrite로 "모든 docs/ 표준 종합 적용 확인" 항목 포함한 계획 작성
- [ ] 각 docs 파일별 표준 적용 체크리스트 생성
- [ ] 클래스/메서드별 종합 표준 준수 일정 포함
- [ ] 최종 품질 검증 단계를 마지막 todo로 추가

### ⚡ 실시간 코드 작성 규칙

**모든 public 메서드 작성 시:**
```java
// ✅ 반드시 이 패턴으로 작성
/**
 * [메서드 목적을 한 줄로 설명]
 * 
 * [필요시 상세 설명: 비즈니스 로직, 동시성 제어, 주의사항]
 *
 * @param [파라미터명] [설명 (null 허용여부, 제약사항 포함)]
 * @return [반환값 설명 (타입과 의미)]
 * @throws [예외타입] [예외 발생 조건과 상황]
 */
public ReturnType methodName(ParamType param) {
    // 구현
}
```

**클래스 작성 시:**
```java
// ✅ 모든 클래스에 필수
/**
 * [클래스 목적과 책임 설명]
 * 
 * [아키텍처에서의 역할, 동시성 특성, 주요 기능]
 *
 * @author Claude Code
 * @version 1.0
 * @since 2024-01-01
 */
@Service  // 또는 적절한 어노테이션
public class ClassName {
```

### 🔍 코드 완성 후 필수 검증 단계

**1. docs/coding-standards.md 준수 확인**
- [ ] 클래스명: PascalCase, 명사/명사구, 약어 첫글자만 대문자
- [ ] 메서드명: camelCase, 동사로 시작, boolean은 is/has/can/should 접두사  
- [ ] 변수명: camelCase, 의미있는 이름, 축약어 지양
- [ ] 상수명: CONSTANT_CASE, static final 필드
- [ ] Import 순서: Java 표준 → 서드파티 → 프로젝트 → static import
- [ ] K&R 중괄호 스타일, 공백 사용 규칙 준수
- [ ] 모든 public 메서드에 완전한 Javadoc (@param, @return, @throws 포함)

**2. docs/spring-boot-standards.md 준수 확인**  
- [ ] 적절한 Spring 어노테이션 사용 (@Service, @Controller, @RestController)
- [ ] 패키지 구조 및 명명 규칙 준수
- [ ] RequiredArgsConstructor, Slf4j 등 Lombok 어노테이션 활용
- [ ] Configuration 클래스 올바른 작성
- [ ] 보안 코딩 및 성능 최적화 적용

**3. docs/architecture.md 준수 확인**
- [ ] AtomicLong ID 생성 패턴 사용
- [ ] synchronized 메서드로 동시성 제어
- [ ] ConcurrentHashMap 활용한 스레드 안전 저장
- [ ] 데드락 방지 메커니즘 (순서화된 락킹) 적용
- [ ] 방어적 복사로 데이터 무결성 보장
- [ ] BigDecimal 정확한 금액 처리

**4. docs/testing-standards.md 준수 확인**
- [ ] 테스트 명명: methodName_condition_expectedResult 패턴
- [ ] Given-When-Then (AAA) 구조 적용
- [ ] 적절한 단위/통합 테스트 분리
- [ ] Mock 객체 올바른 사용
- [ ] 테스트 커버리지 요구사항 충족

**5. docs/api.md 준수 확인**
- [ ] 웹 엔드포인트 명세 준수
- [ ] 파라미터 처리 패턴 일관성
- [ ] RedirectAttributes 플래시 메시지 활용
- [ ] 적절한 HTTP 상태 코드 반환
- [ ] 에러 처리 표준 준수

**6. docs/code-quality-standards.md 준수 확인**
- [ ] 정적 분석 도구 기준 통과 가능한 코드
- [ ] IDE 포맷터 규칙 준수
- [ ] 품질 지표 요구사항 충족
- [ ] 지속적 품질 관리 체크리스트 완료

### 🚨 절대 놓치지 말아야 할 것들

**매 작업마다 자동으로 수행해야 하는 것들:**

1. **모든 docs/ 파일 표준 종합 검토** - 작업 시작 전 8개 문서 모두 확인
2. **코드 작성과 동시에 해당 표준 적용** - 나중에 하지 말고 실시간 적용
3. **Todo 리스트에 "docs/ 종합 표준 준수 검증" 항목 포함** - 매번 필수
4. **완성 후 8개 docs 파일 기준 종합 재검토** - 제출 전 마지막 체크
5. **표준 간 충돌 발견 시 사용자에게 즉시 확인** - 혼란 방지
6. **사용자 확인 전 종합 품질 검사 완료** - 기본 책임

### 📝 표준 준수 자가 진단 질문

코드 작성 완료 후 스스로에게 물어보기:

**docs/coding-standards.md 관련:**
- [ ] "모든 public 메서드에 완전한 Javadoc(@param, @return, @throws)이 있는가?"
- [ ] "명명 규칙(PascalCase 클래스, camelCase 메서드)을 모두 준수했는가?"
- [ ] "Import 순서와 K&R 브레이스 스타일을 올바르게 적용했는가?"

**docs/spring-boot-standards.md 관련:**
- [ ] "Spring 어노테이션을 적절히 사용했는가? (@Service, @Controller 등)"
- [ ] "패키지 구조와 Lombok 어노테이션을 올바르게 활용했는가?"

**docs/architecture.md 관련:**
- [ ] "AtomicLong ID 생성, synchronized 동시성 제어를 적용했는가?"
- [ ] "ConcurrentHashMap과 데드락 방지 메커니즘을 사용했는가?"

**docs/testing-standards.md 관련:**
- [ ] "테스트 명명 규칙과 AAA 패턴을 올바르게 적용했는가?"
- [ ] "적절한 단위/통합 테스트 분리와 Mock 사용을 했는가?"

**docs/api.md 관련:**
- [ ] "웹 엔드포인트 명세와 에러 처리 표준을 준수했는가?"

**docs/code-quality-standards.md 관련:**
- [ ] "정적 분석 도구 기준을 통과할 수 있는 품질의 코드인가?"

**종합 검토:**
- [ ] "8개 docs 파일의 모든 표준을 빠짐없이 적용했는가?"
- [ ] "표준 간 충돌이나 모순되는 부분은 없는가?"

### ⚠️ 품질 보장 약속

**Claude Code는 다음을 보장합니다:**

- **전체 docs/ 표준 자동 준수**: 사용자가 별도 언급하지 않아도 8개 문서의 모든 표준을 자동 적용
- **종합 품질 검증**: coding-standards, spring-boot-standards, architecture, testing-standards, api, code-quality-standards 모든 기준 충족
- **표준 충돌 감지 및 해결**: 여러 문서 간 모순되는 표준 발견 시 사용자에게 즉시 확인 요청
- **실시간 표준 적용**: 코드 작성과 동시에 해당 표준들을 실시간으로 적용
- **Todo 기반 추적**: 모든 docs/ 표준 준수 여부를 TodoWrite로 명시적 추적 및 관리

**이 종합 워크플로우를 따르지 않은 코드는 미완성 코드로 간주합니다.**

## 📚 docs/ 표준 우선순위 및 충돌 해결

**표준 적용 우선순위:**
1. **docs/architecture.md** - 시스템 아키텍처 및 동시성 제어 (최우선)
2. **docs/coding-standards.md** - 코딩 스타일, 명명 규칙, Javadoc (핵심)  
3. **docs/spring-boot-standards.md** - Spring Boot 어노테이션, 구조 (필수)
4. **docs/testing-standards.md** - 테스트 작성 패턴 (필수)
5. **docs/api.md** - 웹 엔드포인트 표준 (API 작업 시)
6. **docs/code-quality-standards.md** - 품질 지표, 정적 분석 (품질 보장)
7. **docs/development-guide.md** - 워크플로우, 프로세스 (참고)
8. **docs/README.md** - 문서 활용법 (가이드)
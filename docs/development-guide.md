# 개발 가이드

## 개발 환경 설정

### 필수 요구사항
- **Java 17** 이상
- **Maven 3.6** 이상
- **IDE**: IntelliJ IDEA, Eclipse, VS Code 등

### 프로젝트 설정
```bash
# 프로젝트 클론
git clone [repository-url]
cd springboot-test

# Maven wrapper 권한 설정 (Linux/Mac)
chmod +x mvnw
```

## 개발 워크플로우

### 1. 로컬 개발 서버 실행
```bash
# Maven wrapper 사용 (권장)
./mvnw spring-boot:run

# 시스템 Maven 사용
mvn spring-boot:run

# 또는 JAR 빌드 후 실행
./mvnw clean package
java -jar target/bank-simulator-1.0.0.jar
```

### 2. 테스트 실행
```bash
# 전체 테스트 실행
./mvnw test

# 특정 테스트 클래스 실행
./mvnw test -Dtest=AccountServiceTest

# 특정 테스트 메서드 실행 (Java 표준 명명 규칙)
./mvnw test -Dtest=AccountServiceTest#createAccount_WithValidInput_Success

# 테스트 건너뛰고 빌드
./mvnw clean package -DskipTests
```

### 3. 애플리케이션 접속
- **URL**: http://localhost:9090
- **기본 페이지**: 계좌 목록 (/accounts)

## 📋 Java 코딩 표준

> **참고**: 상세한 코딩 표준은 다음 문서들을 참조하세요:
> - [Java 코딩 표준](coding-standards.md) - 명명 규칙, 코드 스타일, 클래스 구조
> - [Spring Boot 표준](spring-boot-standards.md) - 어노테이션, 보안, 성능 최적화  
> - [테스트 표준](testing-standards.md) - 테스트 작성 패턴, Mock 사용법

### 1. 메서드 주석 표준 (Javadoc)

#### 주석 작성 원칙
- **모든 public 메서드**: Javadoc 주석 필수
- **복잡한 private 메서드**: Javadoc 주석 권장
- **간단한 getter/setter**: 주석 생략 가능
- **한글 설명**: 비즈니스 로직의 명확한 이해를 위해 한글 사용

#### Javadoc 태그 사용법
```java
/**
 * 메서드의 목적과 동작을 한 줄로 간단히 설명한다.
 * 
 * 필요시 상세 설명을 추가한다. 비즈니스 로직이나 주의사항,
 * 특별한 동작 방식에 대해 설명할 수 있다.
 *
 * @param paramName 파라미터 설명 (null 허용 여부, 제약사항 포함)
 * @return 반환값 설명 (타입과 의미)
 * @throws ExceptionType 예외 발생 조건과 상황 설명
 * @since 1.0.0
 * @see 관련된 다른 메서드나 클래스
 */
```

#### 프로젝트별 주석 예시

**AccountService 메서드 예시**:
```java
/**
 * 새로운 계좌를 생성하고 초기 입금을 처리한다.
 * 
 * 계좌 ID는 AtomicLong으로 자동 생성되며, 초기 입금액이 있는 경우
 * Transaction 객체로 거래 내역을 기록한다.
 *
 * @param accountHolder 예금주명 (null이나 빈 문자열 불허)
 * @param initialDeposit 초기 입금액 (null 허용, 음수 불허)
 * @return 생성된 Account 객체
 * @throws IllegalArgumentException 예금주명이 null/빈값이거나 초기입금액이 음수인 경우
 * @since 1.0.0
 */
public Account createAccount(String accountHolder, BigDecimal initialDeposit) {
    // 구현부
}

/**
 * 지정한 계좌에 금액을 입금한다.
 * 
 * 입금 처리는 스레드 안전하게 수행되며, 거래 내역이 자동으로 기록된다.
 *
 * @param accountId 입금할 계좌 ID
 * @param amount 입금 금액 (양수만 허용)
 * @param description 거래 설명 (null 허용시 "Deposit"으로 설정)
 * @return 업데이트된 Account 객체
 * @throws IllegalArgumentException 계좌가 존재하지 않거나 입금액이 0 이하인 경우
 */
public Account deposit(Long accountId, BigDecimal amount, String description) {
    // 구현부
}

/**
 * 계좌 간 송금을 처리한다.
 * 
 * 데드락 방지를 위해 계좌 ID 순서로 락킹하며,
 * 출금과 입금이 원자적으로 처리된다.
 *
 * @param fromAccountId 송금할 계좌 ID
 * @param toAccountId 받을 계좌 ID  
 * @param amount 송금 금액 (양수, 송금 계좌 잔액 이하)
 * @param description 송금 설명
 * @throws IllegalArgumentException 계좌 미존재, 잔액 부족, 송금액 오류시
 * @throws IllegalStateException 동일 계좌 간 송금 시도시
 */
public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
    // 구현부
}
```

**Account 엔티티 메서드 예시**:
```java
/**
 * 계좌에 금액을 입금한다.
 * 
 * 이 메서드는 synchronized로 보호되어 동시성 문제를 방지한다.
 * 잔액 업데이트와 거래 내역 기록이 원자적으로 수행된다.
 *
 * @param amount 입금할 금액 (반드시 양수)
 * @param description 거래 설명 (null인 경우 "Deposit"으로 설정)
 * @throws IllegalArgumentException amount가 null이거나 0 이하인 경우
 */
public synchronized void deposit(BigDecimal amount, String description) {
    // 구현부
}

/**
 * 계좌에서 금액을 출금한다.
 * 
 * 잔액 확인 후 출금을 진행하며, 부족시 예외를 발생시킨다.
 * 이 메서드는 synchronized로 보호된다.
 *
 * @param amount 출금할 금액 (반드시 양수, 잔액 이하)
 * @param description 거래 설명 (null인 경우 "Withdrawal"으로 설정)
 * @throws IllegalArgumentException amount가 null이거나 0 이하인 경우
 * @throws IllegalArgumentException 잔액이 부족한 경우
 */
public synchronized void withdraw(BigDecimal amount, String description) {
    // 구현부
}
```

**Controller 메서드 예시**:
```java
/**
 * 계좌 목록 페이지를 표시한다.
 *
 * @param model Spring MVC 모델 객체 (accounts 리스트 추가됨)
 * @return accounts.html 템플릿 이름
 */
@GetMapping("/accounts")
public String listAccounts(Model model) {
    // 구현부
}

/**
 * 새 계좌 생성 요청을 처리한다.
 * 
 * 성공 시 계좌 목록으로 리다이렉트하고, 실패 시 계좌 생성 폼으로
 * 돌아가며 에러 메시지를 표시한다.
 *
 * @param accountHolder 예금주명 (필수)
 * @param initialDeposit 초기 입금액 (선택사항)
 * @param redirectAttributes 플래시 메시지 전달용
 * @return 리다이렉트 URL
 */
@PostMapping("/accounts")
public String createAccount(@RequestParam String accountHolder, 
                           @RequestParam(required = false) BigDecimal initialDeposit,
                           RedirectAttributes redirectAttributes) {
    // 구현부
}
```

#### 주석 작성 가이드라인

**DO (해야 할 것)**:
```java
// ✅ 명확하고 구체적인 설명
/**
 * 계좌 잔액이 충분한지 확인한다.
 *
 * @param amount 확인할 금액
 * @return 잔액이 충분하면 true, 부족하면 false
 */

// ✅ 비즈니스 로직 설명 포함
/**
 * 송금 시 데드락을 방지하기 위해 계좌 ID 순서로 락킹한다.
 * 
 * A→B, B→A 동시 송금에서도 안전한 처리를 보장한다.
 */

// ✅ 예외 상황과 제약사항 명시
/**
 * @param accountId 계좌 ID (반드시 존재해야 함)
 * @throws IllegalArgumentException 계좌가 존재하지 않는 경우
 */
```

**DON'T (하지 말아야 할 것)**:
```java
// ❌ 자명한 내용 반복
/**
 * 계좌를 반환한다.
 * @return 계좌
 */

// ❌ 구현 세부사항만 설명
/**
 * ConcurrentHashMap에서 계좌를 조회한다.
 */

// ❌ 의미 없는 주석
/**
 * 이 메서드는 계좌를 생성하는 메서드이다.
 */
```

### 2. 빠른 시작 가이드

#### 프로젝트 클론 및 실행
```bash
# 프로젝트 클론
git clone [repository-url]
cd bank-simulator

# Maven wrapper 권한 설정 (Linux/Mac)
chmod +x mvnw

# 애플리케이션 실행
./mvnw spring-boot:run
```

#### 기본 개발 워크플로우
1. **개발 서버 실행**: `./mvnw spring-boot:run`
2. **테스트 실행**: `./mvnw test`
3. **코드 품질 검사**: `./mvnw clean verify`
4. **브라우저 접속**: http://localhost:9090

## 테스트 작성 가이드

> **참고**: 상세한 테스트 작성 표준은 [테스트 표준](testing-standards.md)을 참조하세요.

### 기본 테스트 패턴
- **명명 규칙**: `methodName_condition_expectedResult`
- **구조**: Given-When-Then (AAA) 패턴  
- **범위**: 단위/통합/시스템 테스트 작성
- **커버리지**: 라인 커버리지 80% 이상 목표

## 새로운 기능 개발

### 1. 도메인 엔티티 추가
```java
// 새로운 엔티티는 Account.java를 참고하여 작성
- AtomicLong으로 ID 자동 생성
- BigDecimal로 금액 처리
- synchronized 메서드로 동시성 제어
- LocalDateTime으로 시간 기록
```

### 2. 비즈니스 로직 추가
```java
// AccountService.java에 메서드 추가
- ConcurrentHashMap에서 데이터 조회
- 유효성 검사 후 IllegalArgumentException 발생
- 성공 시 업데이트된 객체 반환
```

### 3. 웹 엔드포인트 추가
```java
// AccountController.java에 매핑 추가
- @GetMapping: 조회/폼 표시
- @PostMapping: 데이터 처리
- RedirectAttributes: 성공/실패 메시지 전달
- Model: 뷰에 데이터 전달
```

### 4. 뷰 템플릿 추가
```html
<!-- src/main/resources/templates/ -->
- Thymeleaf 문법 사용
- Bootstrap 5 스타일 적용
- 다국어 지원 (한글/영문)
```

## 데이터 모델 이해

### Account (계좌)
```java
- Long id: 자동 생성 ID
- String accountHolder: 예금주명
- BigDecimal balance: 잔액
- LocalDateTime createdAt: 생성 시각
- List<Transaction> transactions: 거래 내역
```

### Transaction (거래)
```java
- Long id: 자동 생성 ID
- Long accountId: 계좌 ID
- TransactionType type: 거래 유형 (DEPOSIT/WITHDRAWAL/TRANSFER_OUT/TRANSFER_IN)
- BigDecimal amount: 거래 금액
- String description: 거래 설명
- LocalDateTime timestamp: 거래 시각
- Long targetAccountId: 송금 시 상대방 계좌 ID (송금 거래에만 사용)
```

## 동시성 처리 주의사항

### 1. 스레드 안전성
```java
// ✅ 올바른 방법 - synchronized 메서드
public synchronized void deposit(BigDecimal amount, String description) {
    this.balance = this.balance.add(amount);
    // 거래 기록도 동일한 락 내에서 처리
    this.transactions.add(new Transaction(this.id, TransactionType.DEPOSIT, amount, description));
}

// ❌ 잘못된 방법 (race condition 발생 가능)
public void deposit(BigDecimal amount, String description) {
    this.balance = this.balance.add(amount); // 동시성 문제
}
```

### 2. 송금 시 데드락 방지
```java
// ✅ 올바른 송금 구현 (데드락 방지)
public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
    // 계좌 ID 순서로 락킹하여 데드락 방지
    Account firstLock = fromAccountId < toAccountId ? fromAccount : toAccount;
    Account secondLock = fromAccountId < toAccountId ? toAccount : fromAccount;
    
    synchronized (firstLock) {
        synchronized (secondLock) {
            fromAccount.transferOut(amount, description, toAccountId);
            toAccount.transferIn(amount, description, fromAccountId);
        }
    }
}

// ❌ 잘못된 방법 (데드락 가능)
synchronized (fromAccount) {
    synchronized (toAccount) {
        // A→B, B→A 동시 송금 시 데드락 발생
    }
}
```

### 2. 데이터 접근 패턴
```java
// ConcurrentHashMap 사용으로 기본적인 스레드 안전성 보장
private final ConcurrentHashMap<Long, Account> accounts = new ConcurrentHashMap<>();

// 방어적 복사로 데이터 무결성 보장
public List<Transaction> getTransactions() {
    return new ArrayList<>(transactions);
}
```

## 디버깅 및 로깅

### 1. 로그 설정
```properties
# application.properties
logging.level.com.example.bank=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

### 로깅 표준 (향후 적용 권장)
```java
// ✅ SLF4J 로거 선언
private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

// ✅ 파라미터화된 메시지 사용
LOGGER.info("계좌 생성 완료: accountId={}, holderName={}", 
           account.getId(), account.getAccountHolder());

// ✅ 적절한 로그 레벨 사용
LOGGER.debug("입금 처리 시작: accountId={}, amount={}", accountId, amount);
LOGGER.info("송금 완료: from={}, to={}, amount={}", fromId, toId, amount);
LOGGER.warn("잔액 부족: accountId={}, balance={}, requestAmount={}", 
           accountId, balance, amount);
LOGGER.error("송금 처리 실패: {}", e.getMessage(), e);

// ✅ 민감 정보 마스킹
LOGGER.info("사용자 로그인: email={}", maskEmail(user.getEmail()));

// ❌ 피해야 할 로깅 패턴
LOGGER.info("계좌 정보: " + account.toString()); // 문자열 연결
LOGGER.debug("SQL: " + sql); // 보안 위험
```

### 2. 일반적인 디버깅 포인트
- **계좌 생성**: ID 생성 확인
- **입출금**: 잔액 변경 및 거래 기록
- **송금**: 데드락 방지 메커니즘 동작 확인
- **동시성**: 여러 스레드에서 동일 계좌 접근
- **예외 처리**: 유효성 검사 로직
- **UI 상호작용**: 드롭다운 계좌 선택 로직

### 3. 브라우저 개발자 도구 활용
- **네트워크 탭**: HTTP 요청/응답 확인
- **콘솔**: JavaScript 오류 확인
- **요소 검사**: HTML/CSS 문제 해결

## 성능 고려사항

### 1. 메모리 사용량
- 모든 데이터가 힙 메모리에 저장
- 계좌/거래 수 증가에 따른 메모리 사용량 증가
- 필요시 `-Xmx` 옵션으로 힙 크기 조정

### 2. 동시성 성능
- ConcurrentHashMap의 내장 파티셔닝 활용
- synchronized 범위 최소화
- 불필요한 객체 생성 방지

## 배포 준비

### 1. 프로덕션 빌드
```bash
# 최종 JAR 파일 생성
./mvnw clean package

# 생성된 JAR 파일 확인
ls -la target/bank-simulator-1.0.0.jar
```

### 2. 환경별 설정
```properties
# application-prod.properties (프로덕션용)
server.port=8080
spring.thymeleaf.cache=true
logging.level.com.example.bank=WARN
```

### 3. 실행 명령어
```bash
# 프로덕션 환경에서 실행
java -jar -Dspring.profiles.active=prod target/bank-simulator-1.0.0.jar
```
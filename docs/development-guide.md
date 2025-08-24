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

### 1. 패키지 구조 (표준 준수)
```
com.example.bank/
├── BankSimulatorApplication.java    # Spring Boot 메인 클래스
├── Account.java                     # 도메인 엔티티
├── Transaction.java                 # 도메인 엔티티 (TRANSFER_OUT/IN 포함)
├── AccountService.java              # 비즈니스 로직 (송금 기능 포함)
└── AccountController.java           # 웹 컨트롤러
```

### 2. 명명 규칙 (Google Java Style Guide)
- **클래스명**: PascalCase (예: `AccountService`, `UserController`)
- **메서드명**: camelCase, 동사로 시작 (예: `createAccount`, `findUserById`)
- **변수명**: camelCase (예: `accountHolder`, `totalAmount`)
- **상수명**: CONSTANT_CASE (예: `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`)
- **boolean 메서드**: is/has/can 접두사 (예: `isActive()`, `hasPermission()`)

### 3. 코드 스타일 표준
- **인코딩**: UTF-8 필수
- **들여쓰기**: Space 4칸 (Tab 사용 금지)
- **줄 길이**: 100자 권장, 120자 최대
- **중괄호**: K&R 스타일 사용

```java
// ✅ 올바른 스타일
if (condition) {
    doSomething();
} else {
    doSomethingElse();
}
```

### 4. Import 규칙
```java
// 1. Java 표준 라이브러리
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 2. 서드파티 라이브러리  
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

// 3. 프로젝트 내부 패키지
import com.example.bank.domain.Account;

// 4. static import (마지막)
import static org.assertj.core.api.Assertions.assertThat;
```

### 5. 예외 처리 표준
```java
// ✅ 구체적인 예외 타입 사용
if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
    throw new IllegalArgumentException("송금액은 0보다 커야 합니다");
}

// ✅ 예외 로깅 (향후 적용 권장)
try {
    accountService.transfer(fromId, toId, amount, description);
} catch (IllegalArgumentException e) {
    LOGGER.warn("잘못된 송금 요청: {}", e.getMessage(), e);
    throw e;
}
```

## 테스트 작성 가이드

### 1. 테스트 파일 위치
```
src/test/java/com/example/bank/
├── AccountServiceTest.java          # 비즈니스 로직 단위 테스트
└── BankSimulatorApplicationTests.java # Spring 컨텍스트 통합 테스트
```

### 2. 테스트 명명 표준 (Java 표준)
```java
// ✅ Java 표준 테스트 명명 규칙
@Test
@DisplayName("유효한 입금 요청 시 잔액이 정상적으로 증가한다")
void deposit_WithValidAmount_IncreasesBalance() {
    // 테스트 구현
}

@Test 
@DisplayName("잔액이 부족한 상태에서 출금 시 예외가 발생한다")
void withdraw_WithInsufficientBalance_ThrowsException() {
    // 테스트 구현
}

@Test
@DisplayName("유효한 송금 요청 시 송금이 성공한다")  
void transfer_WithValidRequest_Success() {
    // 테스트 구현
}
```

### 3. 테스트 구조 (Given-When-Then 패턴)
```java
@Test
@DisplayName("유효한 입금 요청 시 잔액이 증가한다")
void deposit_WithValidAmount_IncreasesBalance() {
    // Given: 테스트 데이터 준비
    Account account = accountService.createAccount("홍길동", new BigDecimal("1000"));
    BigDecimal depositAmount = new BigDecimal("500");
    
    // When: 테스트 실행
    Account updatedAccount = accountService.deposit(account.getId(), depositAmount, "테스트 입금");
    
    // Then: 결과 검증
    assertThat(updatedAccount.getBalance()).isEqualTo(new BigDecimal("1500"));
    assertThat(updatedAccount.getTransactions()).hasSize(2); // 초기입금 + 테스트입금
}
```

### 4. 테스트 범위
- **단위 테스트**: AccountService 비즈니스 로직
- **통합 테스트**: Spring 컨텍스트 로딩
- **핵심 시나리오**: 계좌 생성, 입금, 출금, 송금, 잔액 부족
- **동시성 테스트**: 멀티스레드 환경에서의 송금 안전성

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
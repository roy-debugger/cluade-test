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

# 특정 테스트 메서드 실행
./mvnw test -Dtest=AccountServiceTest#createAccount_Success

# 테스트 건너뛰고 빌드
./mvnw clean package -DskipTests
```

### 3. 애플리케이션 접속
- **URL**: http://localhost:9090
- **기본 페이지**: 계좌 목록 (/accounts)

## 코딩 표준

### 1. 패키지 구조
```
com.example.bank/
├── BankSimulatorApplication.java    # Spring Boot 메인 클래스
├── Account.java                     # 도메인 엔티티
├── Transaction.java                 # 도메인 엔티티  
├── AccountService.java              # 비즈니스 로직
└── AccountController.java           # 웹 컨트롤러
```

### 2. 명명 규칙
- **클래스명**: PascalCase (예: AccountService)
- **메서드명**: camelCase (예: createAccount)
- **변수명**: camelCase (예: accountHolder)
- **상수명**: UPPER_SNAKE_CASE (예: ID_GENERATOR)

### 3. 코드 스타일
- **들여쓰기**: 4 스페이스
- **한글 주석**: 비즈니스 로직 설명 시 사용
- **영문 주석**: 기술적 구현 설명 시 사용

## 테스트 작성 가이드

### 1. 테스트 파일 위치
```
src/test/java/com/example/bank/
├── AccountServiceTest.java          # 비즈니스 로직 단위 테스트
└── BankSimulatorApplicationTests.java # Spring 컨텍스트 통합 테스트
```

### 2. 테스트 네이밍 패턴
```java
@Test
void 메서드명_상황_예상결과() {
    // 예: createAccount_Success()
    // 예: withdraw_InsufficientBalance_ThrowsException()
}
```

### 3. 테스트 구조 (AAA 패턴)
```java
@Test
void deposit_Success() {
    // Arrange: 테스트 데이터 준비
    Account account = accountService.createAccount("홍길동", new BigDecimal("1000"));
    BigDecimal depositAmount = new BigDecimal("500");
    
    // Act: 테스트 실행
    Account updatedAccount = accountService.deposit(account.getId(), depositAmount, "테스트 입금");
    
    // Assert: 결과 검증
    assertEquals(new BigDecimal("1500"), updatedAccount.getBalance());
}
```

### 4. 테스트 범위
- **단위 테스트**: AccountService 비즈니스 로직
- **통합 테스트**: Spring 컨텍스트 로딩
- **핵심 시나리오**: 계좌 생성, 입금, 출금, 잔액 부족

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
- TransactionType type: 거래 유형 (DEPOSIT/WITHDRAWAL)
- BigDecimal amount: 거래 금액
- String description: 거래 설명
- LocalDateTime timestamp: 거래 시각
```

## 동시성 처리 주의사항

### 1. 스레드 안전성
```java
// 올바른 방법
public synchronized void deposit(BigDecimal amount, String description) {
    this.balance = this.balance.add(amount);
}

// 잘못된 방법 (race condition 발생 가능)
public void deposit(BigDecimal amount, String description) {
    this.balance = this.balance.add(amount); // 동시성 문제
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

### 2. 일반적인 디버깅 포인트
- **계좌 생성**: ID 생성 확인
- **입출금**: 잔액 변경 및 거래 기록
- **동시성**: 여러 스레드에서 동일 계좌 접근
- **예외 처리**: 유효성 검사 로직

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
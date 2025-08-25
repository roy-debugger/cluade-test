# 테스트 표준

이 문서는 프로젝트에서 테스트 코드 작성 시 준수해야 하는 표준을 정의합니다.

## 🧪 테스트 기본 원칙

### 테스트 작성 원칙
- **독립성**: 각 테스트는 서로 독립적이어야 함
- **반복성**: 동일한 결과를 보장해야 함  
- **빠른 실행**: 단위 테스트는 빠르게 실행되어야 함
- **명확성**: 테스트 의도가 명확해야 함
- **자동화**: 자동으로 실행되고 결과를 확인할 수 있어야 함

### 테스트 레벨
1. **단위 테스트**: 개별 메서드나 클래스 테스트
2. **통합 테스트**: 여러 컴포넌트 간의 상호작용 테스트
3. **시스템 테스트**: 전체 시스템의 동작 테스트

## 📁 테스트 파일 구조

### 디렉터리 구조
```
src/test/java/com/example/bank/
├── unit/                        # 단위 테스트
│   ├── AccountTest.java         # 도메인 모델 테스트
│   ├── AccountServiceTest.java  # 서비스 레이어 테스트
│   └── TransactionTest.java     # 트랜잭션 테스트
├── integration/                 # 통합 테스트  
│   ├── AccountControllerTest.java
│   └── AccountServiceIntegrationTest.java
└── system/                      # 시스템 테스트
    └── BankSimulatorApplicationTests.java
```

## 🏷️ 테스트 명명 규칙

### 테스트 클래스 명명
- 대상 클래스명 + "Test" suffix
- 예: `AccountService` → `AccountServiceTest`

### 테스트 메서드 명명 패턴
```java
// 패턴: 테스트대상메서드_상황_예상결과
@Test
@DisplayName("유효한 금액으로 입금 시 잔액이 증가한다")
void deposit_WithValidAmount_IncreasesBalance() {
    // 테스트 구현
}

@Test
@DisplayName("음수 금액으로 입금 시 예외가 발생한다")
void deposit_WithNegativeAmount_ThrowsException() {
    // 테스트 구현
}

@Test 
@DisplayName("존재하지 않는 계좌 ID로 조회 시 빈 Optional을 반환한다")
void findById_WithNonExistentId_ReturnsEmptyOptional() {
    // 테스트 구현
}
```

## 🧱 테스트 구조 (AAA 패턴)

### Given-When-Then 패턴
```java
@Test
@DisplayName("계좌 간 송금 성공 테스트")
void transfer_BetweenValidAccounts_Success() {
    // Given: 테스트 데이터 준비
    Account fromAccount = new Account("홍길동", new BigDecimal("10000"));
    Account toAccount = new Account("김철수", new BigDecimal("5000"));
    BigDecimal transferAmount = new BigDecimal("3000");
    
    accountService.createAccount(fromAccount.getAccountHolder(), fromAccount.getBalance());
    accountService.createAccount(toAccount.getAccountHolder(), toAccount.getBalance());
    
    // When: 테스트 실행
    accountService.transfer(fromAccount.getId(), toAccount.getId(), 
                           transferAmount, "용돈 송금");
    
    // Then: 결과 검증
    Account updatedFromAccount = accountService.getAccount(fromAccount.getId()).get();
    Account updatedToAccount = accountService.getAccount(toAccount.getId()).get();
    
    assertThat(updatedFromAccount.getBalance()).isEqualTo(new BigDecimal("7000"));
    assertThat(updatedToAccount.getBalance()).isEqualTo(new BigDecimal("8000"));
    
    // 거래 내역 검증
    List<Transaction> fromTransactions = accountService.getTransactionHistory(fromAccount.getId());
    assertThat(fromTransactions).hasSize(2); // 초기입금 + 송금
    assertThat(fromTransactions.get(1).getType()).isEqualTo(TransactionType.TRANSFER_OUT);
}
```

## 🎯 단위 테스트 표준

### Service 레이어 테스트
```java
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    
    private AccountService accountService;
    
    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }
    
    @Test
    @DisplayName("유효한 계좌 정보로 계좌 생성 시 성공한다")
    void createAccount_WithValidInput_Success() {
        // Given
        String accountHolder = "홍길동";
        BigDecimal initialDeposit = new BigDecimal("10000");
        
        // When
        Account account = accountService.createAccount(accountHolder, initialDeposit);
        
        // Then
        assertThat(account).isNotNull();
        assertThat(account.getId()).isNotNull();
        assertThat(account.getAccountHolder()).isEqualTo(accountHolder);
        assertThat(account.getBalance()).isEqualTo(initialDeposit);
        assertThat(account.getCreatedAt()).isNotNull();
        
        // 초기 입금 거래 내역 확인
        List<Transaction> transactions = account.getTransactions();
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(transactions.get(0).getAmount()).isEqualTo(initialDeposit);
    }
    
    @Test
    @DisplayName("null 예금주명으로 계좌 생성 시 예외가 발생한다")
    void createAccount_WithNullAccountHolder_ThrowsException() {
        // Given
        String accountHolder = null;
        BigDecimal initialDeposit = new BigDecimal("10000");
        
        // When & Then
        assertThatThrownBy(() -> accountService.createAccount(accountHolder, initialDeposit))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Account holder name is required");
    }
}
```

### Domain 모델 테스트
```java
class AccountTest {
    
    @Test
    @DisplayName("계좌 생성 시 ID가 자동으로 생성된다")
    void constructor_AutoGeneratesId() {
        // When
        Account account1 = new Account("홍길동", new BigDecimal("1000"));
        Account account2 = new Account("김철수", new BigDecimal("2000"));
        
        // Then
        assertThat(account1.getId()).isNotNull();
        assertThat(account2.getId()).isNotNull();
        assertThat(account1.getId()).isNotEqualTo(account2.getId());
    }
    
    @Test
    @DisplayName("동시에 여러 스레드에서 입금 시 정확한 잔액이 유지된다")
    void deposit_ConcurrentAccess_MaintainsCorrectBalance() throws InterruptedException {
        // Given
        Account account = new Account("홍길동", BigDecimal.ZERO);
        int threadCount = 10;
        BigDecimal depositAmount = new BigDecimal("1000");
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // When: 동시에 10개 스레드에서 1000원씩 입금
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    account.deposit(depositAmount, "동시 입금 테스트");
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executorService.shutdown();
        
        // Then
        BigDecimal expectedBalance = depositAmount.multiply(new BigDecimal(threadCount));
        assertThat(account.getBalance()).isEqualTo(expectedBalance);
        assertThat(account.getTransactions()).hasSize(threadCount);
    }
}
```

## 🔗 통합 테스트 표준

### Controller 통합 테스트
```java
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private AccountService accountService;
    
    @Test
    @DisplayName("계좌 생성 POST 요청 시 계좌 목록으로 리다이렉트한다")
    void createAccount_ValidRequest_RedirectsToAccountsList() throws Exception {
        // When & Then
        mockMvc.perform(post("/accounts")
                .param("accountHolder", "홍길동")
                .param("initialDeposit", "10000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts"))
                .andExpect(flash().attributeExists("successMessage"));
        
        // 실제로 계좌가 생성되었는지 확인
        List<Account> accounts = accountService.getAllAccounts();
        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getAccountHolder()).isEqualTo("홍길동");
    }
    
    @Test
    @DisplayName("잘못된 계좌 생성 요청 시 에러 메시지와 함께 폼으로 돌아간다")
    void createAccount_InvalidRequest_ReturnsToFormWithError() throws Exception {
        // When & Then
        mockMvc.perform(post("/accounts")
                .param("accountHolder", "") // 빈 예금주명
                .param("initialDeposit", "10000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/new"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
```

### 시스템 테스트
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankSimulatorApplicationTests {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @LocalServerPort
    private int port;
    
    @Test
    @DisplayName("애플리케이션이 정상적으로 시작된다")
    void contextLoads() {
        // Spring 컨텍스트가 로드되는지 확인
        assertThat(restTemplate).isNotNull();
    }
    
    @Test
    @DisplayName("헬스체크 엔드포인트가 정상 응답한다")
    void healthEndpoint_ReturnsOk() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/health", String.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }
}
```

## 🎭 Mock 사용 표준

### Mock 객체 생성과 사용
```java
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    
    @Mock
    private AccountService accountService;
    
    @InjectMocks
    private AccountController accountController;
    
    @Test
    @DisplayName("계좌 상세 페이지 요청 시 계좌 정보를 모델에 추가한다")
    void accountDetail_ExistingAccount_AddsAccountToModel() {
        // Given
        Long accountId = 1L;
        Account account = new Account("홍길동", new BigDecimal("10000"));
        List<Transaction> transactions = Arrays.asList(
            new Transaction(accountId, TransactionType.DEPOSIT, 
                           new BigDecimal("10000"), "초기 입금")
        );
        
        when(accountService.getAccount(accountId)).thenReturn(Optional.of(account));
        when(accountService.getTransactionHistory(accountId)).thenReturn(transactions);
        
        Model model = new ConcurrentModel();
        
        // When
        String viewName = accountController.accountDetail(accountId, model);
        
        // Then
        assertThat(viewName).isEqualTo("account-detail");
        assertThat(model.getAttribute("account")).isEqualTo(account);
        assertThat(model.getAttribute("transactions")).isEqualTo(transactions);
        
        verify(accountService).getAccount(accountId);
        verify(accountService).getTransactionHistory(accountId);
    }
}
```

## 📊 테스트 커버리지 표준

### 커버리지 목표
- **라인 커버리지**: 최소 80%
- **브랜치 커버리지**: 최소 70%  
- **메서드 커버리지**: 최소 90%

### JaCoCo 설정
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>CLASS</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 🛠️ 테스트 유틸리티

### 테스트 데이터 빌더
```java
public class AccountTestDataBuilder {
    
    private String accountHolder = "홍길동";
    private BigDecimal initialDeposit = new BigDecimal("10000");
    
    public static AccountTestDataBuilder anAccount() {
        return new AccountTestDataBuilder();
    }
    
    public AccountTestDataBuilder withAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
        return this;
    }
    
    public AccountTestDataBuilder withInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
        return this;
    }
    
    public Account build() {
        return new Account(accountHolder, initialDeposit);
    }
}

// 사용 예시
@Test
void testWithBuilder() {
    Account account = AccountTestDataBuilder.anAccount()
        .withAccountHolder("김철수")
        .withInitialDeposit(new BigDecimal("20000"))
        .build();
        
    assertThat(account.getAccountHolder()).isEqualTo("김철수");
}
```

## 🏃‍♂️ 테스트 실행 표준

### Maven 명령어
```bash
# 전체 테스트 실행
./mvnw test

# 특정 테스트 클래스 실행
./mvnw test -Dtest=AccountServiceTest

# 특정 테스트 메서드 실행
./mvnw test -Dtest=AccountServiceTest#deposit_WithValidAmount_IncreasesBalance

# 패키지별 테스트 실행
./mvnw test -Dtest="com.example.bank.unit.*Test"

# 커버리지 리포트 생성
./mvnw test jacoco:report
```

### 테스트 프로파일 설정
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    
logging:
  level:
    com.example.bank: DEBUG
    org.springframework.test: DEBUG
```

이러한 테스트 표준을 통해 신뢰성 높고 유지보수가 쉬운 테스트 코드를 작성할 수 있습니다.
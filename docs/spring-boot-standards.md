# Spring Boot 개발 표준

이 문서는 Spring Boot 프로젝트에서 준수해야 하는 개발 표준을 정의합니다.

## 🏗️ 프로젝트 구조

### 기본 디렉터리 구조
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── bank/
│   │               ├── config/          # 설정 클래스
│   │               ├── controller/      # 컨트롤러
│   │               ├── service/         # 비즈니스 로직
│   │               ├── repository/      # 데이터 액세스
│   │               ├── domain/          # 도메인 모델 (현재: Account, Transaction)
│   │               ├── dto/             # 데이터 전송 객체
│   │               ├── util/            # 유틸리티
│   │               └── exception/       # 예외 클래스
│   └── resources/
│       ├── templates/                   # Thymeleaf 템플릿
│       ├── static/                      # 정적 리소스 (CSS, JS, 이미지)
│       └── application.properties       # 설정 파일
└── test/
    └── java/                            # 테스트 코드
```

### 패키지 명명 규칙
- 모두 소문자 사용
- 단어 구분은 점(.)으로 처리
- 역방향 도메인 명명법 사용: `com.example.bank.module`

## 🔧 Spring Boot 어노테이션 사용

### 클래스 레벨 어노테이션
```java
// 컨트롤러 클래스
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final AccountService accountService;
    
    // 메서드들...
}

// 서비스 클래스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    // 메서드들...
}

// 리포지토리 인터페이스
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // 쿼리 메서드들...
}
```

### 메서드 레벨 어노테이션
```java
@Controller
public class AccountController {
    
    // GET 요청 처리
    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        // 구현
    }
    
    // POST 요청 처리 (생성)
    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public String createAccount(@RequestParam String accountHolder,
                               @RequestParam(required = false) BigDecimal initialDeposit,
                               RedirectAttributes redirectAttributes) {
        // 구현
    }
    
    // 경로 변수 사용
    @GetMapping("/accounts/{id}")
    public String getAccount(@PathVariable Long id, Model model) {
        // 구현
    }
    
    // 트랜잭션 처리가 필요한 서비스 메서드
    @Transactional
    public Account transfer(Long fromAccountId, Long toAccountId, 
                          BigDecimal amount, String description) {
        // 구현
    }
}
```

## ⚙️ Configuration 클래스

### 기본 Configuration 구조
```java
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class JpaConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");
    }
}

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf().disable()
            .build();
    }
}
```

### 환경별 설정 관리
```java
// application.yml
spring:
  profiles:
    active: dev
    
---
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:testdb
    
---
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:mysql://localhost:3306/bank_db
```

## 🛡️ 보안 표준

### 입력 값 검증
```java
@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    /**
     * 계좌를 생성한다. 입력값 검증을 수행한다.
     */
    public Account createAccount(@Valid CreateAccountRequest request) {
        // 추가 비즈니스 검증
        validateAccountInput(request);
        
        // 계좌 생성 로직
        return accountRepository.save(Account.from(request));
    }
    
    private void validateAccountInput(CreateAccountRequest request) {
        if (StringUtils.isBlank(request.getAccountHolder())) {
            throw new IllegalArgumentException("예금주명은 필수 입력값입니다.");
        }
        
        if (request.getInitialDeposit() != null && 
            request.getInitialDeposit().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("초기 입금액은 음수일 수 없습니다.");
        }
    }
}
```

### DTO 검증 어노테이션
```java
public class CreateAccountRequest {
    
    @NotBlank(message = "예금주명은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "예금주명은 2-50자 사이여야 합니다.")
    private String accountHolder;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "초기 입금액은 0보다 커야 합니다.")
    @Digits(integer = 10, fraction = 2, message = "잘못된 금액 형식입니다.")
    private BigDecimal initialDeposit;
    
    // getters, setters...
}
```

### 로깅 보안
```java
// ✅ 안전한 로깅
LOGGER.info("계좌 생성 완료: accountId={}", account.getId());
LOGGER.warn("잘못된 접근 시도: IP={}, accountId={}", clientIp, accountId);

// ❌ 민감한 정보 노출 금지
LOGGER.info("계좌 정보: {}", account.toString()); // 모든 필드 노출 위험
LOGGER.debug("요청 파라미터: {}", request); // 민감 정보 포함 가능
```

## 🚀 성능 최적화

### 컬렉션 사용 최적화
```java
@Service
public class AccountService {
    
    // ✅ 적절한 초기 용량 설정
    private final Map<Long, Account> accountCache = new ConcurrentHashMap<>(1000);
    
    /**
     * 활성 계좌 목록을 조회한다.
     */
    public List<Account> getActiveAccounts() {
        return accounts.values().stream()
            .filter(Account::isActive)
            .collect(Collectors.toList());
    }
    
    /**
     * 불변 컬렉션을 반환한다.
     */
    public List<Account> getAllAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts.values()));
    }
}
```

### 문자열 처리 최적화
```java
// ✅ StringBuilder 사용 (많은 문자열 연결)
public String generateAccountSummary(List<Account> accounts) {
    StringBuilder summary = new StringBuilder();
    for (Account account : accounts) {
        summary.append("계좌번호: ").append(account.getId())
               .append(", 잔액: ").append(account.getBalance())
               .append("\n");
    }
    return summary.toString();
}

// ✅ String.format 사용
public String formatTransactionMessage(String type, BigDecimal amount) {
    return String.format("%s이 완료되었습니다. 금액: ₩%s", type, amount);
}
```

## 🧪 테스트 표준 확장

### 통합 테스트
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class AccountServiceIntegrationTest {
    
    @Autowired
    private AccountService accountService;
    
    @Test
    @DisplayName("계좌 생성 통합 테스트")
    void createAccount_IntegrationTest() {
        // Given
        String accountHolder = "홍길동";
        BigDecimal initialDeposit = new BigDecimal("10000");
        
        // When
        Account account = accountService.createAccount(accountHolder, initialDeposit);
        
        // Then
        assertThat(account.getId()).isNotNull();
        assertThat(account.getAccountHolder()).isEqualTo(accountHolder);
        assertThat(account.getBalance()).isEqualTo(initialDeposit);
    }
}
```

### Mock 사용 표준
```java
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    
    @Mock
    private AccountService accountService;
    
    @InjectMocks
    private AccountController accountController;
    
    @Test
    @DisplayName("계좌 목록 조회 API 테스트")
    void listAccounts_ReturnsAccountsView() {
        // Given
        List<Account> accounts = Arrays.asList(
            new Account("홍길동", new BigDecimal("10000")),
            new Account("김철수", new BigDecimal("20000"))
        );
        when(accountService.getAllAccounts()).thenReturn(accounts);
        
        Model model = new ConcurrentModel();
        
        // When
        String viewName = accountController.listAccounts(model);
        
        // Then
        assertThat(viewName).isEqualTo("accounts");
        assertThat(model.getAttribute("accounts")).isEqualTo(accounts);
    }
}
```

## 📏 코드 품질 검사

### 정적 분석 도구 설정
```xml
<!-- pom.xml에 추가 -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.0</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Low</threshold>
        <xmlOutput>true</xmlOutput>
    </configuration>
</plugin>

<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.9.1.2184</version>
</plugin>
```

## 🚀 배포 및 운영

### 로깅 설정
```yaml
# application.yml
logging:
  level:
    com.example.bank: INFO
    org.springframework.web: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/bank-simulator.log
```

### 헬스체크 및 모니터링
```java
@RestController
public class HealthController {
    
    private final AccountService accountService;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("accountCount", accountService.getAccountCount());
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("application", "Bank Simulator");
        info.put("version", "1.0.0");
        info.put("java.version", System.getProperty("java.version"));
        return ResponseEntity.ok(info);
    }
}
```

### 프로파일별 설정
```java
@Component
@Profile("dev")
public class DevDataInitializer {
    
    @PostConstruct
    public void init() {
        // 개발환경 초기 데이터 생성
    }
}

@Component
@Profile("prod")
public class ProductionConfig {
    
    @PostConstruct
    public void init() {
        // 프로덕션 환경 설정
    }
}
```
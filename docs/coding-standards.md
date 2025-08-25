# Java 코딩 표준

이 문서는 프로젝트에서 Java 코드 작성 시 준수해야 하는 표준을 정의합니다.

## 📝 코딩 스타일

### 파일 인코딩 및 기본 설정
- **인코딩**: UTF-8 필수
- **들여쓰기**: Space 4칸 (Tab 문자 금지)
- **줄 길이**: 100자 제한 권장, 120자 최대
- **줄 바꿈**: Unix 스타일 (LF) 사용

### 중괄호 스타일 (K&R Style)
```java
// ✅ 올바른 방식
if (condition) {
    doSomething();
} else {
    doSomethingElse();
}

// ❌ 잘못된 방식
if (condition) 
{
    doSomething();
}
```

### 공백 사용 규칙
```java
// ✅ 올바른 공백 사용
if (condition) {
    method(param1, param2);
    int result = value1 + value2;
}

// 연산자 양쪽에 공백
int sum = a + b * c;

// 메서드 호출 시 괄호 앞에는 공백 없음
methodCall(parameter);
```

## 🏷️ 명명 규칙

### 클래스명
- PascalCase 사용
- 명사 또는 명사구 사용
- 약어는 첫 글자만 대문자

```java
// ✅ 올바른 클래스명
public class UserService { }
public class HttpClient { }
public class XmlParser { }

// ❌ 잘못된 클래스명
public class userservice { }
public class HTTPClient { }
public class XMLParser { }
```

### 메서드명
- camelCase 사용
- 동사로 시작
- boolean 반환 메서드는 is/has/can/should 접두사 사용

```java
// ✅ 올바른 메서드명
public void calculateTotalAmount() { }
public User findUserById(Long id) { }
public boolean isActive() { }
public boolean hasPermission() { }
public boolean canAccess() { }
public boolean shouldUpdate() { }

// ❌ 잘못된 메서드명
public void CalculateAmount() { }
public User user_by_id(Long id) { }
public boolean active() { }
```

### 변수명
- camelCase 사용
- 의미있는 이름 사용
- 축약어 지양

```java
// ✅ 올바른 변수명
String userName;
int totalCount;
List<User> activeUsers;

// ❌ 잘못된 변수명
String usrNm;
int cnt;
List<User> users1;
```

### 상수명
- CONSTANT_CASE 사용 (대문자 + 언더스코어)
- static final 필드에 사용

```java
// ✅ 올바른 상수명
public static final String DEFAULT_ENCODING = "UTF-8";
public static final int MAX_RETRY_COUNT = 3;
private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);

// ❌ 잘못된 상수명
public static final String defaultEncoding = "UTF-8";
public static final int maxRetryCount = 3;
```

## 📦 Import 구문

### Import 순서
1. Java 표준 라이브러리
2. 서드파티 라이브러리
3. 프로젝트 내부 패키지
4. static import (가장 마지막)

```java
// ✅ 올바른 import 순서
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.bank.domain.Account;
import com.example.bank.repository.AccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.example.bank.util.Constants.DEFAULT_PAGE_SIZE;
```

### Import 규칙
- 와일드카드(*) import 금지 (static import 제외)
- 사용하지 않는 import 제거
- 각 그룹 사이에 빈 줄 추가

```java
// ❌ 와일드카드 import 금지
import java.util.*;

// ✅ 명시적 import 사용
import java.util.List;
import java.util.Map;
import java.util.Set;
```

## 🏛️ 클래스 구조

### 클래스 멤버 순서
1. 상수 (static final)
2. static 변수
3. 인스턴스 변수
4. 생성자
5. static 메서드
6. 인스턴스 메서드
7. 내부 클래스 (nested class)

```java
public class AccountService {
    // 1. 상수
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
    private static final int MAX_ACCOUNTS = 1000;
    
    // 2. static 변수
    private static int instanceCount = 0;
    
    // 3. 인스턴스 변수
    private final ConcurrentHashMap<Long, Account> accounts;
    
    // 4. 생성자
    public AccountService() {
        this.accounts = new ConcurrentHashMap<>();
        instanceCount++;
    }
    
    // 5. static 메서드
    public static int getInstanceCount() {
        return instanceCount;
    }
    
    // 6. 인스턴스 메서드
    public Account createAccount(String accountHolder, BigDecimal initialDeposit) {
        // 구현
    }
    
    // 7. 내부 클래스
    private static class AccountValidator {
        // 구현
    }
}
```

### 접근 제어자 규칙
- 항상 명시적으로 접근 제어자 지정
- 가장 제한적인 접근 제어자 사용
- 순서: public → protected → package-private → private

## 💬 주석 및 문서화

### 클래스 주석
```java
/**
 * 계좌 관리 서비스 클래스
 * 
 * 계좌 생성, 입출금, 송금 기능을 제공하며,
 * 동시성 제어와 거래 내역 관리를 담당한다.
 * 
 * @author 개발자명
 * @version 1.0
 * @since 2024-01-01
 */
public class AccountService {
    // 클래스 구현
}
```

### 메서드 주석 (이전에 정의된 표준 사용)
```java
/**
 * 계좌 ID로 계좌 정보를 조회한다.
 * 
 * @param accountId 조회할 계좌의 ID
 * @return 계좌 정보, 존재하지 않으면 Optional.empty()
 * @throws IllegalArgumentException accountId가 null인 경우
 */
public Optional<Account> getAccount(Long accountId) {
    if (accountId == null) {
        throw new IllegalArgumentException("계좌 ID는 null일 수 없습니다.");
    }
    return Optional.ofNullable(accounts.get(accountId));
}
```

### 코드 내 주석 규칙
```java
// 한 줄 주석은 // 다음에 공백 하나 추가
public void processData() {
    // TODO: 성능 최적화 필요
    // FIXME: NPE 가능성 있음
    
    /* 
     * 여러 줄 주석은 이와 같이 작성
     * 각 줄 앞에 * 추가
     */
}
```

## ⚠️ 예외 처리

### 예외 처리 규칙
- 구체적인 예외 타입 사용
- 빈 catch 블록 금지
- 예외 로깅 시 스택 트레이스 포함

```java
// ✅ 올바른 예외 처리
try {
    Account account = accounts.get(accountId);
    account.withdraw(amount, description);
} catch (IllegalArgumentException e) {
    LOGGER.warn("잘못된 출금 요청: accountId={}, amount={}", accountId, amount, e);
    throw new ServiceException("출금 처리 실패", e);
}

// ❌ 잘못된 예외 처리
try {
    processAccount(account);
} catch (Exception e) {
    // 빈 catch 블록
}
```

### 사용자 정의 예외
```java
/**
 * 계좌 서비스 관련 예외의 기본 클래스
 */
public class AccountServiceException extends RuntimeException {
    public AccountServiceException(String message) {
        super(message);
    }
    
    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * 계좌를 찾을 수 없을 때 발생하는 예외
 */
public class AccountNotFoundException extends AccountServiceException {
    public AccountNotFoundException(Long accountId) {
        super("계좌를 찾을 수 없습니다: " + accountId);
    }
}
```
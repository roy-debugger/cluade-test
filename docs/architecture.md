# 아키텍처 문서

## 시스템 개요

Spring Boot 3.2.0 기반의 인메모리 은행 계좌 시뮬레이터로, 웹 인터페이스를 통해 계좌 관리 및 거래 처리 기능을 제공합니다.

## 핵심 설계 원칙

### 1. 완전 인메모리 저장
- **데이터베이스 불사용**: 모든 데이터를 메모리에 저장
- **ConcurrentHashMap 활용**: 스레드 안전한 데이터 저장
- **데이터 리셋**: 애플리케이션 재시작 시 모든 데이터 초기화 (의도된 동작)

### 2. 스레드 안전성
- **AtomicLong**: ID 자동 생성을 위한 스레드 안전한 카운터
- **synchronized 메서드**: 잔액 조작 시 동시성 제어
- **불변 객체**: Transaction 객체의 불변성 보장

### 3. 계층화 아키텍처
```
Web Layer (Controller) 
    ↓
Business Layer (Service) 
    ↓
Data Layer (Entity)
```

## 패키지 구조

```
com.example.bank/
├── BankSimulatorApplication.java    # 메인 애플리케이션
├── Account.java                     # 계좌 엔티티
├── Transaction.java                 # 거래 엔티티
├── AccountService.java              # 비즈니스 로직
└── AccountController.java           # 웹 컨트롤러
```

## 핵심 컴포넌트

### 1. 데이터 계층

#### Account.java
```java
- ID: AtomicLong으로 자동 생성
- 잔액: BigDecimal로 정확한 금액 처리
- 거래내역: 각 계좌별 Transaction 리스트 보관
- 동시성: synchronized 메서드로 입출금 보호
```

**주요 메서드**:
- `deposit()`: 동기화된 입금 처리
- `withdraw()`: 동기화된 출금 처리 + 잔액 확인
- `getTransactions()`: 방어적 복사로 거래내역 반환

#### Transaction.java
```java
- 불변 객체: 생성 후 수정 불가
- ID: AtomicLong으로 자동 생성
- 타입: DEPOSIT/WITHDRAWAL enum
- 타임스탬프: LocalDateTime으로 정확한 시간 기록
```

**트랜잭션 타입**:
- `DEPOSIT`: 입금 (한국어 표시: "입금")
- `WITHDRAWAL`: 출금 (한국어 표시: "출금")

### 2. 비즈니스 계층

#### AccountService.java
```java
- 스토리지: ConcurrentHashMap<Long, Account>
- 스레드 안전: ConcurrentHashMap의 내장 동시성 제어
- 비즈니스 로직: 계좌 생성, 입출금, 조회 등
```

**핵심 기능**:
- `createAccount()`: 계좌 생성 및 초기입금 처리
- `deposit()/withdraw()`: 거래 처리 위임
- `getAllAccounts()`: 전체 계좌 조회 (방어적 복사)
- `getAccount()`: Optional을 통한 안전한 조회

### 3. 웹 계층

#### AccountController.java
```java
- Spring MVC 컨트롤러
- Thymeleaf 뷰 연동
- RedirectAttributes를 통한 플래시 메시지 처리
```

**라우팅 구조**:
- `GET /accounts`: 계좌 목록
- `GET /accounts/new`: 계좌 개설 폼
- `POST /accounts`: 계좌 생성 처리
- `GET /accounts/{id}`: 계좌 상세/거래
- `POST /accounts/{id}/deposit`: 입금 처리
- `POST /accounts/{id}/withdraw`: 출금 처리

## 동시성 제어 전략

### 1. 계좌 저장소 레벨
```java
ConcurrentHashMap<Long, Account> accounts
```
- 여러 스레드가 동시에 다른 계좌에 접근 가능
- 내장 동시성 제어로 맵 연산 안전성 보장

### 2. 계좌 레벨
```java
public synchronized void deposit(BigDecimal amount, String description)
public synchronized void withdraw(BigDecimal amount, String description)
```
- 동일 계좌의 동시 입출금 방지
- 잔액 조회/수정의 원자성 보장

### 3. ID 생성 레벨
```java
private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
```
- 계좌 ID와 거래 ID의 유일성 보장
- 여러 스레드에서 동시 ID 생성 시 충돌 방지

## 데이터 흐름

### 계좌 생성 흐름
```
사용자 요청 → Controller → Service → Account 생성 → ConcurrentHashMap 저장
```

### 거래 처리 흐름
```
사용자 요청 → Controller → Service → Account 조회 → synchronized 메서드 호출 → Transaction 생성
```

### 조회 흐름
```
사용자 요청 → Controller → Service → 방어적 복사 → View 렌더링
```

## 메모리 관리

### 데이터 생명주기
- **계좌 데이터**: 애플리케이션 생명주기와 동일
- **거래 내역**: 각 계좌 객체 내부에 ArrayList로 누적 저장
- **세션 데이터**: Spring의 기본 세션 관리 활용

### 메모리 효율성 고려사항
- **BigDecimal 사용**: 정확한 금액 계산을 위해 부동소수점 대신 사용
- **방어적 복사**: 데이터 무결성을 위해 필요한 곳에만 적용
- **AtomicLong**: 메모리 효율적인 스레드 안전 카운터

## 확장성 고려사항

### 현재 제한사항
- **메모리 제한**: 모든 데이터가 힙 메모리에 저장
- **단일 인스턴스**: 스케일 아웃 시 데이터 불일치 가능성
- **영속성 부재**: 재시작 시 데이터 손실

### 확장 가능 지점
- **데이터베이스 연동**: JPA/Hibernate 추가 가능
- **캐시 계층**: Redis 등 외부 캐시 도입 가능
- **API 분리**: REST API 컨트롤러 추가 가능
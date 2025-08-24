# 🏦 은행 계좌 시뮬레이터

Java 17 + Spring Boot 3.x로 구현된 인메모리 은행 계좌 시뮬레이터입니다.

## 🚀 기능

- ✅ 계좌 개설 (예금주명, 초기입금액)
- ✅ 입금/출금 기능
- ✅ 계좌 조회/목록
- ✅ 거래내역 확인
- ✅ 반응형 웹 UI (Bootstrap 5)

## 🛠 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Maven**
- **내장 Tomcat**
- **Thymeleaf**
- **Bootstrap 5**
- **인메모리 저장** (ConcurrentHashMap)

## 📋 요구사항

- Java 17 이상
- Maven 3.6 이상

## 🔧 설치 및 실행

### 1. Maven으로 실행

```bash
# 프로젝트 클론/다운로드 후
mvn spring-boot:run
```

### 2. JAR 파일로 실행

```bash
# 빌드
mvn clean package

# 실행
java -jar target/bank-simulator-1.0.0.jar
```

### 3. VS Code에서 실행

1. Java Extension Pack 설치
2. `F5` 키 누르고 "Launch Bank Simulator" 선택
3. 또는 `BankSimulatorApplication.java` 파일에서 Run 버튼 클릭

## 🌐 접속

애플리케이션 실행 후 브라우저에서 접속:

```
http://localhost:9090
```

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/bank/
│   │   ├── Account.java              # 계좌 엔티티
│   │   ├── Transaction.java          # 거래 엔티티
│   │   ├── AccountService.java       # 비즈니스 로직
│   │   ├── AccountController.java    # 웹 컨트롤러
│   │   └── BankSimulatorApplication.java  # 메인 클래스
│   └── resources/
│       ├── templates/               # Thymeleaf 템플릿
│       │   ├── accounts.html        # 계좌 목록
│       │   ├── account-form.html    # 계좌 개설 폼
│       │   └── account-detail.html  # 계좌 상세/거래
│       └── application.properties   # 설정 파일
```

## 💾 데이터 저장

- **완전 인메모리**: DB 없이 ConcurrentHashMap 사용
- **동시성 제어**: AtomicLong, synchronized 메서드
- **데이터 리셋**: 서버 재시작시 모든 데이터 초기화 (의도된 동작)

## 🔒 동시성 처리

- `ConcurrentHashMap`으로 계좌 데이터 thread-safe 저장
- `AtomicLong`으로 ID 자동생성
- `synchronized` 메서드로 입출금 동시성 제어

## 📱 화면 구성

1. **계좌 목록**: 전체 계좌 조회, 새 계좌 개설 버튼
2. **계좌 개설**: 예금주명, 초기입금액 입력
3. **계좌 상세**: 잔액 확인, 입금/출금 폼, 거래내역

## 🎯 사용 예시

1. 계좌 개설: "홍길동", 초기입금 100,000원
2. 입금: 50,000원 (급여)
3. 출금: 30,000원 (생활비)
4. 거래내역에서 모든 거래 확인

## ⚡ 빠른 시작

```bash
git clone [repository-url]
cd springboot-test
mvn spring-boot:run
```

브라우저에서 `http://localhost:9090` 접속하여 계좌를 개설해보세요!
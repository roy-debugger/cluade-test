# API 문서

## 웹 엔드포인트

### 계좌 관리

#### GET /
- **설명**: 루트 경로, 계좌 목록으로 리다이렉트
- **응답**: 302 리다이렉트 → `/accounts`

#### GET /accounts
- **설명**: 모든 계좌 목록 조회
- **응답**: 계좌 목록 페이지 (`accounts.html`)
- **모델 데이터**:
  - `accounts`: 전체 계좌 리스트

#### GET /accounts/new
- **설명**: 새 계좌 개설 폼
- **응답**: 계좌 개설 폼 페이지 (`account-form.html`)

#### POST /accounts
- **설명**: 새 계좌 생성
- **파라미터**:
  - `accountHolder` (String, 필수): 예금주명
  - `initialDeposit` (BigDecimal, 선택): 초기입금액
- **성공**: 계좌 목록으로 리다이렉트, 성공 메시지 표시
- **실패**: 계좌 개설 폼으로 리다이렉트, 에러 메시지 표시

#### GET /accounts/{id}
- **설명**: 특정 계좌 상세 정보 및 거래내역 조회
- **경로 변수**:
  - `id` (Long): 계좌 ID
- **응답**: 계좌 상세 페이지 (`account-detail.html`)
- **모델 데이터**:
  - `account`: 계좌 정보
  - `transactions`: 거래내역 리스트
- **실패**: 계좌 목록으로 리다이렉트 (계좌 미존재시)

### 거래 처리

#### POST /accounts/{id}/deposit
- **설명**: 계좌 입금
- **경로 변수**:
  - `id` (Long): 계좌 ID
- **파라미터**:
  - `amount` (BigDecimal, 필수): 입금액
  - `description` (String, 선택): 거래 설명
- **성공**: 계좌 상세 페이지로 리다이렉트, 성공 메시지 표시
- **실패**: 계좌 상세 페이지로 리다이렉트, 에러 메시지 표시

#### POST /accounts/{id}/withdraw
- **설명**: 계좌 출금
- **경로 변수**:
  - `id` (Long): 계좌 ID
- **파라미터**:
  - `amount` (BigDecimal, 필수): 출금액
  - `description` (String, 선택): 거래 설명
- **성공**: 계좌 상세 페이지로 리다이렉트, 성공 메시지 표시
- **실패**: 계좌 상세 페이지로 리다이렉트, 에러 메시지 표시

## 서비스 메서드

### AccountService

#### createAccount(String accountHolder, BigDecimal initialDeposit)
- **설명**: 새 계좌 생성
- **파라미터**:
  - `accountHolder`: 예금주명 (null/공백 불허)
  - `initialDeposit`: 초기입금액 (음수 불허)
- **반환**: 생성된 Account 객체
- **예외**: IllegalArgumentException

#### getAllAccounts()
- **설명**: 모든 계좌 조회
- **반환**: Account 리스트

#### getAccount(Long accountId)
- **설명**: 특정 계좌 조회
- **파라미터**: `accountId` - 계좌 ID
- **반환**: Optional&lt;Account&gt;

#### deposit(Long accountId, BigDecimal amount, String description)
- **설명**: 입금 처리
- **파라미터**:
  - `accountId`: 계좌 ID
  - `amount`: 입금액 (양수)
  - `description`: 거래 설명
- **반환**: 업데이트된 Account 객체
- **예외**: IllegalArgumentException

#### withdraw(Long accountId, BigDecimal amount, String description)
- **설명**: 출금 처리
- **파라미터**:
  - `accountId`: 계좌 ID
  - `amount`: 출금액 (양수, 잔액 이하)
  - `description`: 거래 설명
- **반환**: 업데이트된 Account 객체
- **예외**: IllegalArgumentException

#### getTransactionHistory(Long accountId)
- **설명**: 계좌 거래내역 조회
- **파라미터**: `accountId` - 계좌 ID
- **반환**: Transaction 리스트
- **예외**: IllegalArgumentException

## 에러 처리

### 일반적인 에러 상황
- **계좌 미존재**: "Account not found"
- **예금주명 누락**: "Account holder name is required"
- **음수 입금액**: "Initial deposit cannot be negative"
- **잔액 부족**: "Insufficient balance"
- **잘못된 거래금액**: "Deposit/Withdrawal amount must be positive"

### 플래시 메시지
- **성공 메시지**: `successMessage` 속성으로 전달
- **에러 메시지**: `errorMessage` 속성으로 전달
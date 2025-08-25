# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 코드 작업을 수행할 때 사용하는 가이드입니다.

## 개발 명령어

### 애플리케이션 실행
```bash
# Maven wrapper 사용 (권장)
./mvnw spring-boot:run

# 시스템 Maven 사용
mvn spring-boot:run

# JAR 빌드 후 실행
./mvnw clean package
java -jar target/bank-simulator-1.0.0.jar
```

### 테스트
```bash
# 모든 테스트 실행
./mvnw test

# 특정 테스트 클래스 실행
./mvnw test -Dtest=AccountServiceTest

# 단일 테스트 메서드 실행 (Java 표준 명명 규칙 적용)
./mvnw test -Dtest=AccountServiceTest#createAccount_WithValidInput_Success
```

### 빌드
```bash
# 클린 빌드
./mvnw clean package

# 빌드 시 테스트 건너뛰기
./mvnw clean package -DskipTests

# 코드 품질 검사와 함께 빌드
./mvnw clean verify
```


## 📋 개발 표준 참조

상세한 개발 표준은 다음 문서들을 참조하세요:

### 핵심 문서
- **[Java 코딩 표준](docs/coding-standards.md)**: 명명 규칙, 코드 스타일, Import 순서, 클래스 구조, Javadoc 표준
- **[Spring Boot 표준](docs/spring-boot-standards.md)**: 어노테이션 사용법, 프로젝트 구조, 보안 코딩
- **[개발 가이드](docs/development-guide.md)**: 개발 환경 설정, 워크플로우, Javadoc 작성 예시
- **[테스트 표준](docs/testing-standards.md)**: 테스트 명명 규칙, AAA 패턴, Mock 사용법
- **[코드 품질 관리](docs/code-quality-standards.md)**: 정적 분석 도구, IDE 설정, 품질 지표

### 빠른 참조
**명명 규칙**: PascalCase 클래스, camelCase 메서드/변수, CONSTANT_CASE 상수  
**코드 스타일**: UTF-8, 공백 4칸, K&R 중괄호, 100자 줄 길이  
**Javadoc**: 모든 public 메서드에 필수 (@param, @return, @throws 포함)


## 🔥 Claude Code 필수 개발 워크플로우

> **중요**: 모든 코드 작성/수정 작업 시 아래 워크플로우를 **반드시** 따르세요. 사용자가 별도로 언급하지 않아도 자동으로 수행해야 합니다.

### 📋 코드 작성 전 필수 체크리스트

**1. 개발 표준 종합 검토 (필수)**
- [ ] **모든 docs/ 파일 표준 검토 완료**: 8개 문서의 모든 표준 확인 (상세 목록은 [개발 표준 참조](#-개발-표준-참조) 섹션 참조)
- [ ] **기존 코드 패턴 분석**: 현재 코드베이스와의 일관성 확인
- [ ] **표준 충돌 사항 식별**: 여러 문서 간 모순되는 표준이 있는지 확인

**2. 구현 계획 수립**
- [ ] TodoWrite로 "모든 docs/ 표준 종합 적용 확인" 항목 포함한 계획 작성  
- [ ] 각 핵심 문서별 표준 적용 체크리스트 생성
- [ ] 클래스/메서드별 종합 표준 준수 일정 포함
- [ ] 최종 품질 검증 단계를 마지막 todo로 추가

### 📝 표준 준수 자가 진단 질문

코드 작성 완료 후 스스로에게 물어보기:

**핵심 확인 사항:**
- [ ] **Javadoc**: 모든 public 메서드에 완전한 Javadoc (@param, @return, @throws)
- [ ] **명명 규칙**: PascalCase 클래스, camelCase 메서드, CONSTANT_CASE 상수
- [ ] **Spring 표준**: 적절한 어노테이션, 패키지 구조, Lombok 활용
- [ ] **동시성 제어**: AtomicLong, synchronized, ConcurrentHashMap, 데드락 방지
- [ ] **테스트 패턴**: AAA 구조, 적절한 분리, Mock 사용법
- [ ] **API/품질**: 엔드포인트 명세 준수, 정적 분석 도구 기준 통과

**종합 검토:**
- [ ] "모든 docs/ 표준을 빠짐없이 적용했는가?"
- [ ] "표준 간 충돌이나 모순되는 부분은 없는가?"

### ⚠️ 품질 보장 약속

**Claude Code는 다음을 보장합니다:**

- **전체 docs/ 표준 자동 준수**: 사용자가 별도 언급하지 않아도 모든 개발 표준 문서의 기준을 자동 적용
- **종합 품질 검증**: coding-standards, spring-boot-standards, architecture, testing-standards, api, code-quality-standards 모든 기준 충족
- **표준 충돌 감지 및 해결**: 여러 문서 간 모순되는 표준 발견 시 사용자에게 즉시 확인 요청
- **실시간 표준 적용**: 코드 작성과 동시에 해당 표준들을 실시간으로 적용
- **Todo 기반 추적**: 모든 docs/ 표준 준수 여부를 TodoWrite로 명시적 추적 및 관리

**‼️ 이 종합 워크플로우를 따르지 않은 코드는 미완성 코드로 간주합니다.**

## 📚 docs/ 표준 우선순위 및 충돌 해결

**표준 적용 우선순위:**
1. **[architecture.md](docs/architecture.md)** - 시스템 아키텍처 및 동시성 제어 (최우선)
2. **[coding-standards.md](docs/coding-standards.md)** - 코딩 스타일, 명명 규칙, Javadoc (핵심)  
3. **[spring-boot-standards.md](docs/spring-boot-standards.md)** - Spring Boot 어노테이션, 구조 (필수)
4. **[testing-standards.md](docs/testing-standards.md)** - 테스트 작성 패턴 (필수)
5. **[api.md](docs/api.md)** - 웹 엔드포인트 표준 (API 작업 시)
6. **[code-quality-standards.md](docs/code-quality-standards.md)** - 품질 지표, 정적 분석 (품질 보장)
7. **[development-guide.md](docs/development-guide.md)** - 워크플로우, 프로세스 (참고)
8. **[README.md](docs/README.md)** - 문서 활용법 (가이드)
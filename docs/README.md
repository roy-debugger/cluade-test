# 문서 디렉토리

이 디렉토리는 Spring Boot 은행 계좌 시뮬레이터 프로젝트의 체계적인 문서들을 포함합니다.

## 문서 목록

### 📋 [API 문서](api.md)
- 웹 엔드포인트 명세
- AccountService 메서드 설명
- 요청/응답 파라미터
- 에러 처리 방법

### 🏗️ [아키텍처 문서](architecture.md)
- 시스템 설계 원칙
- 컴포넌트 구조
- 동시성 제어 전략
- 데이터 흐름

### 🔧 [개발 가이드](development-guide.md)
- 개발 환경 설정
- 코딩 표준 및 규칙
- 테스트 작성 방법
- 새로운 기능 개발 절차

### 📝 [Java 코딩 표준](coding-standards.md)
- 명명 규칙 (클래스, 메서드, 변수, 상수)
- 코딩 스타일 (K&R 브레이스, 공백 사용)
- Import 구문 정리
- 클래스 구조 및 주석 작성법

### 🏛️ [Spring Boot 표준](spring-boot-standards.md)
- 프로젝트 구조 및 패키지 명명
- Spring 어노테이션 사용법
- Configuration 클래스 작성
- 보안 및 성능 최적화

### 🧪 [테스트 표준](testing-standards.md)
- 테스트 구조 및 명명 규칙
- 단위/통합/시스템 테스트 작성법
- Mock 객체 사용 표준
- 테스트 커버리지 관리

### 📏 [코드 품질 관리](code-quality-standards.md)
- 정적 분석 도구 설정 (Checkstyle, PMD, SpotBugs)
- IDE 설정 및 포맷터 규칙
- Claude Code 체크리스트
- 품질 지표 및 보고서 생성

## 문서 활용법

### Claude Code와의 연동
이 문서들은 CLAUDE.md에 참조되어 Claude Code가 코드 생성 시 자동으로 활용합니다:

- **API 설계**: 새로운 엔드포인트 작성 시 기존 패턴 준수
- **아키텍처 준수**: 동시성 처리, 데이터 모델 일관성 유지
- **개발 표준**: 명명 규칙, 테스트 패턴, 코드 스타일 적용

### 개발자를 위한 가이드
- 새로운 팀원 온보딩 시 필수 참고 자료
- 기능 개발 전 아키텍처 이해를 위한 학습 자료
- API 사용법 및 테스트 방법 참조

## 문서 업데이트

새로운 기능 추가나 아키텍처 변경 시 관련 문서도 함께 업데이트해주세요:

1. **API 변경** → `api.md` 업데이트
2. **아키텍처 변경** → `architecture.md` 업데이트  
3. **개발 프로세스 변경** → `development-guide.md` 업데이트
4. **CLAUDE.md** → docs 참조 섹션 업데이트
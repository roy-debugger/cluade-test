# 코드 품질 관리 표준

이 문서는 코드 품질 관리를 위한 도구 설정과 체크리스트를 정의합니다.

## 🔧 정적 분석 도구

### Maven 플러그인 설정

#### 1. Checkstyle (코딩 스타일 검사)
```xml
<!-- pom.xml에 추가 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.2.0</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <encoding>UTF-8</encoding>
        <consoleOutput>true</consoleOutput>
        <failsOnError>true</failsOnError>
        <linkXRef>false</linkXRef>
    </configuration>
    <executions>
        <execution>
            <id>validate</id>
            <phase>validate</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### 2. PMD (코드 품질 검사)
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.19.0</version>
    <configuration>
        <rulesets>
            <ruleset>/category/java/bestpractices.xml</ruleset>
            <ruleset>/category/java/codestyle.xml</ruleset>
            <ruleset>/category/java/design.xml</ruleset>
            <ruleset>/category/java/errorprone.xml</ruleset>
            <ruleset>/category/java/performance.xml</ruleset>
            <ruleset>/category/java/security.xml</ruleset>
        </rulesets>
        <printFailingErrors>true</printFailingErrors>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### 3. SpotBugs (버그 패턴 검사)
```xml
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.0</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Low</threshold>
        <xmlOutput>true</xmlOutput>
        <excludeFilterFile>spotbugs-exclude.xml</excludeFilterFile>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### 4. SonarQube (종합 코드 품질 분석)
```xml
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.9.1.2184</version>
</plugin>
```

### Checkstyle 설정 파일
```xml
<!-- checkstyle.xml -->
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>
    
    <!-- 파일 길이 제한 -->
    <module name="FileLength">
        <property name="max" value="2000"/>
    </module>
    
    <!-- 탭 문자 금지 -->
    <module name="FileTabCharacter"/>
    
    <module name="TreeWalker">
        <!-- 들여쓰기 -->
        <module name="Indentation">
            <property name="basicOffset" value="4"/>
            <property name="braceAdjustment" value="0"/>
            <property name="caseIndent" value="4"/>
            <property name="throwsIndent" value="4"/>
            <property name="lineWrappingIndentation" value="4"/>
            <property name="arrayInitIndent" value="4"/>
        </module>
        
        <!-- 줄 길이 -->
        <module name="LineLength">
            <property name="max" value="120"/>
            <property name="ignorePattern" value="^package.*|^import.*|a href|href|http://|https://|ftp://"/>
        </module>
        
        <!-- 명명 규칙 -->
        <module name="TypeName"/>
        <module name="MethodName"/>
        <module name="ParameterName"/>
        <module name="LocalVariableName"/>
        <module name="ConstantName"/>
        <module name="MemberName"/>
        <module name="PackageName">
            <property name="format" value="^[a-z]+(\.[a-z][a-z0-9]*)*$"/>
        </module>
        
        <!-- Import 규칙 -->
        <module name="AvoidStarImport"/>
        <module name="UnusedImports"/>
        <module name="ImportOrder">
            <property name="groups" value="/^java\./,javax,org,com"/>
            <property name="ordered" value="true"/>
            <property name="separated" value="true"/>
            <property name="option" value="above"/>
        </module>
        
        <!-- 중괄호 -->
        <module name="LeftCurly"/>
        <module name="RightCurly"/>
        <module name="NeedBraces"/>
        
        <!-- 공백 -->
        <module name="WhitespaceAround"/>
        <module name="WhitespaceAfter"/>
        <module name="NoWhitespaceBefore"/>
        <module name="GenericWhitespace"/>
        
        <!-- 기타 스타일 -->
        <module name="EmptyLineSeparator">
            <property name="allowNoEmptyLineBetweenFields" value="true"/>
        </module>
        <module name="SeparatorWrap">
            <property name="id" value="SeparatorWrapDot"/>
            <property name="tokens" value="DOT"/>
            <property name="option" value="nl"/>
        </module>
        <module name="SeparatorWrap">
            <property name="id" value="SeparatorWrapComma"/>
            <property name="tokens" value="COMMA"/>
            <property name="option" value="EOL"/>
        </module>
        
        <!-- 어노테이션 -->
        <module name="AnnotationLocation">
            <property name="id" value="AnnotationLocationMostCases"/>
            <property name="tokens" value="CLASS_DEF, INTERFACE_DEF, ENUM_DEF, METHOD_DEF, CTOR_DEF"/>
        </module>
        <module name="AnnotationLocation">
            <property name="id" value="AnnotationLocationVariables"/>
            <property name="tokens" value="VARIABLE_DEF"/>
            <property name="allowSamelineMultipleAnnotations" value="true"/>
        </module>
    </module>
</module>
```

## 🎯 IDE 설정

### IntelliJ IDEA 설정
```java
// .editorconfig 파일
root = true

[*.java]
charset = utf-8
end_of_line = lf
indent_style = space
indent_size = 4
insert_final_newline = true
max_line_length = 120
trim_trailing_whitespace = true

[*.{xml,yml,yaml}]
indent_size = 2
```

### Eclipse 설정
```xml
<!-- eclipse-formatter.xml -->
<profiles version="13">
    <profile kind="CodeFormatterProfile" name="Bank Simulator" version="13">
        <setting id="org.eclipse.jdt.core.formatter.tabulation.char" value="space"/>
        <setting id="org.eclipse.jdt.core.formatter.tabulation.size" value="4"/>
        <setting id="org.eclipse.jdt.core.formatter.line_split" value="120"/>
        <setting id="org.eclipse.jdt.core.formatter.comment.line_length" value="120"/>
        <setting id="org.eclipse.jdt.core.formatter.brace_position_for_type_declaration" value="end_of_line"/>
        <setting id="org.eclipse.jdt.core.formatter.brace_position_for_method_declaration" value="end_of_line"/>
        <setting id="org.eclipse.jdt.core.formatter.brace_position_for_block" value="end_of_line"/>
    </profile>
</profiles>
```

## 📏 품질 지표

### 코드 품질 목표
- **순환 복잡도**: 메서드당 10 이하
- **코드 중복**: 5% 이하
- **테스트 커버리지**: 라인 커버리지 80% 이상
- **기술 부채 비율**: 5% 이하 (SonarQube 기준)
- **코드 냄새**: 0개 (Critical/Blocker 수준)

### 정적 분석 실행 명령어
```bash
# 전체 품질 검사 실행
./mvnw clean verify

# 개별 도구 실행
./mvnw checkstyle:check
./mvnw pmd:check  
./mvnw spotbugs:check
./mvnw sonar:sonar

# 코드 커버리지 확인
./mvnw test jacoco:report
```

## 🔍 Claude Code 체크리스트

### 개발 시작 전
- [ ] 프로젝트 구조가 표준을 따르는가?
  - `com.example.bank` 패키지 구조 준수
  - controller/service/domain 레이어 분리
- [ ] 패키지 명명이 올바른가?
  - 소문자, 점(.) 구분, 역방향 도메인 방식
- [ ] 기본 설정이 올바른가?
  - UTF-8 인코딩, Space 4칸 들여쓰기, LF 줄바꿈

### 코드 작성 시
- [ ] 명명 규칙을 따르는가?
  - 클래스: PascalCase (`AccountService`)
  - 메서드: camelCase, 동사 시작 (`createAccount`)
  - 변수: camelCase (`accountHolder`)
  - 상수: CONSTANT_CASE (`MAX_RETRY_COUNT`)
  - boolean 메서드: is/has/can 접두사 (`isActive()`)
- [ ] 코딩 스타일이 올바른가?
  - K&R 중괄호 스타일
  - 연산자 양쪽 공백
  - 100-120자 줄 길이 제한
- [ ] Import 순서와 규칙이 올바른가?
  - Java 표준 → 서드파티 → 프로젝트 내부 → static
  - 와일드카드(*) import 금지
  - 사용하지 않는 import 제거
- [ ] Javadoc 주석이 작성되었는가?
  - 모든 public 메서드에 필수
  - @param, @return, @throws 태그 포함
  - 한글로 명확한 설명

### 아키텍처 준수
- [ ] 계층화 구조를 따르는가?
  - Controller → Service → Domain 흐름
  - 각 계층의 책임 분리
- [ ] 동시성 제어가 적절한가?
  - AtomicLong으로 ID 생성
  - synchronized 메서드로 상태 변경 보호
  - ConcurrentHashMap으로 스레드 안전한 저장소
- [ ] 예외 처리가 적절한가?
  - 구체적인 예외 타입 사용
  - 빈 catch 블록 금지
  - 예외 로깅 시 스택 트레이스 포함

### Spring Boot 특화
- [ ] 어노테이션이 올바르게 사용되었는가?
  - @Service, @Controller, @RestController 적절한 사용
  - @RequestParam, @PathVariable 파라미터 처리
  - @Transactional 트랜잭션 관리
- [ ] 보안이 고려되었는가?
  - 입력값 검증 (@Valid, 커스텀 검증)
  - 민감정보 로깅 방지
  - SQL Injection 방지
- [ ] 성능이 최적화되었는가?
  - 적절한 컬렉션 초기 용량 설정
  - StringBuilder 사용 (많은 문자열 연결)
  - 불변 컬렉션 반환

### 테스트 코드
- [ ] 테스트 명명 규칙을 따르는가?
  - `methodName_condition_expectedResult` 패턴
  - 한글 @DisplayName 사용
- [ ] 테스트 구조가 올바른가?
  - Given-When-Then (AAA) 패턴
  - @BeforeEach 초기화
  - 독립적인 테스트 작성
- [ ] 테스트 커버리지가 충분한가?
  - 라인 커버리지 80% 이상
  - 주요 비즈니스 로직 테스트
  - 예외 상황 테스트

### 코드 리뷰 시
- [ ] 비즈니스 로직이 올바른가?
  - 계좌 생성, 입출금, 송금 로직 검증
  - 잔액 부족 등 예외 상황 처리
- [ ] 동시성 문제가 없는가?
  - Race condition 방지
  - Deadlock 방지 (송금 시 ID 순서 락킹)
- [ ] 코드 품질이 충족되는가?
  - 정적 분석 도구 통과
  - 코드 중복 최소화
  - 순환 복잡도 적정 수준

### 배포 전
- [ ] 모든 테스트가 통과하는가?
- [ ] 정적 분석 결과가 기준을 만족하는가?
- [ ] 문서가 업데이트되었는가?
- [ ] 로그 레벨이 적절히 설정되었는가?

## 📊 품질 보고서 생성

### 종합 품질 보고서 생성
```bash
# Maven site 생성 (모든 리포트 포함)
./mvnw site

# 개별 리포트 생성
./mvnw checkstyle:checkstyle     # 스타일 검사 리포트
./mvnw pmd:pmd                   # PMD 리포트
./mvnw spotbugs:spotbugs         # SpotBugs 리포트
./mvnw jacoco:report             # 커버리지 리포트
```

### 지속적인 품질 관리
```yaml
# GitHub Actions 예시 (.github/workflows/quality.yml)
name: Code Quality Check
on: [push, pull_request]

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run quality checks
        run: |
          ./mvnw clean verify
          ./mvnw checkstyle:check
          ./mvnw pmd:check
          ./mvnw spotbugs:check
      
      - name: Generate test report
        run: ./mvnw surefire-report:report
```

이러한 코드 품질 관리 표준을 통해 일관되고 높은 품질의 코드를 유지할 수 있습니다.
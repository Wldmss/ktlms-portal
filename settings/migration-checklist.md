# Portal 기반 Genius/LMS 마이그레이션 체크리스트

## 0. 전제

- [ ] `portal`은 `JDK 21`, `Spring 6`, `Spring Security`, `JWT`, `JBoss 38.0.1` 기준의 표준 베이스 프로젝트로 확정한다.
- [ ] `genius`는 교육포털 사용자 사이트 레거시 프로젝트로 본다.
- [ ] `lms`는 교육포털 어드민 사이트 레거시 프로젝트로 본다.
- [ ] `new-genius`는 `portal`을 복제한 뒤 `genius` 코드를 이관한 신규 사용자 사이트로 만든다.
- [ ] `new-admin`은 `portal`을 복제한 뒤 `lms` 코드를 이관한 신규 어드민 사이트로 만든다.
- [ ] 이관은 메뉴 단위로 진행한다.
- [ ] Java/JSP 코드는 가능한 최소 수정한다.
- [ ] Spring 6, JDK 21 기준에서 유지 비용이 큰 구기술은 대체하거나 제거한다.
- [ ] 미사용 설정, 변수, 파일, 라이브러리는 이관하지 않는다.

## 1. Portal 공통 베이스라인 완료 체크리스트

### 1.1 빌드/런타임

- [ ] `JDK 21` 기준으로 로컬 빌드가 성공한다.
- [ ] `pom.xml`의 Java 버전, Spring 버전, dependency scope가 정리되어 있다.
- [ ] WAR 패키징 기준이 확정되어 있다.
- [ ] JBoss 38.0.1 배포 기준 파일이 정리되어 있다.
- [ ] `jboss-web.xml`의 context path 정책이 정리되어 있다.
- [ ] `jboss-deployment-structure.xml`의 module exclude/include 기준이 정리되어 있다.
- [ ] 로컬, 개발, 운영 profile 실행 방식이 문서화되어 있다.
- [ ] IntelliJ 실행 설정에 필요한 VM option/system property가 문서화되어 있다.

### 1.2 프로젝트 구조

- [ ] Java package root를 `com.kt.ktedu` 기준으로 확정한다.
- [ ] 기존 `anymobi`, `ktlms` 같은 레거시 상위 package 이동 규칙을 확정한다.
- [ ] `auth`, `core`, `common`, 업무 도메인 package 경계를 확정한다.
- [ ] `src/main/resources/conf` 하위 설정 구조를 확정한다.
- [ ] `src/main/webapp/resources` 하위 front resource 구조를 확정한다.
- [ ] `WEB-INF/views` 하위 JSP 배치 규칙을 확정한다.
- [ ] `WEB-INF/views/common/layout`의 공통 include 구조를 확정한다.
- [ ] `WEB-INF/views/direct` 사용 기준을 확정한다.

### 1.3 Spring 설정

- [ ] XML 설정은 원칙적으로 `root-context.xml`, `servlet-context.xml`만 유지한다.
- [ ] 기존 `KT-properties.xml` 역할은 `application.properties` 계열로 대체한다.
- [ ] 기존 `KT-interceptors.xml` 권한 제어는 Spring Security 기준으로 대체한다.
- [ ] MyBatis 설정 위치와 mapper scan 규칙을 확정한다.
- [ ] transaction manager 설정을 확정한다.
- [ ] component scan 범위를 확정한다.
- [ ] view resolver prefix/suffix를 확정한다.
- [ ] static resource handler 경로를 확정한다.

### 1.4 Properties/Profile

- [ ] 공통 공개 설정은 `application.properties`에 둔다.
- [ ] 환경별 민감 설정은 `application-local.properties`, `application-dev.properties`, `application-prod.properties`로 분리한다.
- [ ] Git에 올릴 sample 파일 생성 기준을 확정한다.
- [ ] Unicode escape를 제거하고 UTF-8 기준으로 관리한다.
- [ ] 레거시 properties 중 실제 사용되는 key만 이관한다.
- [ ] 메뉴 이관 중 property가 필요할 때 추가하는 절차를 문서화한다.
- [ ] 삭제한 property에 대한 추적 기준을 정한다.

### 1.5 Spring Security/JWT

- [ ] Security filter chain 구성이 확정되어 있다.
- [ ] 로그인 URL, public URL, static resource URL whitelist가 정리되어 있다.
- [ ] JWT 발급, 검증, 만료, refresh 정책이 정리되어 있다.
- [ ] 인증 실패 응답 기준이 정리되어 있다.
- [ ] 권한 부족 응답 기준이 정리되어 있다.
- [ ] Controller에서 현재 사용자 정보를 얻는 표준 방법이 정리되어 있다.
- [ ] `@EnableMethodSecurity`와 `@PreAuthorize` 사용 기준이 확정되어 있다.
- [ ] 메뉴/URL 권한은 `SecurityConfig`, 업무 메서드 권한은 `@PreAuthorize`로 나누는 기준이 정리되어 있다.
- [ ] 레거시 session key를 JWT claim 또는 사용자 context로 매핑하는 기준이 정리되어 있다.
- [ ] 공통 AJAX 요청에서 Authorization header를 붙이는 방식이 구현되어 있다.
- [ ] JWT 만료 시 front 처리 방식이 구현되어 있다.

### 1.6 Exception/Response

- [ ] 공통 exception handler가 구현되어 있다.
- [ ] AJAX 오류 응답 포맷이 확정되어 있다.
- [ ] 화면 redirect/message 처리 방식이 확정되어 있다.
- [ ] 권한 오류, 인증 오류, validation 오류, system 오류 응답이 분리되어 있다.
- [ ] 기존 대량평가 등 특수 exception 처리 방식이 공통화되어 있다.
- [ ] 이관자가 exception을 어떻게 바꿔야 하는지 가이드가 있다.

### 1.7 공통 Util

- [ ] AES/RSA/GCM 등 crypto util의 사용 기준이 정리되어 있다.
- [ ] 파일 업로드/다운로드 util의 사용 기준이 정리되어 있다.
- [ ] 날짜/시간 변환 util 사용 기준이 정리되어 있다.
- [ ] 문자열/null 처리 util 사용 기준이 정리되어 있다.
- [ ] paging 관련 공통 객체 또는 property 사용 기준이 정리되어 있다.
- [ ] XSS 처리 방식과 적용 위치가 정리되어 있다.
- [ ] 신규 util 추가 기준이 정리되어 있다.

### 1.8 Front 공통

- [ ] 공통 CSS, header CSS, login CSS 기준이 정리되어 있다.
- [ ] `common.js`, `common-ajax.js`, `formatter.js`, `header.js` 역할이 정리되어 있다.
- [ ] AJAX 공통 성공/오류/인증만료 처리가 구현되어 있다.
- [ ] modal, alert, loading, snackbar 공통 JSP 사용법이 정리되어 있다.
- [ ] 공통 header, gnb, footer include 구조가 확정되어 있다.
- [ ] 기존 화면을 JSP include 구조로 옮기는 샘플이 있다.
- [ ] popup 화면, iframe 화면, mobile 화면의 예외 기준이 정리되어 있다.

## 2. 복제 전 필수 가이드 작성 체크리스트

### 2.1 공통 이관 원칙 가이드

- [ ] 이관 단위를 메뉴 기준으로 정의한다.
- [ ] 메뉴별 이관 산출물을 정의한다.
- [ ] Java/JSP 최소 수정 원칙을 정의한다.
- [ ] 미사용 파일 판단 기준을 정의한다.
- [ ] 신규 dependency 추가 승인 기준을 정의한다.
- [ ] 신규 property 추가 승인 기준을 정의한다.
- [ ] package 이동 규칙을 정의한다.
- [ ] 파일명, class명, mapper명 유지/변경 기준을 정의한다.
- [ ] commit/branch/PR 단위를 정의한다.

### 2.2 Tiles 전환 가이드

- [ ] 기존 tiles definition 파일 위치를 식별하는 방법을 작성한다.
- [ ] tiles layout, header, body, footer, attribute 매핑 방법을 작성한다.
- [ ] 기본 화면은 `common-top.jsp` + content + `common-bottom.jsp`로 전환하는 기준을 작성한다.
- [ ] GNB 없는 화면은 `default-top.jsp` + content + `default-bottom.jsp`로 전환하는 기준을 작성한다.
- [ ] popup/modal 화면은 별도 layout을 쓰는 기준을 작성한다.
- [ ] nested tiles 처리 기준을 작성한다.
- [ ] mobile tiles 처리 기준을 작성한다.
- [ ] tiles attribute를 request attribute 또는 JSP include parameter로 바꾸는 방법을 작성한다.
- [ ] tiles 제거 후 테스트해야 할 화면 요소를 정의한다.

### 2.3 Interceptor/Security 전환 가이드

- [ ] `KT-interceptors.xml`의 interceptor 목록을 기능별로 분류한다.
- [ ] 인증/인가 interceptor는 Spring Security로 대체하는 기준을 작성한다.
- [ ] URL 권한은 `SecurityConfig.authorizeHttpRequests`로 옮기는 기준을 작성한다.
- [ ] 업무 권한은 `@PreAuthorize`로 옮기는 기준을 작성한다.
- [ ] 단순 logging, locale, 공통 model attribute는 `HandlerInterceptor` 유지 가능 기준을 작성한다.
- [ ] interceptor exclude path를 security whitelist로 옮기는 기준을 작성한다.
- [ ] 권한 코드/role naming 규칙을 작성한다.
- [ ] JSP에서 권한별 버튼 노출을 처리하는 기준을 작성한다.

### 2.4 Session -> JWT 전환 가이드

- [ ] 레거시 session key 목록화 방법을 작성한다.
- [ ] session key별 JWT claim/user context 매핑표 양식을 만든다.
- [ ] Controller에서 session 대신 현재 사용자 정보를 얻는 방법을 작성한다.
- [ ] JSP에서 사용자 정보를 참조하는 방법을 작성한다.
- [ ] AJAX에서 JWT header를 보내는 방법을 작성한다.
- [ ] token 만료 시 화면 이동/재로그인 처리 기준을 작성한다.
- [ ] 서버에서 session 생성이 필요한 예외 상황이 있는지 기준을 작성한다.

### 2.5 Direct JSP 전환 가이드

- [ ] 기존 `/WebContent` 하위 direct JSP 식별 방법을 작성한다.
- [ ] 단순 정적 JSP는 `WebConfig` view controller로 등록하는 기준을 작성한다.
- [ ] 파라미터 검증, 권한, 조회 로직이 있으면 Controller로 승격하는 기준을 작성한다.
- [ ] direct JSP의 target path를 `WEB-INF/views/direct`로 옮기는 기준을 작성한다.
- [ ] `.do` suffix 제거 또는 redirect 기준을 작성한다.
- [ ] direct URL 테스트 체크리스트를 작성한다.

### 2.6 JDK21/Spring6/Jakarta 전환 가이드

- [ ] `javax.*`를 `jakarta.*`로 바꾸는 범위를 작성한다.
- [ ] Servlet, JSP, Validation, Annotation 관련 import 변경 기준을 작성한다.
- [ ] `Date`, `Calendar`, `SimpleDateFormat`을 유지할지 `java.time`으로 바꿀지 기준을 작성한다.
- [ ] encoding 처리 기준을 UTF-8로 통일한다.
- [ ] deprecated API 처리 기준을 작성한다.
- [ ] reflection, classloader, resource loading 코드 점검 기준을 작성한다.
- [ ] Java 7 문법을 Java 21에서 유지/개선할 기준을 작성한다.
- [ ] 컴파일 오류 유형별 수정 예시를 작성한다.

### 2.7 iBatis -> MyBatis 전환 가이드

- [ ] iBatis mapper 위치와 namespace 식별 방법을 작성한다.
- [ ] DAO 호출 방식을 MyBatis mapper interface 또는 `SqlSession` 기준으로 정리한다.
- [ ] parameterClass/resultClass 전환 기준을 작성한다.
- [ ] dynamic SQL 전환 예시를 작성한다.
- [ ] `#value#`, `$value$` 차이와 SQL injection 주의사항을 작성한다.
- [ ] resultMap 전환 기준을 작성한다.
- [ ] paging query 전환 기준을 작성한다.
- [ ] mapper 테스트 방법을 작성한다.

### 2.8 Properties/Library 추가 가이드

- [ ] property key naming 규칙을 작성한다.
- [ ] 환경별 property 추가 위치를 작성한다.
- [ ] sample property 갱신 절차를 작성한다.
- [ ] 신규 library 추가 전 확인 항목을 작성한다.
- [ ] Nexus에 없는 jar를 `WEB-INF/lib`에 둘 때의 기준을 작성한다.
- [ ] 중복/구버전 library 제거 기준을 작성한다.
- [ ] POI, lucy-xss 등 주요 라이브러리 변경 시 코드 수정 기준을 작성한다.

## 3. Genius/LMS 이관 대상 인벤토리 체크리스트

### 3.1 메뉴 인벤토리

- [ ] 메뉴 ID를 정리한다.
- [ ] 메뉴명을 정리한다.
- [ ] 현재 URL을 정리한다.
- [ ] 신규 URL을 정리한다.
- [ ] 사용자 권한/role을 정리한다.
- [ ] 연결된 controller를 정리한다.
- [ ] 연결된 service를 정리한다.
- [ ] 연결된 DAO/mapper를 정리한다.
- [ ] 연결된 JSP를 정리한다.
- [ ] 연결된 JS/CSS/image를 정리한다.
- [ ] 연결된 properties key를 정리한다.
- [ ] 연결된 library를 정리한다.
- [ ] session 사용 여부를 정리한다.
- [ ] tiles 사용 여부를 정리한다.
- [ ] direct JSP 여부를 정리한다.
- [ ] popup/modal/mobile 여부를 정리한다.

### 3.2 파일 분류

- [ ] 이관 대상 파일을 표시한다.
- [ ] 삭제 대상 파일을 표시한다.
- [ ] 공통화 후보 파일을 표시한다.
- [ ] 대체 기술로 전환할 파일을 표시한다.
- [ ] 보류 파일을 표시한다.
- [ ] 사용 여부가 불명확한 파일은 호출 경로를 확인한다.
- [ ] 사용되지 않는 JSP, JS, CSS, image는 이관하지 않는다.
- [ ] 사용되지 않는 XML 설정은 이관하지 않는다.
- [ ] 사용되지 않는 property는 이관하지 않는다.

## 4. New-Genius/New-Admin 생성 체크리스트

- [ ] portal 기준 소스를 복제한다.
- [ ] 프로젝트명을 `new-genius`, `new-admin`으로 각각 변경한다.
- [ ] artifactId를 각각 변경한다.
- [ ] context path를 각각 변경한다.
- [ ] application name을 각각 변경한다.
- [ ] log path/log file name을 각각 변경한다.
- [ ] profile property sample을 각각 변경한다.
- [ ] JBoss 배포 파일명을 각각 변경한다.
- [ ] README의 실행 방법을 각각 변경한다.
- [ ] IntelliJ module 설정을 각각 확인한다.
- [ ] 빈 상태에서 로컬 build가 성공한다.
- [ ] 빈 상태에서 JBoss 배포가 성공한다.
- [ ] 기본 로그인/보안 흐름이 동작한다.
- [ ] 기본 sample 화면이 동작한다.

## 5. 메뉴별 이관 체크리스트

### 5.1 이관 전

- [ ] 메뉴 인벤토리가 작성되어 있다.
- [ ] 기존 화면 URL과 신규 화면 URL이 정해져 있다.
- [ ] 권한 정책이 정해져 있다.
- [ ] 사용하는 파일 목록이 확정되어 있다.
- [ ] 사용하는 property 목록이 확정되어 있다.
- [ ] 사용하는 library 목록이 확정되어 있다.
- [ ] session 사용 항목이 정리되어 있다.
- [ ] tiles/direct 여부가 정리되어 있다.
- [ ] 이관하지 않을 파일이 정리되어 있다.

### 5.2 Java 이관

- [ ] Controller를 신규 package 규칙에 맞게 옮긴다.
- [ ] Service를 신규 package 규칙에 맞게 옮긴다.
- [ ] DAO 또는 mapper 호출 방식을 신규 기준에 맞게 옮긴다.
- [ ] `javax.*` import를 `jakarta.*`로 변경한다.
- [ ] 컴파일 오류를 최소 범위로 수정한다.
- [ ] session 직접 접근 코드를 JWT/user context 방식으로 변경한다.
- [ ] 권한 체크를 `SecurityConfig` 또는 `@PreAuthorize`로 변경한다.
- [ ] exception 처리 방식을 공통 exception 기준에 맞춘다.
- [ ] 파일 업로드/다운로드는 공통 util 기준에 맞춘다.
- [ ] 암복호화는 공통 crypto util 기준에 맞춘다.

### 5.3 Mapper/SQL 이관

- [ ] 실제 사용하는 mapper만 이관한다.
- [ ] iBatis mapper를 MyBatis 기준으로 변경한다.
- [ ] namespace를 신규 기준에 맞춘다.
- [ ] parameter/result mapping을 확인한다.
- [ ] dynamic SQL을 MyBatis 기준으로 변경한다.
- [ ] SQL injection 위험이 있는 `$` 사용을 점검한다.
- [ ] paging query를 신규 기준에 맞춘다.
- [ ] mapper scan 경로에 포함되는지 확인한다.

### 5.4 JSP 이관

- [ ] JSP를 `WEB-INF/views` 하위 신규 위치로 옮긴다.
- [ ] tiles layout을 JSP include 구조로 변경한다.
- [ ] 공통 header/gnb/footer include를 적용한다.
- [ ] popup/modal 화면은 전용 layout 기준을 적용한다.
- [ ] direct JSP는 `direct` 기준 또는 Controller 기준으로 전환한다.
- [ ] session 참조를 신규 사용자 context 방식으로 변경한다.
- [ ] JSTL/Spring tag import가 Jakarta/Spring 6 환경에서 동작하는지 확인한다.
- [ ] hard-coded path를 context path 기준으로 점검한다.
- [ ] encoding이 UTF-8로 동작하는지 확인한다.

### 5.5 Front Resource 이관

- [ ] 실제 사용하는 JS만 이관한다.
- [ ] 실제 사용하는 CSS만 이관한다.
- [ ] 실제 사용하는 image/font/icon만 이관한다.
- [ ] 공통 AJAX 호출 방식으로 변경한다.
- [ ] JWT header가 필요한 요청에 적용되어 있다.
- [ ] legacy global function 충돌이 없는지 확인한다.
- [ ] vendor library 중복이 없는지 확인한다.
- [ ] 브라우저 console 오류가 없는지 확인한다.

### 5.6 Properties/Library 반영

- [ ] 필요한 property만 추가한다.
- [ ] 환경별 property sample을 갱신한다.
- [ ] property key naming 규칙을 따른다.
- [ ] 필요한 library만 추가한다.
- [ ] 기존 dependency와 중복되지 않는지 확인한다.
- [ ] JDK21/Spring6/Jakarta 호환 버전인지 확인한다.
- [ ] Nexus에 없는 jar 사용 사유가 기록되어 있다.

### 5.7 메뉴 검증

- [ ] 신규 URL로 화면이 열린다.
- [ ] 로그인 전 접근 제어가 동작한다.
- [ ] 권한 없는 사용자 접근 제어가 동작한다.
- [ ] 정상 권한 사용자 기능이 동작한다.
- [ ] 목록/상세/등록/수정/삭제 기능이 동작한다.
- [ ] popup/modal 기능이 동작한다.
- [ ] 파일 업로드/다운로드가 있으면 동작한다.
- [ ] Excel/POI 기능이 있으면 동작한다.
- [ ] 한글 입력/조회/다운로드 encoding이 정상이다.
- [ ] AJAX 오류 응답 처리가 정상이다.
- [ ] 서버 log에 예상치 못한 error가 없다.

## 6. 파일럿 이관 체크리스트

- [ ] genius에서 대표 메뉴 1개를 선정한다.
- [ ] lms에서 대표 메뉴 1개를 선정한다.
- [ ] 선정 메뉴에 JSP, controller, mapper, properties, 권한, session, tiles/direct 중 최대한 많은 유형이 포함되어 있다.
- [ ] 파일럿 메뉴를 끝까지 이관한다.
- [ ] 파일럿 중 나온 예외 케이스를 가이드에 반영한다.
- [ ] 파일럿 후 메뉴별 체크리스트를 수정한다.
- [ ] 파일럿 후 신규 프로젝트 구조를 확정한다.
- [ ] 파일럿 완료 전에는 대량 분업을 시작하지 않는다.

## 7. 최종 검증 체크리스트

### 7.1 빌드/배포

- [ ] 로컬 build가 성공한다.
- [ ] 테스트 profile build가 성공한다.
- [ ] WAR 생성이 정상이다.
- [ ] JBoss 38.0.1 배포가 성공한다.
- [ ] 서버 기동 시 fatal/error log가 없다.
- [ ] profile별 property loading이 정상이다.

### 7.2 보안

- [ ] 로그인 성공/실패가 정상이다.
- [ ] JWT 발급/검증이 정상이다.
- [ ] JWT 만료 처리가 정상이다.
- [ ] refresh 정책이 정상이다.
- [ ] public URL 접근이 정상이다.
- [ ] 보호 URL 접근 제어가 정상이다.
- [ ] role별 메뉴 접근 제어가 정상이다.
- [ ] CSRF 적용 여부와 예외 기준이 정리되어 있다.
- [ ] XSS 처리 방식이 정상이다.

### 7.3 화면/기능

- [ ] 공통 layout이 정상이다.
- [ ] header/gnb/footer가 정상이다.
- [ ] 메뉴 이동이 정상이다.
- [ ] direct JSP 화면이 정상이다.
- [ ] popup/modal 화면이 정상이다.
- [ ] mobile 화면이 있으면 정상이다.
- [ ] 공통 AJAX 처리가 정상이다.
- [ ] 공통 error page가 정상이다.

### 7.4 데이터/파일

- [ ] DB 연결이 정상이다.
- [ ] transaction rollback이 정상이다.
- [ ] MyBatis mapper loading이 정상이다.
- [ ] 파일 업로드가 정상이다.
- [ ] 파일 다운로드가 정상이다.
- [ ] Excel 다운로드/업로드가 정상이다.
- [ ] 한글 파일명 처리가 정상이다.

## 8. 메뉴별 작업 기록 양식

```markdown
## 메뉴명

- 기존 프로젝트:
- 신규 프로젝트:
- 기존 URL:
- 신규 URL:
- 권한/Role:
- 담당자:
- 상태: 대기 / 진행 / 검토 / 완료 / 보류

### 이관 파일

- Controller:
- Service:
- DAO/Mapper:
- JSP:
- JS:
- CSS:
- Image/Font:
- Properties:
- Library:

### 변경 사항

- Tiles 전환:
- Session -> JWT 전환:
- Interceptor/Security 전환:
- Direct JSP 전환:
- JDK21/Jakarta 수정:
- iBatis/MyBatis 수정:

### 검증

- [ ] 빌드 성공
- [ ] 화면 접근 성공
- [ ] 권한 확인
- [ ] 주요 기능 확인
- [ ] AJAX 확인
- [ ] 파일/Excel 확인
- [ ] 서버 로그 확인

### 메모

-
```

## 9. 완료 판단 기준

- [ ] portal 공통 베이스라인이 복제 가능한 상태다.
- [ ] new-genius/new-admin이 portal 복제 후 빈 상태로 실행 가능하다.
- [ ] 필수 가이드가 작성되어 있다.
- [ ] 파일럿 메뉴 이관이 완료되어 있다.
- [ ] 파일럿 결과가 가이드와 체크리스트에 반영되어 있다.
- [ ] 메뉴별 이관자가 같은 방식으로 작업할 수 있다.
- [ ] 미사용 파일/설정/라이브러리를 이관하지 않는 기준이 작동한다.
- [ ] JDK21/Spring6/JBoss 38.0.1 기준으로 빌드와 배포가 가능하다.

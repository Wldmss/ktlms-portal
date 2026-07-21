# Genius 폴더 단위 이관 계획

기준 목록은 [genius-file-list.md](genius-file-list.md)이다. 현재 `[x] 제외`로 표시한 파일은 이관하지 않고, 체크되지 않은 파일은 이관 후보로 본다. 로그인 화면·로그인 전용 Java/XML은 전부 제외하고 portal의 현재 Spring Security/JWT 로그인 구현을 사용한다.

## 1. 폴더 이관 단위

- 제외 파일이 하나도 없는 폴더는 해당 폴더를 통째로 이관한다.
- 제외 파일이 섞인 폴더는 제외 파일의 바로 상위 공통 하위 폴더까지만 내려가서, 이관 후보 파일만 묶어 이관한다. 제외 파일을 임시 복사한 뒤 삭제하는 방식은 사용하지 않는다.
- 폴더를 옮긴 뒤 같은 폴더의 JSP → Java → XML 의존성을 확인하고, 파일 목록의 체크 상태를 `[x] 이관`으로 갱신한다.
- `adm`, `batch`, 구형 라이브러리 등 전부 제외된 폴더는 이관 대상에서 제거한다.

## 2. 실행 순서

### Phase 1 — JSP 공통 레이아웃 및 화면

1. **1차 legacy 기반**: genius `web/layout`의 header/footer/GNB/search/popup 조각을 `WEB-INF/views/legacy` 하위에 원본 계열로 이관한다. 이 단계에서는 portal `common/layout`으로 합치지 않고, 버전업·Jakarta·경로·JWT 연동에 필요한 최소 수정만 한다.
2. **Tiles 매핑 변환**: `web/tiles`의 layout JSP와 definition을 최종 공통 layout으로 옮기지 않는다. 업무 JSP를 옮길 때 원본 Tiles definition을 확인해 portal의 기존 `common/tiles` include를 사용한다. 단, 1차 단계의 `tiles-top/bottom`, `m-top/bottom` 내부 header/footer/GNB 의존성은 `legacy`에 이관한 genius 조각을 가리키도록 최소 수정한다. 따라서 wrapper 구조는 portal `common/tiles`, 실제 shell 구현은 `legacy`에 남긴다.
   - 웹 GNB 있음: `common/tiles/web/tiles-top` → 본문 → `common/tiles/web/tiles-bottom`
   - 웹 GNB 없음: 해당 legacy/default 계열 top/bottom include
   - 모바일: `common/tiles/mobile/m-top` → 본문 → `m-bottom` 또는 `m-main-top`/`m-main-bottom`
   - 팝업/특수 화면: shell을 생략하거나 popup 전용 include
3. **리소스 분리 및 매핑**: CSS/JS/이미지는 이미 이관된 `src/main/webapp/resources/legacy`의 원본 계열을 그대로 사용한다. JSP에서는 `${pageContext.request.contextPath}/resources/legacy/...`로 참조하고 `WEB-INF/views/legacy` 아래에는 리소스를 중복 복사하지 않는다. `common/core/resources.jsp`와 `common/core/script.jsp`에는 모든 메뉴가 사용하는 전역 리소스만 둔다. 메뉴 전용 리소스는 해당 `resources/legacy` 경로를 페이지별로 include하며, CSS는 top 처리 구간, JS는 bottom 처리 구간에서 로드한다. 전역 파일에 메뉴 선택자·이벤트를 추가하지 않는다.
4. **본문 JSP 구조 정리**: top/bottom include가 `html/head/body/main`을 담당하므로 이관하는 본문 JSP의 중복 문서 구조와 중복 header/footer를 제거한다. 본문은 content 영역과 페이지별 리소스 선언만 남긴다.
5. **진입 화면**: `web/main`, `web/external`, `web/redirect.jsp`, `web/error`를 이관한다. `web/login`, `mobile/login`, `index.jsp`는 전부 제외하고 portal의 JWT/Spring Security 로그인 화면·흐름을 사용한다. `meta.jsp`의 `window._isMobile`을 기준으로 모바일 경로를 처리한다.
6. **업무 화면**: 제외 파일이 없는 `web/academy`, `web/azure`, `web/campaign`, `web/cash`, `web/cds`, `web/course`, `web/education`, `web/exam`, `web/expert`, `web/search`, `web/selfDev`, `web/support`, `web/tutor`를 폴더 단위로 이관한다.
7. **혼합 폴더**: `web/dept`, `web/educontents`, `web/learncom`, `web/myclass` 및 `mobile/mobile/m`은 제외된 하위 기능을 건너뛰고 후보 하위 폴더만 이관한다. 각 페이지의 Tiles 매핑과 데스크톱/모바일 include를 기록한다.
8. JSP compile/render, legacy header/footer/GNB 중복 여부, 리소스 범위, 모바일 분기와 링크를 검증한 후 다음 단계로 진행한다.

### Phase 1.5 — 공통 layout 승격

업무 화면이 안정화된 뒤 기능 단위로 하나씩 `legacy`의 header/footer/GNB를 `common/layout/gnb`, `common/layout/footer` 등으로 옮긴다. 승격 시 해당 기능의 페이지를 `common-top`/`common-bottom` 또는 `default-top`/`default-bottom`으로 바꾸고, 회귀 테스트 후 원본 legacy 조각을 제거한다. 모든 기능을 한 번에 옮기지 않는다.

### Phase 2 — Java

1. `src/com/anymobi/usr`와 `src/com/ktlms/common`을 우선 이관한다. 단, 로그인 전용 `usr/.../login` 패키지는 전부 제외한다.
2. `src/com/anymobi/common`은 제외 표시되지 않은 클래스만 이관하고, portal 공통 기능과 중복되는 클래스는 매핑표를 작성한 뒤 중복 이관하지 않는다.
3. `anymobi/adm`, `anymobi/icampus`는 남은 후보만 실제 JSP/컨트롤러 참조를 확인해 이관한다.
4. 전부 제외된 `batch`, `credu`, `kt/edu`, `oreilly`는 이관하지 않는다. Java 이관 시 package 경로만 `com.kt.ktedu.legacy...`로 변경하고 파일명·주석은 보존한다.
5. JSP의 URL/폼 액션과 Java의 Controller 매핑에서 `.do`를 제거하고, Session 직접 접근은 JWT/Spring Security 방식으로 치환한다.

### Phase 3 — XML

1. `src/conf/sql/usr` 하위 mapper를 기능 폴더별로 Java 이관 직후 옮긴다. 로그인 mapper(`usr/login`)와 현재 제외된 `usr/icampus`는 제외한다.
2. 후보로 남은 `usr/{academy,alarm,api,campaign,cds,common,course,dept,deptmng,education,educontents,exam,expert,external,learncom,main,myclass,selfDevelop,setting,support,tutor}`를 순서대로 처리한다.
3. `adm`, `batch`, `common`, `mail`에서 제외된 mapper는 옮기지 않는다. `sms/SmsSql.xml`과 `adm/system`처럼 남은 후보는 실제 Java 참조가 있을 때만 이관한다.
4. SQL mapper 이외의 Spring/Web XML은 mapper 목록과 분리하여 portal 설정(`application`, Security, MVC)으로 흡수할지 검토한다. 원본 설정을 무조건 복사하지 않는다.

## 3. 실제 착수 순서

### 1단계 — 폴더 구조 이관(실행 전)

이 단계에서는 화면 실행, 기능 연결, 공통 layout 승격을 하지 않는다. 제외 체크를 적용해 파일을 원본 계열 그대로 배치하는 것이 목표다.

#### Step 0 — 이관 기준 고정

- 현재 portal 로그인/JWT/Security 동작을 기준선으로 고정한다. genius 로그인 JSP·Java·XML은 작업 대상에서 제외한다.
- `genius-file-list.md`에서 작업할 폴더의 제외 상태를 먼저 확정하고, `migration-list.md`에 원본 폴더와 portal 대상 폴더를 등록한다.
- `resources/legacy`의 CSS/JS/이미지는 추가 복사하지 않고 원본 계열 URL로 참조한다.

#### Step 1 — JSP 폴더 이관

- genius header/footer/GNB/layout 조각을 `WEB-INF/views/legacy` 아래 원본 계열로 배치한다.
- Tiles 업무 JSP는 원본 폴더와 파일명을 유지해 `views/legacy`에 배치하고, 필요한 경우에만 Jakarta/JSTL/include 경로 같은 버전업 최소 수정만 한다.
- `common/tiles` wrapper 연결, `meta.jsp`의 `window._isMobile` 적용, CSS/JS URL 연결은 실행 단계에서 처리한다. 이 단계에서는 구조와 대상 파일만 맞춘다.
- `web/login`, `mobile/login`, `index.jsp` 등 제외 항목은 복사하지 않는다.

#### Step 2 — Java 폴더 이관

- JSP 후보 전체를 먼저 배치한 뒤, 제외되지 않은 Java를 원본 패키지 계열에 맞춰 `com.kt.ktedu.legacy` 아래에 배치한다.
- 파일명·주석·디렉터리 구조를 유지하고, 컴파일을 막는 JDK21/Jakarta 문법 변경만 최소 적용한다.
- 로그인 패키지와 portal에 이미 구현된 인증/공통 기능은 복사하지 않는다.

#### Step 3 — XML 폴더 이관

- Java 후보 배치가 끝난 뒤 대응 SQL mapper를 `src/main/resources/mapper/legacy` 아래 원본 계열로 배치한다.
- namespace·statement id·resultMap은 구조를 유지하고, MyBatis/JDK21 전환에 필요한 최소 문법 수정만 한다.
- 실제 Java 참조가 없는 XML, 제외 표시된 XML, 로그인 XML은 배치하지 않는다.

#### Step 4 — 구조 이관 대조

- 원본 폴더와 portal `legacy` 폴더를 비교해 누락·과잉 복사를 확인한다.
- `genius-file-list.md`의 제외 상태와 실제 파일 상태를 일치시킨다.
- `migration-list.md`에 JSP/Java/XML 폴더 매칭과 대체·제외 사유를 기록한다.
- 이 단계의 완료 기준은 “파일 배치 완료”이며, 화면이 실행되거나 컴파일되는 상태를 완료 조건으로 보지 않는다.

### 2단계 — 기능 실행 및 단계적 전환(후속)

- 구조 이관이 끝난 뒤 폴더별로 JSP → Java → XML 연결, `.do` 제거, JWT/SecurityContext 치환, `@PreAuthorize`, MyBatis 실행을 검증한다.
- 검증된 기능부터 legacy header/footer/GNB를 `common/layout`으로 하나씩 승격하고 `common-top/common-bottom` 또는 `default-top/default-bottom`으로 전환한다.
- 승격 후 회귀 테스트가 끝난 경우에만 legacy shell을 제거한다.

## 4. 폴더 완료 조건

### 1단계 구조 이관 완료 조건

- 파일 목록의 이관/제외 체크가 실제 복사 결과와 일치한다.
- 원본 폴더 계열과 portal `legacy` 대상 계열이 일치한다.
- CSS/JS/이미지는 `resources/legacy`를 참조하고 중복 복사하지 않았다.
- `migration-list.md`에 폴더 매칭과 대체·제외 사유가 기록되어 있다.

### 2단계 실행 완료 조건

- JSP include, 정적 리소스, URL의 `.do` 제거가 확인된다.
- Java Controller/Service/Mapper 연결과 `@PreAuthorize` 권한이 확인된다.
- XML namespace·mapper id·resultMap이 Java 호출과 일치한다.
- portal에서 컴파일 및 해당 화면의 기본 진입/조회/저장 흐름을 확인한다.

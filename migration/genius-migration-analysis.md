# Genius(genious_prd) 이관 대상 1차 분석

분석 대상: `/Users/sqi/ktgenius/git/genious_prd`

이 문서는 `portal` 기반 `new-genius` 생성을 위한 1차 정적 분석 결과다.  
여기서 말하는 "삭제"는 기존 운영 소스에서 삭제한다는 뜻이 아니라, `new-genius`로 직접 이관하지 않는다는 뜻이다.

## 0. 분석 요약

### 정량 현황

- 전체 파일 수: 약 22,578개
- `WebContent/WEB-INF/classes`, `.idea` 등을 제외한 주요 파일:
  - Java: 647개
  - JSP: 563개
  - JS: 354개
  - CSS: 266개
  - XML: 106개
  - properties: 17개
  - jar: 166개
- Spring `@Controller`: 58개
- `@RequestMapping` 계열: 약 741개
- iBatis SQL XML: 57개
- `WEB-INF/jsp` 하위 JSP: 537개
- Tiles definition 관련 항목: 약 328개
- `KT-interceptors.xml`:
  - mapping: 79개
  - exclude-mapping: 315개
  - interceptor class: 20여 개
- Java `javax.*` 참조: 약 631건
- iBatis 참조: 약 139건
- session 참조: 약 530건

### 큰 판단

- `genius`는 사용자 교육포털 업무 코드가 중심이다.
- 핵심 업무 코드는 `src/com/anymobi/usr/**`에 몰려 있다.
- JSP는 `WebContent/WEB-INF/jsp/web/**`, `WebContent/WEB-INF/jsp/mobile/**`가 중심이다.
- `WebContent/WEB-INF/classes/**`는 `src`와 설정 파일 복사본 성격이므로 직접 이관하지 않는다.
- `WebContent/WEB-INF/lib/**`의 jar를 그대로 복사하지 않는다. portal `pom.xml` 기준 dependency로 대체한다.
- Tiles, iBatis, Spring 3 XML 설정은 그대로 이관하지 않고 portal 기준으로 변환한다.
- admin성 `/adm/**`, batch, SCORM, iCampus, DWR, WebSquare, SSO agent JSP는 서비스 유지 여부 확인이 필요하다.

## 1. 꼭 이관해야 하는 것

### 1.1 사용자 사이트 업무 Java

아래 패키지는 `new-genius`의 핵심 업무 기능 후보다. 메뉴 단위 이관 시 해당 controller, service, vo, mapper, JSP를 함께 추적한다.

| 구분 | 경로 | 판단 |
|---|---|---|
| 로그인/인증 | `src/com/anymobi/usr/login/**` | 필수. 단, session 기반 로그인은 portal JWT/Spring Security 기준으로 재작성 |
| 메인 | `src/com/anymobi/usr/main/**` | 필수. portal main, banner, popup, link, cash 관련 기능 확인 |
| 공통 사용자 기능 | `src/com/anymobi/usr/common/**` | 필수. GNB, sitemap, 회원검색, 사용자 상세, 공통 AJAX |
| 교육 목록/수강 | `src/com/anymobi/usr/education/**` | 필수 핵심. 과정 목록, 수강신청, 과제/시험/설문/학습 콘텐츠 |
| 나의 강의실 | `src/com/anymobi/usr/myclass/**` | 필수 핵심. 수강 중/완료, 학습조직, 내 학습 |
| 과정 | `src/com/anymobi/usr/course/**` | 필수. online/offline/course system |
| 고객지원 | `src/com/anymobi/usr/support/**` | 필수. notice, faq, qna, alarm |
| 아카데미 | `src/com/anymobi/usr/academy/**` | 필수 후보. 시설/예약/소개 유지 여부는 메뉴별 확인 |
| 교육 콘텐츠 | `src/com/anymobi/usr/educontents/**` | 필수 후보. courseData, hrdIbox, leaders |
| 학습조직/자격 | `src/com/anymobi/usr/learncom/**` | 필수 후보. CoP, exlicense |
| 평가 | `src/com/anymobi/usr/exam/**` | 필수 후보. 시험 결과/상세 |
| 진단/CDS | `src/com/anymobi/usr/cds/**` | 필수 후보. 현재 메뉴 노출 여부 확인 필요 |
| 전문가/튜터 | `src/com/anymobi/usr/expert/**`, `src/com/anymobi/usr/tutor/**` | 필수 후보 |
| 부서/승인 | `src/com/anymobi/usr/dept/**`, `src/com/anymobi/usr/deptmng/**` | 필수 후보. 일부 admin 성격은 분리 검토 |
| 개인학습/캠페인/캐시/검색/설정 | `selfDev`, `campaign`, `cash`, `search`, `setting` | 메뉴 유지 시 필수 |
| 외부 연동 | `src/com/anymobi/usr/external/**`, `src/com/anymobi/usr/api/**`, `src/com/anymobi/usr/sso/**` | 기능 유지 시 필수. 인증/보안 방식 재정리 필요 |

### 1.2 JSP 화면

아래는 메뉴 단위로 반드시 추적해야 하는 JSP 영역이다.

| 구분 | 경로 | 판단 |
|---|---|---|
| PC 사용자 화면 | `WebContent/WEB-INF/jsp/web/**` | 필수. active 메뉴만 이관 |
| 모바일 화면 | `WebContent/WEB-INF/jsp/mobile/**` | 모바일 서비스를 유지하면 필수 |
| 로그인 화면 | `WebContent/WEB-INF/jsp/web/login/**`, `WebContent/WEB-INF/jsp/mobile/login/**` | 필수. portal 로그인/JWT 흐름에 맞게 조정 |
| 공통 layout | `WebContent/WEB-INF/jsp/web/layout/**` | 필수 후보. portal `common/layout`으로 재구성 |
| 공통 JSP | `WebContent/WEB-INF/jsp/web/common/**`, `mobile/common/**` | 필수 후보. 메뉴에서 참조되는 것만 |
| 오류 화면 | `WebContent/WEB-INF/jsp/web/error/**`, `mobile/error/**` | portal 공통 오류 화면과 병합 |
| Tiles layout JSP | `WebContent/WEB-INF/jsp/web/tiles/**` | 직접 이관하지 않고 include layout 전환 참고자료로 사용 |

JSP 수량 기준으로는 `mobile/mobile` 173개, `web/education` 60개, `web/myclass` 37개, `web/tiles` 28개, `web/learncom` 26개가 크다.  
따라서 첫 파일럿 메뉴는 `education` 또는 `myclass`에서 잡는 것이 좋다.

### 1.3 SQL/Mapper

아래 mapper는 메뉴 단위로 MyBatis 전환 대상이다.

| 구분 | 경로 | 판단 |
|---|---|---|
| 사용자 업무 SQL | `src/conf/sql/usr/**/*.xml` | 필수. 메뉴별로 사용 query만 MyBatis로 전환 |
| 공통 SQL | `src/conf/sql/common/**/*.xml` | 필수 후보. 공통 코드/메뉴/파일/사용자 조회 |
| 메일/SMS SQL | `src/conf/sql/mail/*.xml`, `src/conf/sql/sms/*.xml` | 기능 유지 시 필수 |
| batch SQL | `src/conf/sql/batch/*.xml` | 애매. batch 유지 방식 결정 후 이관 |
| admin SQL | `src/conf/sql/adm/**/*.xml` | `new-genius`보다는 `new-admin` 후보 |

기존 `KT-ibatis.xml`, `sqlMapConfig*.xml`, iBatis jar는 직접 이관하지 않는다. SQL 문장과 namespace/query id만 추출해서 MyBatis mapper로 옮긴다.

### 1.4 공통 Java 중 선별 이관 대상

아래는 기능 자체는 필요하지만 portal에 이미 유사 기능이 있으면 병합/대체한다.

| 구분 | 경로 | 판단 |
|---|---|---|
| 상수 | `src/com/anymobi/common/Constants.java` | 필수 후보. 사용 상수만 portal 기준으로 정리 |
| 세션 유틸 | `src/com/anymobi/common/util/SessionUtil.java` | 직접 이관 금지. JWT/user context 전환 기준 자료 |
| 문자열/공통 유틸 | `Utils`, `SharedMethods`, `RedirectUtil` | 사용 메서드만 선별. portal util과 중복 제거 |
| 파일 처리 | `src/com/ktlms/common/file/**`, `src/com/anymobi/common/util/FileUtil.java` | 필수 후보. portal file util과 병합 |
| 공통 DAO | `src/com/anymobi/common/dao/**` | iBatis 제거 후 MyBatis 공통 mapper/helper로 대체 |
| 메뉴 권한 | `CommonMenuAuthService`, `MenuAuth*` | Spring Security 권한 체계로 전환 |
| 예외 | `BaseExceptionResolver`, `ExceptionService*` | portal 공통 exception으로 병합 |
| Firebase | `FirebaseConfig`, `FirebaseService` | push 기능 유지 시 portal 공통 설정으로 병합 |
| paging | `common/util/paging/**`, `PagingHandler` | JSP/mapper에서 쓰는 것만 선별 |

### 1.5 Properties 중 선별 이관 대상

아래 파일은 그대로 복사하지 않는다. 메뉴별로 실제 사용하는 key만 portal `application-*.properties`로 옮긴다.

| 파일 | 현재 활성 key 수 | 판단 |
|---|---:|---|
| `src/conf/cresys.properties` | 약 299 | 필수 key 선별. upload path, LDAP, SSO, Firebase, 외부 URL 등 |
| `src/conf/cresys_service.properties` | 약 233 | 서비스 URL/외부 연동 유지 시 선별 |
| `src/conf/bulletin.properties` | 약 656 | 게시판 기능 이관 시 선별 |
| `src/conf/credu_ko.properties` | 약 992 | 메시지/라벨. 실제 JSP/Java 참조분만 |
| `src/conf/mail.properties` | 약 88 | 메일 기능 유지 시 선별 |
| `menu_name.properties`, `menu_depth.properties` | 소량 | 메뉴 구조를 portal 방식으로 바꿀 때 참고 |
| `saml.properties` | 별도 | SAML 유지 시 portal SSO 설정과 병합 |
| `scorm2004.properties` | 약 26 | SCORM 유지 시만 |

### 1.6 Front resource 중 우선 이관 대상

| 구분 | 경로 | 판단 |
|---|---|---|
| 최신 포털 리소스 | `WebContent/newPortal/**` | 우선 이관 후보. JSP에서 다수 참조 |
| 공통 JS/CSS | `WebContent/common/**`, `WebContent/script/**`, `WebContent/css/**` | 메뉴 JSP가 참조하는 파일만 |
| 레거시 이미지 | `WebContent/images/**`, `WebContent/image/**`, `WebContent/anymobi/**` | 전체 복사 금지. 참조되는 asset만 |
| 모바일 리소스 | `WebContent/anymobi/mobile/**`, `newPortal/icons/m/**` | 모바일 유지 시 선별 |

## 2. 삭제해도 되는 것

정확히는 `new-genius`에 이관하지 않아도 되는 항목이다.

### 2.1 IDE/레거시 빌드 메타

- `.idea/**`
- `.settings/**`
- `.classpath`
- `.project`
- `.pmd`
- `genious_prd.iml`
- `build.xml`

`new-genius`는 portal 복제본이므로 Maven/pom 기준을 따른다. Ant `build.xml`은 분석 자료로만 본다.

### 2.2 빌드 산출물/중복 복사본

- `WebContent/WEB-INF/classes/**`

이 영역은 `src`와 설정 파일이 복사된 런타임 산출물 성격이다. 직접 이관하면 중복과 혼선이 생긴다.

### 2.3 jar 직접 복사

`WebContent/WEB-INF/lib/**`의 jar는 원칙적으로 직접 복사하지 않는다.  
특히 아래는 삭제/대체 대상이다.

- Spring 3 계열 jar
- Tiles 3 계열 jar
- iBatis 2 계열 jar
- `jboss-servlet-api_3.0_spec`, `jboss-jsp-api_2.2_spec`
- `log4j-1.2.x`
- 구버전 `commons-*` 중 portal dependency와 겹치는 것
- `ojdbc14`, `ojdbc5`, `mysql-connector-java-5.1.x`
- `jstl-1.2`, `standard.jar`
- `local_policy.jar`, `US_export_policy.jar`
- `solr-solrj-4.0.0-sources.jar`
- `google-collect-1.0-rc1.zip`

특수 vendor jar는 삭제 확정이 아니라 애매 항목으로 별도 분리한다.

### 2.4 기존 XML 설정 파일

아래 파일은 직접 이관하지 않고, 필요한 설정값만 portal 구조로 옮긴다.

- `src/conf/spring/KT-application.xml`
- `src/conf/spring/KT-ibatis.xml`
- `src/conf/spring/KT-interceptors.xml`
- `src/conf/spring/KT-properties.xml`
- `src/conf/spring/KT-service.xml`
- `src/conf/spring/KT-servlet.xml`
- `WebContent/WEB-INF/spring/tiles-define.xml`
- `src/conf/spring/sqlMapConfig*.xml`

### 2.5 레거시 필터/세션/인코딩 구현

portal 공통 기능으로 대체할 대상이다.

- `filters/SetCharacterEncodingFilter.java`
- `com.anymobi.common.filter.HttpsFilter`
- `CrossScriptingFilter`
- `HttpRequestWrapper*`
- `JsonFilter`
- session check 계열 interceptor

단, 기존 동작을 이해하기 위해 분석 자료로는 남겨야 한다.

### 2.6 테스트/개발용으로 보이는 항목

운영 사용 여부가 확인되지 않으면 이관하지 않는다.

- `testDev`, `testLogin`, `testDevLogout` 관련 controller/JSP/URL
- 주석상 "사용안함 newdept로 바꿈" 처리된 tiles definition:
  - `dept/deptStatistics/deptStatisticsList`
  - `deptmng/edustatus/eduStoldDeptMemberStatus`
- `.bak`, `.java_bak`, `.swf_bak`, `.swf_back`
- 원본 편집 파일 성격의 `.fla`, `.psd`

### 2.7 정적 파일 전체 복사 금지

아래는 폴더 단위 이관 금지다. 메뉴별 JSP/CSS/JS 참조를 따라 필요한 파일만 옮긴다.

- `WebContent/images/**`
- `WebContent/image/**`
- `WebContent/anymobi/**`
- `WebContent/css/**`
- `WebContent/script/**`
- `WebContent/htmlskin/**`
- `WebContent/bootstrap/**`

## 3. 애매한 것

아래 항목은 삭제하거나 이관하기 전에 서비스 유지 여부를 확인해야 한다.

### 3.1 Admin 성격 코드

| 경로 | 판단 |
|---|---|
| `src/com/anymobi/adm/**` | `new-genius`가 아니라 `new-admin` 후보일 가능성이 높음 |
| `src/conf/sql/adm/**` | admin 기능 유지 시 `new-admin`으로 이관 |
| `/adm/**` URL | genius 사용자 사이트에 남길지, admin으로 분리할지 결정 필요 |

현재 genius 안에 miniLecture, intellect, slow query, dept popup 등 admin URL이 섞여 있다.  
`new-genius`에서는 제거하고 `new-admin`으로 보내는 방향을 우선 검토한다.

### 3.2 Batch

| 경로 | 판단 |
|---|---|
| `src/com/anymobi/batch/**` | web app 내부 유지 여부 확인 |
| `src/conf/sql/batch/*.xml` | batch job 유지 시 MyBatis 전환 |
| `KT-batch.xml`, `KT-eojtBatch.xml`, `KT-kCampusBatch.xml`, `KT-lmsBatch.xml`, `KT-logBatch.xml` | portal 웹앱에 포함할지 별도 batch app/job으로 분리할지 결정 |

JDK21/Spring6 기준에서는 가능하면 웹앱과 batch를 분리하는 것이 좋다.

### 3.3 iCampus/mCampus

| 경로 | 판단 |
|---|---|
| `src/com/anymobi/icampus/**` | 모바일/외부 캠퍼스 연동 유지 여부 확인 |
| `WebContent/WEB-INF/jsp/mobile/micampus/**` | iCampus 화면 유지 시 이관 |
| `/micampus/**`, `/m/micampus/**`, `/mobile/*/micampus/**` URL | 외부 앱 또는 모바일 앱 연동 여부 확인 |

현재 interceptor whitelist에도 iCampus API가 별도로 잡혀 있으므로 운영 연동 가능성이 있다.

### 3.4 SCORM/구 서블릿

| 경로 | 판단 |
|---|---|
| `src/com/credu/scorm2004/**` | SCORM 콘텐츠 재생/추적 유지 여부 확인 |
| `src/controller/scorm2004/**` | old servlet 방식. Controller로 재구성 필요 |
| `WebContent/css/scorm2004/**`, `WebContent/script/scorm2004/**` | SCORM 유지 시 선별 |
| `src/net/sf/jazzlib/**` | SCORM import zip 처리에 묶여 있을 가능성 |

SCORM은 유지한다면 별도 파일럿으로 다뤄야 한다. 일반 메뉴 이관과 섞으면 위험하다.

### 3.5 DWR/WebSquare/Invoker

| 항목 | 판단 |
|---|---|
| DWR servlet `/dwr/*` | 실제 호출 화면이 있는지 확인 |
| `websquare_home/**`, `*.wq` | WebSquare 화면 유지 여부 확인 |
| `InvokerLoadListener`, `invoker.packages` | 과거 servlet dispatcher 방식. 직접 이관 비추천 |
| `controller/library`, `controller/mypage`, `controller/study` | old servlet 방식. URL 사용 여부 확인 |

### 3.6 SSO/NSSO/SAML/LDAP

| 항목 | 판단 |
|---|---|
| `WebContent/nsso_auth.jsp`, `nsso_return.jsp` | NSSO 유지 시 필요 |
| `WebContent/ssoagent/*.jsp` | vendor agent 관리/로그온 JSP. 운영 필요 여부 확인 |
| `WebContent/portal.store` | SSO store 파일. 신규 보안 정책 확인 |
| `SafeSignOn.jar`, `nets-nsso-agent-core.jar`, `ADUtilSSL.jar`, `EPTicket.jar` | vendor jar. portal 인증 구조와 통합 여부 확인 |
| `src/com/anymobi/usr/sso/**` | SAML 유지 시 필수 |
| `ldap.*` properties | 로그인 정책 유지 시 필수 |

portal에 이미 Spring Security/JWT가 있으므로, SSO/LDAP은 "로그인 성공 후 JWT 발급" 흐름으로 재정의해야 한다.

### 3.7 외부 연동/특수 라이브러리

| 항목 | 판단 |
|---|---|
| Firebase | push 유지 시 portal FirebaseConfig로 병합 |
| Solr | 검색 유지 시 최신 client 또는 대체 검색 구조 검토 |
| Naver/YouTube/GData | 실제 메뉴 사용 여부 확인 |
| POI/JXL/excelService | Excel 기능 메뉴별 선별 |
| lucy-xss/ESAPI | portal XSS 정책과 중복 여부 확인 |
| pirateCheck | 모사검사 기능 유지 여부 확인 |
| SAP/RFC 관련 `functions/rfc`, `response/functions/rfc` | 운영 연동 여부 확인 |
| `eacademy/**`, `gate/**` | direct public page인지 legacy page인지 확인 |

## 4. session -> JWT 우선 매핑 후보

session key 빈도 기준으로 먼저 확인해야 할 항목이다.

| session key | 빈도/중요도 | 이관 방향 |
|---|---|---|
| `userid` | 최상 | JWT subject 또는 user context |
| `name` | 높음 | user display name claim/context |
| `gadmin`, `gadmin_open` | 높음 | role/authority로 전환 |
| `authUserId` | 높음 | 위임/관리자 권한 구조 확인 |
| `SAMLRequest`, `SAMLChannel`, `RelayState` | SSO | 로그인 과정 임시 저장. JWT claim으로 넣지 말고 인증 플로우 state로 분리 |
| `CURRENT_MENU_ID`, `CURRENT_MENU_DATA` | 메뉴 | request/model 또는 front 메뉴 상태로 분리 |
| `comp`, `emp_gubn`, `handphone`, `email` | 사용자 부가 정보 | 필요한 것만 user context |
| `s_subj`, `s_subjseq`, `s_year` 등 학습 상태 | 콘텐츠/시험 | 업무별 request/DB 기반으로 전환 검토 |

## 5. 우선 이관 순서 제안

### 1단계: 공통 기반

- 로그인/LDAP/SSO 흐름 분석
- session key -> JWT/user context 매핑
- `CommonController`, `CommonUserController`, GNB/menu AJAX
- 파일 다운로드
- 공통 exception/redirect/json 응답
- `newPortal` 공통 front resource

### 2단계: 파일럿 메뉴

추천 파일럿 후보:

1. `support/notice` 또는 `support/qna`
   - 게시판, 파일, JSP, mapper, 권한, AJAX가 적당히 포함됨
2. `education/list/courseList`
   - 핵심 메뉴지만 영향 범위가 큼
3. `myclass/course/myCourse`
   - session/JWT, 학습 상태, 권한 이슈를 빨리 드러낼 수 있음

첫 파일럿은 `support/notice` 또는 `support/qna`가 더 안전하다.  
`education`/`myclass`는 두 번째 파일럿으로 잡는 편이 좋다.

### 3단계: 메뉴별 반복 이관

각 메뉴마다 다음 순서로 진행한다.

1. URL 확인
2. Controller 확인
3. Service/DAO 확인
4. SQL mapper 확인
5. JSP 확인
6. Tiles definition에서 layout/body 확인
7. JS/CSS/image 참조 확인
8. property key 확인
9. library 필요 여부 확인
10. session/security 변환
11. MyBatis 변환
12. 화면/기능 검증

## 6. 1차 결론

`genius`는 전체를 복사해서 고치는 방식으로 가면 안 된다.  
`portal`을 기준으로 공통 구조를 고정한 뒤, `src/com/anymobi/usr/**`와 `WebContent/WEB-INF/jsp/web|mobile/**`, `src/conf/sql/usr/**`를 메뉴 단위로 선별 이관하는 방식이 맞다.

삭제 가능 항목은 대부분 "레거시 실행 환경을 구성하던 껍데기"다.  
애매 항목은 기능 자체가 오래됐기 때문이 아니라, `new-genius`에 남겨야 하는지 `new-admin`/별도 batch/별도 연동으로 분리해야 하는지 결정이 필요한 항목이다.

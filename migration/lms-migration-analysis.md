# LMS(lms_prd) 이관 대상 1차 분석

작성일: 2026-07-01  
기준 프로젝트: `/Users/sqi/ktgenius/git/lms_prd`  
목표 프로젝트: `new-admin`  
기준: 정적 파일 분석. 운영 메뉴/접속 로그/DB 사용 여부 확인 전 단계의 1차 분류이다.

## 0. 결론

`lms`는 `genius`보다 훨씬 더 강하게 두 갈래 코드가 섞여 있다.

1. 신규 관리자 축: `src/com/ktlms/adm/**`, `src/com/ktlms/common/**`, `WebContent/WEB-INF/jsp/web/adm/**`, `src/conf/sql/adm/**`
2. 구 관리자/구 포털 축: `src/controller/**`, `src/com/credu/**`, `WebContent/learn/admin/**`, `WebContent/learn/offadmin/**`, `WebContent/research/**`

`new-admin`의 1차 이관 기준은 신규 관리자 축을 우선으로 삼는다. 구 관리자/구 포털 축은 파일 수가 크지만, JDK 21/Spring 6 기준으로 직접 이관하면 수정량이 폭발한다. 따라서 운영 메뉴, URL 로그, 관리자 메뉴 DB, 현업 사용 여부가 확인된 항목만 별도 전환 대상으로 남긴다.

## 1. 규모 요약

전체 파일 수는 약 28,957개이다. `WebContent/WEB-INF/classes`, `.idea`를 제외해도 레거시 산출물과 정적 리소스가 매우 많다.

주요 파일 수:

| 구분 | 수량 | 비고 |
|---|---:|---|
| Java | 2,989 | Spring MVC 코드와 구 Servlet 코드 혼재 |
| JSP | 2,205 | `WEB-INF/jsp/web/adm`와 `learn/admin`이 핵심 분기 |
| XML | 271 | Spring XML, iBatis SQL, Tiles 정의 포함 |
| JS | 1,084 | 관리자 화면/구 화면 스크립트 혼재 |
| CSS | 505 | `ktlms`, `newPortal`, 구 `css` 혼재 |
| JAR | 141 | 직접 복사 금지. 의존성 재정의 필요 |
| GIF/PNG/JPG/SVG | 17,826+ | 정적 리소스 선별 필요 |

주요 기술 부채 지표:

| 항목 | 수량 | 판단 |
|---|---:|---|
| `@Controller`/`@RestController` | 236 | 신규 관리자 이관의 1차 기준 |
| `@RequestMapping` 계열 | 약 1,609 | 메뉴별 URL 매핑 정리 필요 |
| `HttpServlet` 상속 | 555 | 직접 이관 비추천. 운영 확인 필요 |
| `web.xml` `invoker.servlet` 등록 | 547 | 구 서블릿 호출 체계 |
| `javax.*` 참조 | 4,544 | Spring 6/Jakarta 전환 필요 |
| `jakarta.*` 참조 | 0 | 현재 전환 전 상태 |
| iBatis 참조 | 123 | MyBatis 전환 필요 |
| 세션 직접/유틸 접근 | 5,687 | Security Principal/세션 정책 재정리 필요 |
| Tiles `adm` definition | 66 | JSP include 레이아웃 전환 필요 |
| property 유효 라인 | 약 603 | 실제 사용 key만 이관 |

## 2. 꼭 이관해야 하는 것

### 2.1 관리자 Spring MVC 코드

다음 경로는 `new-admin`의 핵심 업무 코드로 보고 우선 이관 대상으로 둔다.

- `src/com/ktlms/adm/**`
- `src/com/ktlms/common/**` 중 관리자 MVC에서 실제 호출되는 공통 코드
- `src/conf/sql/adm/**`
- `WebContent/WEB-INF/jsp/web/adm/**`
- `WebContent/WEB-INF/jsp/web/login/loginAdmin.jsp`
- `WebContent/WEB-INF/jsp/web/common/**`
- `WebContent/WEB-INF/jsp/web/error/**`
- `WebContent/WEB-INF/spring/tiles-define.xml`은 직접 이관이 아니라 레이아웃 변환 참고 자료로 사용

Spring MVC 컨트롤러 분포상 우선순위는 다음과 같다.

| 모듈 | Controller 수 | 판단 |
|---|---:|---|
| `educontents` | 122 | 가장 큰 핵심. 후순위 큰 덩어리로 단계 이관 |
| `capabilityv3` | 17 | 역량진단 관리자. 메뉴 사용 여부 확인 후 이관 |
| `cash` | 12 | 정산/비용 계열. 업무 중요도 확인 필요 |
| `course` | 9 | 온라인 과정 관리자 |
| `auth` | 8 | 권한/메뉴/역할. 초반 필수 |
| `selfDev` | 8 | 자기개발계획 계열 |
| `system` | 7 | 시스템/예외/엑셀/추천학습시간 |
| `common` | 6 | 공통 팝업/관리 기능 |
| `board` | 6 | 게시판 계열. 파일/권한 검증용 pilot 후보 |
| `license` | 6 | 자격/라이선스 |
| `tutor`, `form`, `learncom` 등 | 1~5 | 메뉴별 순차 이관 |

초기 공통 기반에서 먼저 가져와야 하는 관리자 기능:

- `src/com/ktlms/adm/login/**`
- `src/com/ktlms/adm/auth/menu/**`
- `src/com/ktlms/adm/auth/rolemenu/**`
- `src/com/ktlms/adm/auth/userrole/**`
- `src/com/ktlms/adm/auth/personal/**`
- `src/com/ktlms/adm/menu/front/**`
- `src/com/ktlms/adm/common/**`
- `src/com/ktlms/common/file/**`
- `src/com/ktlms/common/exception/**`
- `src/com/ktlms/common/code/**`
- `src/com/ktlms/common/spring/formatter/**`
- `src/com/ktlms/common/util/paging/**`

단, `src/com/ktlms/common/filter/**`, `src/com/ktlms/common/interceptor/**`는 그대로 이관하지 않고 portal의 Spring Security/WebConfig 정책으로 흡수한다.

### 2.2 JSP 화면

핵심 JSP는 다음 경로이다.

- `WebContent/WEB-INF/jsp/web/adm/**`
- `WebContent/WEB-INF/jsp/web/login/loginAdmin.jsp`
- `WebContent/WEB-INF/jsp/web/common/INC_ADM_HEADER.jsp`
- `WebContent/WEB-INF/jsp/web/common/INC_Header.jsp`
- `WebContent/WEB-INF/jsp/web/common/INC_Footer.jsp`
- `WebContent/WEB-INF/jsp/tiles/blend/*.jsp`

`WEB-INF/jsp/web/adm`의 주요 분포:

| JSP 경로 | 수량 | 판단 |
|---|---:|---|
| `web/adm/educontents` | 275 | 핵심이지만 복잡도 높음. 뒤쪽 큰 단계 |
| `web/adm/course` | 39 | 과정 관리 |
| `web/adm/learncom` | 34 | 학습 커뮤니티/COP/멘토링 |
| `web/adm/common` | 29 | 공통 팝업/관리 화면 |
| `web/adm/tutorManage` | 17 | 튜터 관리 |
| `web/adm/selfDev` | 16 | 자기개발 |
| `web/adm/facilityManage` | 16 | 시설 관리 |
| `web/adm/capabilityv3` | 16 | 역량진단 |
| `web/adm/cash` | 13 | 정산 |
| `web/adm/form`, `event`, `display` | 각 11 | 메뉴별 이관 |
| `web/adm/auth` | 8 | 권한/메뉴. 초반 필수 |
| `web/adm/board` | 8 | pilot 후보 |

JSP 이관 기준:

- Controller가 반환하는 view name과 매칭되는 JSP만 이관한다.
- Tiles definition만 있고 Controller 매핑이 없거나, 반대로 JSP만 있는 파일은 메뉴별 확인 전까지 보류한다.
- JSP 내부 include/script/css 참조는 해당 JSP 이관 시점에만 같이 가져온다.
- Excel 다운로드 JSP는 화면이 아니라 응답 템플릿인 경우가 많으므로 Controller 사용 여부를 확인하고 이관한다.

### 2.3 SQL Mapper

현재는 iBatis 2 기반이다. Spring 6에서는 직접 유지하지 말고 MyBatis mapper로 전환한다.

우선 이관 대상:

- `src/conf/sql/adm/**`
- `src/conf/sql/common/CommonSql.xml`
- `src/conf/sql/common/CommonService.xml`
- `src/conf/sql/sms/SmsSql.xml`은 관리자 알림/문자 기능이 사용될 때만 이관
- `src/conf/sql/usr/login/**`은 관리자 로그인 또는 공통 로그인에서 실제 참조되면 이관

`src/conf/sql/adm`의 주요 분포:

| 경로 | 수량 | 판단 |
|---|---:|---|
| `adm/educontents` | 62 | educontents 이관 시 필수 |
| `adm/capabilityv3` | 9 | capabilityv3 이관 시 필수 |
| `adm/form` | 7 | form 이관 시 필수 |
| `adm/cash` | 6 | cash 이관 시 필수 |
| `adm/course` | 5 | course 이관 시 필수 |
| `adm/common` | 5 | 공통 기능 필수 후보 |
| `adm/auth` | 4 | 권한/메뉴 초반 필수 |
| `adm/system`, `adm/selfdev`, `adm/tutor`, `adm/board` | 3~4 | 메뉴별 이관 |

SQL 이관 기준:

- `sqlMapClient3`의 명시 매핑은 신규 관리자 코드에서 주로 쓰는 매핑으로 보고 우선 검토한다.
- DAO가 실제 호출하는 statement id만 MyBatis mapper로 옮긴다.
- 사용하지 않는 resultMap, parameterMap, 중복 query는 옮기지 않는다.
- `CommonDAO`, `CommonDAO2`처럼 문자열 id로 호출하는 구조는 mapper namespace를 먼저 확정한 뒤 변환한다.

### 2.4 관리자 공통 설정

다음 설정은 역할을 `portal` 공통 또는 `new-admin` 설정으로 재구성해야 한다.

- `src/conf/spring/KT-servlet.xml`
  - component scan
  - formatter/conversion service
  - view resolver
  - JSON view
  - download view
- `src/conf/spring/KT-application.xml`
  - multipart
  - messageSource
  - exception resolver
  - property placeholder
  - jasypt
- `src/conf/spring/KT-service.xml`
  - transaction AOP
- `src/conf/spring/KT-ibatis.xml`
  - datasource
  - transaction manager
  - SQL mapper 연결
- `src/conf/spring/KT-interceptors.xml`
  - 로그인 체크
  - 메뉴 권한
  - 공통 모델 세팅
  - 실행 로그/접속 정보

직접 XML을 이관하지 말고 Java Config, Spring Security, MyBatis 설정으로 재작성한다.

### 2.5 인증/권한/메뉴

기존 인터셉터는 5개이다.

- `SessionCheckInterceptor`
- `ControllerExecuteInterceptor`
- `AdminTaskMenuInterceptor`
- `ControllerContectInfoInterceptor`
- `AdminMenuAuthInterceptor`

전환 기준:

- 로그인 여부: Spring Security 인증 필터/JWT/세션 정책으로 통합
- URL 접근권한: `@PreAuthorize`만으로 전부 해결하지 말고 URL matcher + method security를 함께 사용
- 메뉴 권한: 로그인한 관리자 Principal에 role/menu permission을 싣고, 화면 공통 모델에서 메뉴 목록을 제공
- 실행 로그/접속 정보: Spring MVC interceptor 또는 AOP로 분리
- 공통 모델 세팅: `@ControllerAdvice` 또는 `HandlerInterceptor`로 분리

즉, `KT-interceptors.xml`은 `@PreAuthorize`로 일괄 치환하지 않는다. 보안 판단은 Spring Security, 화면 편의 데이터는 MVC 공통 처리, 로그는 AOP/Interceptor로 역할을 나눈다.

### 2.6 정적 리소스

우선 후보:

- `WebContent/ktlms/**`
- `WebContent/dist/**`
- `WebContent/bootstrap/**`
- `WebContent/newPortal/**`
- `WebContent/common/**`

이관 기준:

- JSP에서 참조되는 CSS/JS/image/font만 이관한다.
- `images/**`, `css/**`, `script/**`는 구 화면 리소스가 대량 포함되어 있으므로 통째로 이관하지 않는다.
- `upload/**`, `excelupload/**`, `dp/**`는 운영 업로드/샘플/템플릿이 섞였을 가능성이 있어 runtime storage와 배포 리소스를 분리해서 판단한다.

## 3. 삭제해도 되는 것

여기서 "삭제"는 `new-admin`에 직접 이관하지 않는다는 의미이다. 운영 서버에서 즉시 삭제하자는 뜻은 아니다.

### 3.1 IDE/빌드/산출물

다음은 이관하지 않는다.

- `.idea/**`
- `.settings/**`
- `.classpath`
- `.project`
- `KTedu_admin.iml`
- `build.xml`
- `build_local.xml`
- `WebContent/WEB-INF/classes/**`
- 컴파일 산출물, IDE 메타데이터, 로컬 빌드 설정

### 3.2 직접 JAR 복사

`WebContent/WEB-INF/lib`의 JAR 141개는 그대로 복사하지 않는다. Maven/Gradle 의존성으로 재정의한다.

직접 이관 금지 또는 대체 대상:

- Spring 3.x 계열
- Tiles 3.x 계열
- iBatis 2.x 계열
- `log4j-1.2.15.jar`
- `ojdbc14.jar`, `ojdbc5.jar`
- `mysql-connector-java-5.1.36.jar`
- `commons-dbcp-1.3.jar`
- `commons-lang-2.6.jar`
- `json-lib`, `ezmorph`, 구 Jackson 1.x/2.9.x 혼재
- `jxl.jar`, 구 POI 3.x 계열
- `cos.jar`
- `dwr.jar`
- `joda-time-0.95.jar`

업무 연동 여부 확인 후 판단할 JAR:

- `SafeSignOn.jar`
- `NetsSSOJSPAgent.jar`
- `ADUtilSSL.jar`
- `EPTicket.jar`
- `kt_crypto.jar`
- `kisa.jar`
- `lucy-xss*`
- `com.sap.*`, `sap.logging.jar`
- `gdata-*`
- `pirateResultParser.jar`
- `scsl.jar`
- `Softcamp` 관련 코드와 연결되는 라이브러리

### 3.3 구 Spring XML 자체

다음 XML은 파일 자체를 직접 이관하지 않는다. 역할만 추출한다.

- `src/conf/spring/KT-application.xml`
- `src/conf/spring/KT-servlet.xml`
- `src/conf/spring/KT-ibatis.xml`
- `src/conf/spring/KT-service.xml`
- `src/conf/spring/KT-interceptors.xml`
- `src/conf/spring/KT-properties.xml`
- `src/conf/spring/sqlMapConfig*.xml`
- `WebContent/WEB-INF/spring/tiles-define.xml`

`*.bak` 파일은 이관하지 않는다.

### 3.4 구 필터/보안 설정 직접 이관

다음은 Spring Security/WebConfig/서버 설정으로 흡수하고 직접 이관하지 않는다.

- `HttpRequestWrapperFilter`
- `CharacterEncodingFilter`의 EUC-KR 강제 정책
- `SetCharacterEncodingFilter`
- `HttpsFilter`
- `MultipartFilter`
- `CrossScriptingFilter`
- `Ajax401Filter`
- `XPoweredByHeaderFilter`
- `web.xml`의 Servlet 2.5 기반 설정

단, 기존 필터가 막고 있던 공격/호환 케이스는 portal 공통 정책에 반영해야 한다.

### 3.5 백업/임시/구 산출 파일

다음 패턴은 이관하지 않는다.

- `*.bak`
- `*_bak`
- `*.java_bak`
- `*.xml_bak`
- `*.jsp_bak`
- `*.backup*`
- `*.090901`
- IDE/빌드/로컬 테스트 산출물

현재 정적 검색 기준으로 최소 12개 이상이 확인된다. 확장자 없는 임시 파일과 날짜 접미 파일은 메뉴 이관 시 추가로 확인한다.

### 3.6 Tiles 직접 이관

Tiles 라이브러리와 definition XML은 직접 이관하지 않는다.

대신 다음 레이아웃 단위로 JSP include 템플릿을 만든다.

- 기본 관리자 레이아웃: `pageLayoutTiles`
- 전체 화면 레이아웃: `fullPageLayoutTiles`
- 집합 운영 레이아웃: `blendOperLayoutTiles`
- 자율과정 운영 레이아웃: `mpackOperLayoutTiles`
- 평가 운영 레이아웃: `examOperLayoutTiles`
- 역량진단 운영 레이아웃: `capability3PageLayoutTiles`

## 4. 애매한 것

### 4.1 `src/controller/**`

`src/controller/**`에는 `HttpServlet` 기반 구 관리자/구 포털 코드가 매우 많다. `web.xml`의 `invoker.servlet`에도 547개 항목이 등록되어 있다.

주요 분포:

| 경로 | Servlet 수 | 판단 |
|---|---:|---|
| `controller/homepage` | 57 | 구 포털 성격. admin 이관 대상인지 확인 필요 |
| `controller/study` | 46 | 학습/수강 성격. portal 또는 보류 가능성 |
| `controller/system` | 41 | 구 시스템 관리자일 수 있음. 메뉴 확인 필요 |
| `controller/research` | 35 | 설문/리서치. 사용 여부 확인 |
| `controller/eportfolio` | 35 | 사용 여부 확인 |
| `controller/cp` | 34 | CP/콘텐츠 제공사 관리자 가능성 |
| `controller/course` | 33 | 구 과정 관리자 가능성 |
| `controller/beta` | 24 | 구/폐기 가능성 높음 |
| `controller/community` | 23 | 커뮤니티 관리자 가능성 |
| `controller/eduoff`, `mentor`, `library`, `propose` | 15~19 | 사용 여부 확인 |

판정 기준:

- 현재 관리자 메뉴 DB에 연결된 URL이면 이관 후보
- 최근 3~6개월 운영 접속 로그가 있으면 이관 후보
- `learn/admin` JSP와만 연결되고 신규 `adm` 메뉴와 무관하면 보류
- 단순 조회/다운로드 기능이면 Spring MVC Controller로 재작성
- 복잡한 SCORM/학습 진행/수료 로직이면 별도 설계 대상으로 분리

### 4.2 `src/com/credu/**`

`com.credu`는 오래된 업무 도메인 코드이다. 세션 키 `userid`, `gadmin`, `name`, `comp`, `s_subj`, `s_year`, `s_subjseq` 등에 강하게 의존한다.

판정 기준:

- `com.ktlms.adm`에서 직접 호출하면 해당 클래스만 최소 이관
- `controller/**`에서만 호출하면 구 서블릿 메뉴 판정 결과에 따른다
- SCORM, LCMS, community, gatepage, budget 등은 운영 메뉴 확인 전까지 보류

### 4.3 `WebContent/learn/**`, `WebContent/research/**`

JSP 분포상 `learn/admin`이 1,221개, `learn/offadmin`이 202개로 매우 크다. 하지만 신규 관리자 JSP는 `WEB-INF/jsp/web/adm/**`에 따로 존재한다.

판정:

- `new-admin` 1차 이관 대상은 아니다.
- 현재 관리자에서 직접 접근하는 구 메뉴가 있으면 메뉴별로 별도 이관한다.
- 사용하지 않으면 삭제 대상이다.
- `research/**`는 설문/리서치 관리자 기능이 현재 운영 중인지 확인이 필요하다.

### 4.4 Batch

다음은 애매한 영역이다.

- `src/com/ktlms/batch/**`
- `src/conf/sql/batch/**`
- `src/conf/spring/KT-batch.xml`
- `src/conf/spring/KT-lmsBatch.xml`
- `src/conf/spring/KT-logBatch.xml`
- `src/conf/spring/KT-kCampusBatch.xml`
- `src/conf/spring/KT-eojtBatch.xml`
- `/api/scheduler/*.do`

판정 기준:

- 관리자 화면에서 즉시 실행하는 batch이면 `new-admin` 이관
- 정기 스케줄이면 별도 scheduler/batch 모듈로 분리
- 구 DB 직접 연결/하드코딩 datasource는 반드시 환경변수/JNDI/properties로 교체

### 4.5 SSO/LDAP/DRM/Softcamp/SAP/외부 연동

다음은 코드/JAR만 보고 삭제하면 위험하다.

- SSO: `SafeSignOn`, `NetsSSO`, `EPTicket`
- LDAP/AD: `ADUtilSSL`
- 암호화: `kt_crypto`, `kisa`
- DRM/문서보안: `Softcamp`, `DRMFileUtil`
- SAP: `com.sap.*`, `sap.logging`
- Google/YouTube API: `gdata-*`
- 불법복제/콘텐츠 보안: `pirateResultParser`, `piratecheck`

판정 기준:

- 로그인/권한/파일다운로드/콘텐츠 업로드 플로우에서 호출되면 이관
- 특정 메뉴에서만 쓰면 해당 메뉴 이관 시점에 추가
- 사용처가 없으면 이관하지 않음

### 4.6 업로드/엑셀/임시 디렉터리

다음 경로는 배포 리소스, 샘플 템플릿, 운영 업로드 파일이 섞여 있을 수 있다.

- `WebContent/upload/**`
- `WebContent/excelupload/**`
- `WebContent/dp/**`
- `WebContent/display/**`

판정 기준:

- 다운로드 템플릿 파일이면 `resources/templates` 또는 정적 리소스로 이관
- 운영 업로드 데이터면 애플리케이션 소스에 포함하지 않고 스토리지 정책으로 분리
- 임시 파일이면 삭제

## 5. 이관 순서 제안

### 5.1 new-admin 공통 기반

먼저 다음을 portal 기반에 확정한다.

- admin 로그인 URL/인증 플로우
- 관리자 Principal 구조
- role/menu permission 모델
- URL matcher + method security 기준
- 공통 응답/JSON 에러 규격
- 파일 업로드/다운로드 정책
- JSP view resolver와 `/WEB-INF/jsp` 경로 규칙
- MyBatis datasource/transaction 기준
- EUC-KR 페이지 처리 여부와 UTF-8 전환 원칙

### 5.2 Pilot 메뉴

처음부터 `educontents`를 옮기면 범위가 너무 크다. 다음 중 하나를 pilot으로 추천한다.

1. `adm/auth/menu`
   - 권한/메뉴 구조를 초반에 확정할 수 있다.
   - 단, 보안 공통 설계와 같이 움직여야 해서 난이도는 중간이다.
2. `adm/board`
   - 일반적인 목록/상세/등록/파일 패턴 검증에 좋다.
   - 권한, 파일, JSP include, MyBatis 전환 가이드를 만들기 좋다.
3. `adm/support/manual`
   - 비교적 작고 관리자 화면 패턴 확인에 좋다.

추천 순서는 `adm/board` 또는 `adm/support/manual`로 화면/SQL/파일 패턴을 먼저 검증하고, 그 다음 `adm/auth/menu`로 권한 구조를 확정하는 방식이다.

### 5.3 큰 메뉴군

Pilot 이후 다음 순서로 넓힌다.

1. `adm/auth/**`, `adm/menu/front/**`
2. `adm/common/**`, 공통 팝업/파일/엑셀
3. `adm/board/**`, `adm/support/**`
4. `adm/course/**`
5. `adm/educontents/learn`, `micro`, `article`, `shorts`
6. `adm/educontents/blend`, `mpack`, `exam`
7. `adm/capabilityv3/**`
8. `adm/cash/**`, `adm/license/**`, `adm/stat*`
9. 구 서블릿/`learn/admin`/`research` 중 운영 확인된 항목

## 6. 메뉴별 이관 체크 기준

각 메뉴 이관 시 담당자는 아래 순서로 확인한다.

1. 관리자 메뉴 DB 또는 현재 운영 URL에서 메뉴 사용 여부 확인
2. Controller `@RequestMapping` 목록 작성
3. Controller가 반환하는 JSP 목록 작성
4. JSP 내부 include/css/js/image 참조 목록 작성
5. Service/DAO 호출 목록 작성
6. iBatis statement id 목록 작성
7. MyBatis mapper로 필요한 SQL만 전환
8. 세션 사용 키를 Principal 또는 명시 파라미터로 전환
9. 권한은 URL matcher 또는 `@PreAuthorize`로 지정
10. Tiles view name이면 JSP include 레이아웃으로 변경
11. 파일 업로드/다운로드가 있으면 portal 공통 파일 정책 적용
12. Excel/PDF/DRM/외부 연동이 있으면 메뉴별 추가 dependency 검토
13. 사용하지 않는 JSP, JS, CSS, SQL, VO, DTO는 이관하지 않음

## 7. 최종 분류 요약

### 꼭 이관

- `src/com/ktlms/adm/**`
- `src/com/ktlms/common/**` 중 신규 관리자에서 호출되는 공통 코드
- `WebContent/WEB-INF/jsp/web/adm/**`
- `WebContent/WEB-INF/jsp/web/login/loginAdmin.jsp`
- `WebContent/WEB-INF/jsp/web/common/**`
- `src/conf/sql/adm/**`
- `src/conf/sql/common/**` 중 실제 호출 SQL
- 관리자 로그인/권한/메뉴/파일/공통코드/페이징/포맷터/예외 처리
- JSP에서 실제 참조되는 `ktlms`, `dist`, `bootstrap`, `newPortal` 리소스

### 삭제해도 됨

- IDE/빌드 메타 파일
- `WebContent/WEB-INF/classes/**`
- 직접 JAR 복사
- Spring 3/Tiles/iBatis XML 직접 이관
- Tiles 라이브러리와 definition 직접 이관
- 백업/임시 파일
- 사용처 없는 정적 리소스
- 구 필터/인터셉터의 직접 이관

### 애매함

- `src/controller/**`
- `src/com/credu/**`
- `WebContent/learn/admin/**`
- `WebContent/learn/offadmin/**`
- `WebContent/research/**`
- batch/scheduler
- SSO/LDAP/DRM/Softcamp/SAP/Google API/콘텐츠 보안 연동
- 업로드/엑셀/운영 데이터 디렉터리

## 8. 다음 작업

1. `new-admin` 공통 구조에 admin security/menu/file/MyBatis 기준을 먼저 확정한다.
2. `adm/board` 또는 `adm/support/manual` 중 하나를 pilot 메뉴로 실제 이관한다.
3. pilot 결과로 `tiles-to-jsp-include-guide.md`, `ibatis-to-mybatis-guide.md`, `admin-security-guide.md`를 작성한다.
4. 이후 메뉴별 담당자는 본 문서의 분류와 체크 기준에 맞춰 필요한 파일만 이관한다.

# KT-interceptors → Spring Security / @PreAuthorize 전환 가이드

분석 대상: `genious_prd`(genius), `lms_prd`(lms, module `KTedu_admin`)
기준 프로젝트: `ktlms-portal`(portal) — Spring Security + JWT + `@EnableMethodSecurity(prePostEnabled=true)` 이미 활성

이 문서는 genius/lms의 `KT-interceptors.xml`에 걸린 인터셉터들을 새 프로젝트에서 어떤 메커니즘으로 대체할지, 그리고 **언제(코드 이관 전/후) 작업할지**를 정리한다. [session-to-jwt-guide.md](session-to-jwt-guide.md)와 짝이 되는 문서다.

---

## 0. 순서 판단 — "코드 먼저, 권한 나중" 이 맞다 (단, 조건부)

결론부터: **맞다.** 단 인터셉터를 성격별로 3분류해서 다르게 다룬다.

| 처리 시점 | 대상 | 이유 |
|---|---|---|
| **지금 1회만 미리 정의** (메뉴 이관 전) | 인증 필터체인(완료), 공통 모델 노출(완료), 로깅/접속통계 AOP, 메뉴권한 *메커니즘* | 모든 메뉴가 공유하는 cross-cutting. 메뉴마다 만들면 중복 |
| **코드 옮기고 나중에** (메뉴별) | 각 컨트롤러의 `@PreAuthorize`, 메뉴별 권한 배선, 메뉴 전용 로깅 | 메뉴마다 URL·role·권한 규칙이 다름 |
| **아예 이관 안 함 (삭제)** | 순수 로그인/세션 체크 인터셉터, 빈 stub, XSS 인터셉터 | Spring Security가 대체하거나 죽은 코드 |

### 왜 "코드 먼저"가 안전한가

portal baseline이 `permitAll`이 아니라 **`.anyRequest().authenticated()`** 다 ([SecurityConfig.java](../src/main/java/com/kt/ktedu/core/security/config/SecurityConfig.java)). 그래서 `@PreAuthorize` 없이 컨트롤러만 옮겨도:

- 익명 사용자에게 노출되지 않는다 (로그인 필수는 이미 강제됨).
- 다만 **"로그인한 사용자 전체 허용"** 상태가 된다 — 원래 특정 role/메뉴 권한이 필요했던 화면도 임시로는 아무 로그인 사용자나 접근 가능.

마이그레이션 중(비운영)에는 이 정도 위험은 감수 가능하고, 5절의 TODO 규약으로 "권한 안 붙은 것"을 추적하면 운영 반영 전에 빠짐없이 막을 수 있다.

### ⚠️ 중요한 전제 — 전환 기간엔 "기본 허용 + 옵트인"

메뉴 권한을 **중앙 집중식 deny-by-default**(모든 URL을 메뉴테이블로 검사해서 없으면 차단)로 미리 깔면, 코드만 옮기고 아직 권한 배선을 안 한 메뉴가 **전부 403**이 되어 "코드 먼저" 전략이 깨진다.

그래서 전환 기간에는:
- **`@PreAuthorize`(메서드 단위, 옵트인)** 방식을 기본으로 쓴다 → 안 붙인 메서드는 `.authenticated()` 로만 걸려 접근 가능.
- 중앙 AuthorizationManager로 메뉴테이블 기반 deny-by-default를 하고 싶다면, **모든 메뉴 이관이 끝난 뒤** 마지막에 켠다.

---

## 1. 인터셉터 5분류 → 대체 메커니즘

genius/lms 인터셉터를 기능별로 보면 아래 5가지이고, 대체 방법이 정해져 있다.

| 분류 | 설명 | 대체 | 시점 |
|---|---|---|---|
| **(a) 로그인/세션 체크** | 세션 없으면 redirect/401 | **삭제.** Spring Security(`STATELESS`+`authenticated()`+`JwtAuthenticationFilter`)가 대체 | 이관 안 함 |
| **(b) URL/메뉴 권한** | role·메뉴테이블로 접근 허용/차단 | `@PreAuthorize` (+ 필요 시 중앙 `AuthorizationManager`) | 메뉴별(나중) |
| **(c) 공통 모델/컨텍스트** | 뷰에 사용자·메뉴·디바이스 정보 세팅 | `@ControllerAdvice`(`@ModelAttribute`) / `HandlerInterceptor` | 1회 정의 |
| **(d) 로깅/접속통계** | 실행시간·파라미터·접속로그 DB 적재 | **AOP** `@Aspect`(`@Around`) | 1회 정의(공통) / 메뉴별(전용 로그) |
| **(e) 기타(XSS 등)** | 입력 검증 등 | 죽은 코드는 이관 안 함. XSS는 별도 정책(CSP·필터) | 이관 안 함 |

---

## 2. portal에 "1회만" 정의할 공통 (메뉴 이관 전)

메뉴마다 만들지 말고 아래를 base에 한 번 둔다. (있으면 재사용)

| 공통 | 상태 | 대체하는 레거시 인터셉터 |
|---|---|---|
| Security 필터체인 (인증) | ✅ 완료 (`SecurityConfig`) | 모든 (a) 로그인/세션 체크 |
| `${loginUser}` 뷰 노출 | ✅ 완료 ([CurrentUserModelAdvice](../src/main/java/com/kt/ktedu/core/web/CurrentUserModelAdvice.java)) | SessionCheck의 (c) 부분, CustomMapArgumentResolver의 세션주입 일부 |
| 메뉴권한 메커니즘(@PreAuthorize용 bean/PermissionEvaluator) | ⬜ 미정의 | CommonMenuAuth*, AdminMenuAuth |
| 디바이스 판별 (모바일 유지 시) | ⬜ 미정의 (2.3) | CustomLiteDeviceResolver |

> 실행/파라미터 로깅 AOP, 접속통계는 **공통 인프라로 미리 만들지 않는다.** 아래 2.1/2.2 참고 — 로깅 AOP는 이관하지 않는 쪽을 권하고, 접속통계는 인프라가 아니라 업무 메뉴로 취급한다.

### 2.1 실행/파라미터 로깅 (ControllerExecuteInterceptor) → 이관하지 않음 (권장)

레거시가 찍던 것: 컨트롤러 클래스/메서드, **요청 파라미터 전량**, 실행시간, 매 요청 메모리 통계. 이건 옛 디버깅 보조물이고 이관을 권하지 않는다.

- 요청 파라미터 전량 로깅은 **보안/개인정보 리스크** (비밀번호·주민번호·연락처가 로그에 남음).
- 실행시간·메모리는 JBoss 액세스 로그 + `logging.level` + (필요 시) actuator 로 대체된다.

→ 전역 AOP로 옮기지 말고, 특정 버그를 볼 때 해당 지점에만 국소적으로 로그를 넣는다.

### 2.2 접속통계 (ControllerContectInfoInterceptor) → 인프라 아님, "업무 메뉴"로 취급

통계 테이블 적재는 공통 인프라가 아니라 **업무 기능**이다. 그리고 lms 기준으로 조사한 결과, 적재(`insertContectLog`)는 하지만 그 로그를 읽는 조회/엑셀 쿼리(`getConnectStatictList` 등, `ConnectStatic.xml`)를 **호출하는 컨트롤러가 없다 → 현재 write-only(소비 리포트 없음).**

→ 공통으로 미리 만들지 않는다. "접속통계/접속로그 리스트" 관리자 메뉴를 이관할 때, 그 리포트가 실제 살아있으면 insert 로직을 함께 옮기고(userId는 `SecurityUtil.getCurrentUserId()`로), 소비처가 없으면 적재 자체를 버린다.

### 2.3 디바이스 판별 (CustomLiteDeviceResolver 대체) — 모바일 유지 시에만

**JWT로 "대체"되는 게 아니다.** `CustomLiteDeviceResolver`는 Spring Mobile의 `LiteDeviceResolver`를 상속한 것인데:

- 본체는 **User-Agent 기반 판별**(phone/desktop) — JWT와 무관한 요청 헤더 파싱이다. (태블릿은 별도 사이트를 두지 않으므로 WEB 취급)
- 세션에서 온 부분은 `appCheck`/`isDevice` 플래그(네이티브 앱 웹뷰면 강제 MOBILE)뿐이다.
- 게다가 **Spring Mobile은 개발 중단되어 Spring 6 / Jakarta 를 지원하지 않는다.** `org.springframework.mobile.device.*` 는 portal 에서 못 쓰므로 어차피 새로 구현해야 한다.

정리하면 두 신호를 이렇게 나눈다:

| 신호 | 대체 |
|---|---|
| phone/desktop (태블릿은 WEB 취급) | 요청마다 `User-Agent` 파싱하는 유틸 (JWT 아님) |
| 앱 여부(`appCheck`/`isDevice`) | 네이티브 앱이 매 요청에 **헤더**(`X-Client-Type: app`)를 보내게 하는 게 최선. 앱 수정이 불가하면 로그인 시 결정되는 값이므로 JWT claim(`isApp`)로 실어도 됨 |

권장 구현(모바일 사이트를 유지하는 경우에만):

**이미 portal 에 구현되어 있다** (모바일 유지 확정). 재구현하지 말고 아래를 쓴다:

- [DeviceType](../src/main/java/com/kt/ktedu/common/web/DeviceType.java) — `WEB` / `MOBILE` (태블릿은 WEB)
- [DeviceResolver](../src/main/java/com/kt/ktedu/common/web/DeviceResolver.java) — `@Component`. `resolve()`(디바이스), `isApp()`(앱 여부)
- [DeviceModelAdvice](../src/main/java/com/kt/ktedu/core/web/DeviceModelAdvice.java) — 모든 JSP 에 `${deviceType}`, `${isApp}` 노출

디바이스 vs 앱 여부는 별개 개념이다:

- **`deviceType`** (WEB/MOBILE): 화면 레이아웃 선택용. 폰 UA면 MOBILE.
- **`isApp`**: 네이티브 앱(genius-app, RN Expo WebView) 안인지 vs 모바일 브라우저인지. 앱 전용 UI(헤더/푸터 숨김, 네이티브 브리지 등)에 사용.

소비처 대체:

- **`preUrl`**(레거시: 앱/모바일이면 `/mobile/m`) → 컨트롤러에서 `DeviceResolver.resolve(request)` 결과로 뷰 prefix 결정하거나, JSP 에서 `${deviceType.mobile}` 로 분기.
- **모바일/웹 JSP 분기** → `<c:if test="${deviceType.mobile}">` / `<c:if test="${deviceType == 'WEB'}">`.
- **앱 전용 분기** → `<c:if test="${isApp}">` / `<c:if test="${!isApp}">`.

> **앱(genius-app) 연동 주의:** RN WebView 의 `source.headers` 는 **최초 문서 요청 1회에만** 적용되어 이후 화면이동/AJAX 에는 안 붙는다. 그래서 앱 판별의 주 채널은 **User-Agent 마커**다 — WebView 의 `userAgent` prop 에 `GeniusApp`(`DeviceResolver.APP_UA_MARKER`) 을 append 하면 모든 요청에 실린다. `X-Client-Type: app` 헤더는 보조로만 인식한다.

---

## 3. 인터셉터별 이관표

### 3.1 genius (`com.anymobi.common.interceptor`)

| 인터셉터 | 분류 | 처리 |
|---|---|---|
| `CustomLiteDeviceResolver` (device) | (c) | Spring Mobile 폐기(Spring6 미지원) → UA 파싱 유틸 + 앱은 헤더로 재구현. 모바일 유지 시에만(2.3) |
| `ContSessionCheckInterceptor` | (a) | **삭제**. AJAX 999 → 이제 401 + common-ajax 자동 refresh가 처리 |
| `ExamSessionCheckInterceptor` | (a) | **삭제** |
| `SessionCheckInterceptor` | (a)+(c) | 인증부분 **삭제**. `adminAuthList` 세팅(c)은 필요 메뉴에서 `@ControllerAdvice`나 조회로 |
| `ControllerExecuteInterceptor` | (d) | **이관 안 함 권장**(2.1). 디버깅 필요 지점에만 국소 로그 |
| `ControllerContectInfoInterceptor` | (d) | 인프라 아님. 접속통계 메뉴 이관 시에만, 소비 리포트 있으면(2.2) |
| `CourseInterceptor` | (d) | **course 메뉴 이관 시 함께.** 학습로그 적재는 해당 컨트롤러/서비스로 |
| `CommonMenuAuthInterceptor` (`@MenuAuth` 기반) | (b) | 메뉴별 `@PreAuthorize`로. 4절 참고 |
| `CommonMenuAuthCheckInterceptor` (TB_FRONT_MENU DB체크) | (b) | 메뉴테이블 기반 → 중앙 `AuthorizationManager` 또는 `@PreAuthorize`+권한 bean. 4.2 |
| `MicroSessionCheckInterceptor` | (a) 중복세션 | **특수** — 단일세션(중복로그인 차단) 정책. 6절 참고 |
| 비활성 `Menu*AuthInterceptor` 13개 | (b) | **각 메뉴 이관 시 그 메뉴의 `@PreAuthorize` 1개로** 흡수 (아래) |

비활성 13개는 "메뉴별 권한을 코드 옮기며 붙인다"의 완벽한 예시다. 매핑:

| 레거시 인터셉터 | 검사 내용 | 대체 `@PreAuthorize` 방향 |
|---|---|---|
| `MenuDeptAuthInterceptor` | 세션 `mgrYn=='Y'` | `@PreAuthorize("@auth.isManager()")` 또는 role |
| `MenuExpertAuthInterceptor` | 세션 `isExpert=='Y'` | 전문가 권한 bean/claim |
| `MenuExecutiveAuthInterceptor` | 세션 `isExecutive=='Y'` | 임원 권한 |
| `MenuTutorAuthInterceptor` | `adminAuthService.getTutorAuth` | `@PreAuthorize("@auth.hasTutorAuth()")` |
| `MenuHrderAuthInterceptor` | `getManagerCheck` | 〃 |
| `MenuTechAuthInterceptor` / `MenuLeadersAuthInterceptor` / `MenuExternalCourseInterceptor` | 각 서비스 `chk*AuthYn` | 서비스 호출을 권한 bean 메서드로 감싸 `@PreAuthorize` |
| `MenuCdsResultAuthInterceptor` / `MenuCdsLeaderResultAuthInterceptor` | CDS 결과 조회 권한 | 파라미터 기반이면 `@PreAuthorize("@auth.canViewCds(#id)")` |
| `MenuAuthCheckInterceptor` / `NewMenuDeptAuthInterceptor` | `checkAuth`/`getNewDeptAuth` | 〃 |

### 3.2 lms (`com.ktlms.common.interceptor`)

| 인터셉터 | 분류 | 처리 |
|---|---|---|
| `SessionCheckInterceptor` | (a)+중복로그인+(c) | 인증 **삭제**. 중복로그인은 6절. `adminAuthList`(c)는 advice/조회로 |
| `ControllerExecuteInterceptor` | (d) | **이관 안 함 권장**(2.1) |
| `AdminTaskMenuInterceptor` | (e) 빈 stub | **삭제** (구현 없음) |
| `ControllerContectInfoInterceptor` | (d) | 인프라 아님. 접속통계 메뉴 이관 시에만(2.2) |
| `AdminMenuAuthInterceptor` | (b) TB_AUTH_MENU+role+API 화이트리스트 | 메뉴는 중앙 `AuthorizationManager`/`@PreAuthorize`, API 패턴은 4.3 |

공통: `CustomMapArgumentResolver`(genius/lms 둘 다)의 `s_` 세션주입은 [session-to-jwt-guide.md](session-to-jwt-guide.md) 참고. `XSSInterceptor`(둘 다 미등록/죽은 코드)는 이관하지 않고, XSS는 CSP(이미 적용)+입력 검증 정책으로 별도 처리.

---

## 4. 메뉴 권한(@PreAuthorize) 작성 패턴

### 4.1 단순 role 검사

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/adm/xxx")
public String list(@AuthenticationPrincipal CustomUserDetails user, Model model) { ... }
```

### 4.2 DB 메뉴테이블 기반 (CommonMenuAuthCheck / AdminMenuAuth 대체)

레거시는 URL을 메뉴테이블에 대조하고 role-menu 매핑으로 허용 여부를 판단했다. 두 가지 방법:

**(권장, 전환기) 권한 bean + `@PreAuthorize`** — 메서드 옵트인이라 "코드 먼저"와 호환:
```java
@Component("menuAuth")
public class MenuAuthChecker {
    private final AdminMenuMapper mapper; // 메뉴/role 테이블 조회
    public boolean canAccess(String menuUri) {
        String userId = SecurityUtil.getCurrentUserId();
        return mapper.hasMenuAuth(userId, menuUri); // TB_AUTH_MENU + role-menu
    }
}
```
```java
@PreAuthorize("@menuAuth.canAccess('/adm/board/list')")
@GetMapping("/adm/board/list")
public String list(...) { ... }
```

**(전 메뉴 이관 완료 후) 중앙 `AuthorizationManager`** — URL 단위 deny-by-default. 인터셉터 동작에 가장 가깝지만, 안 붙인 메뉴가 다 막히므로 **마지막에** 켠다:
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/adm/**").access(adminMenuAuthorizationManager) // DB 조회
    .anyRequest().authenticated())
```

### 4.3 API 패턴 화이트리스트 (lms AdminMenuAuth의 `/api/`·`/sub/` 부분)

세션의 `AUTH_API_PATTERNS`로 정규식 매칭하던 로직 → 로그인 시 사용자 권한으로 허용 API 패턴 집합을 만들고, 4.2의 권한 bean에서 동일하게 검사하거나 API 컨트롤러에 `@PreAuthorize` 지정. `AUTH_API_PATTERNS`를 세션에 캐싱하던 것은 매 요청 조회 또는 짧은 캐시로 대체(세션 저장 금지).

---

## 5. TODO/추적 규약 (권한 누락 방지)

"코드 먼저"의 핵심 리스크는 **권한 배선을 잊고 운영에 나가는 것**이다. 규약으로 강제한다.

- 권한이 필요한데 아직 안 붙인 컨트롤러 메서드에는 반드시 주석 마커:
  ```java
  // TODO(sec): 레거시 CommonMenuAuthCheckInterceptor 대응 @PreAuthorize 필요 (menuUri=/adm/board/list)
  ```
- 이관 담당자는 원본 인터셉터의 exclude 목록과 대조해서, exclude가 아니었던(=권한 대상이었던) URL이면 마커를 남긴다.
- 운영 반영 전 `grep -rn "TODO(sec)" src/` 가 0건인지 확인 (릴리스 체크리스트에 포함).
- 원래 로그인 불필요(public)였던 URL(레거시 exclude·`permitAll` 성격)은 `SecurityConfig`의 permitAll 목록에 추가하고 마커 불필요.

---

## 6. 특수: 중복 로그인(단일 세션) 처리

genius `MicroSessionCheckInterceptor`, lms `SessionCheckInterceptor`는 "다른 곳에서 접속 시 기존 세션 무효화"를 **세션ID를 DB와 대조**해서 했다. STATELESS+JWT에는 HttpSession이 없으므로 그대로 못 옮긴다.

대체 방향(정책 결정 필요):
- portal은 이미 refresh token을 DB(userId+tokenId)로 관리하고 rotation 재사용을 감지한다.
- **단일 세션(다른 기기 로그인 시 이전 기기 로그아웃)** 을 원하면: 로그인 시 해당 userId의 기존 refresh token을 모두 삭제하고 새로 발급 → 이전 기기는 다음 refresh에서 401 → 재로그인.
- 이건 인터셉터 이관이 아니라 인증 정책이므로 로그인/JWT 작업과 함께 결정한다. (session-to-jwt-guide 8절과 연계)

---

## 7. 메뉴 이관 시 인터셉터 체크리스트

- [ ] 이 메뉴 URL이 레거시에서 어떤 인터셉터에 걸렸는지 `KT-interceptors.xml`의 mapping/exclude로 확인했다.
- [ ] (a) 로그인 체크만 걸려 있었다 → 별도 작업 없음 (Security가 처리).
- [ ] (b) 메뉴/role 권한이 걸려 있었다 → `@PreAuthorize` 붙였다. 아직이면 `TODO(sec)` 마커를 남겼다.
- [ ] (d) 메뉴 전용 로깅(CourseInterceptor류)이 있었다 → 해당 컨트롤러/서비스나 AOP로 옮겼다.
- [ ] 세션 참조는 `@AuthenticationPrincipal`/`SecurityUtil`/`${loginUser}`로 바꿨다 (session-to-jwt-guide).
- [ ] 레거시에서 public(exclude/비로그인)이었던 URL은 `SecurityConfig` permitAll에 반영했다.
- [ ] 운영 반영 전 `TODO(sec)` 잔여 0건을 확인했다.

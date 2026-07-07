# Session(CommandMap) → JWT 전환 가이드

분석 대상: `genious_prd`(genius), `lms_prd`(lms, module `KTedu_admin`)
기준 프로젝트: `ktlms-portal`(portal) — 이미 Spring Security + JWT(access/refresh, httpOnly cookie) 기반으로 구성되어 있음

이 문서는 메뉴를 하나씩 이관하는 담당자가 **session 접근 코드를 어떻게 JWT/SecurityContext 기반으로 바꿀지** 그대로 따라 할 수 있도록 만든 실전 가이드다.

---

## 0. 먼저 확인할 것 — `CommandMap`의 실체

genius/lms 모두 실제 클래스 이름은 **`CommandMap`**이다 (genius: `com.anymobi.common.util.CommandMap`, lms: `com.ktlms.common.util.CommandMap`). 구조는 동일하다.

```java
public class CommandMap {
    Map<String, Object> map = new HashMap<String, Object>();
    public Object get(String key) { return map.get(key); }
    public void put(String key, Object value) { map.put(key, value); }
    // ... HashMap을 감싼 단순 래퍼일 뿐, 그 자체는 세션과 무관
}
```

`CommandMap` 자체는 세션과 무관한 단순 `HashMap` 래퍼다. 진짜 핵심은 **컨트롤러가 이걸 어떻게 채워서 받는지**에 있다.

컨트롤러가 메서드 인자에 `CommandMap input`을 선언하면(`public String someAction(CommandMap input) {...}`), Spring이 호출하기 전에 커스텀 `HandlerMethodArgumentResolver`인 `CustomMapArgumentResolver`가 매 요청마다 자동으로 `CommandMap`을 만들어 채워준다 (genius: `com.anymobi.common.interceptor.CustomMapArgumentResolver`, lms: `com.ktlms.common.interceptor.CustomMapArgumentResolver`, `KT-interceptors.xml`에 전역 `argumentResolvers`로 등록됨). 이때 두 가지를 합쳐 넣는다.

1. **HTTP 요청 파라미터 전체** — `request.getParameterNames()`를 그대로(또는 URI에 `Ajax`/`Json`/`loginProcAjax`가 들어가면 JSON으로 파싱해서) 넣음
2. **로그인 세션 맵 전체** — `SessionUtil.getSession(request)`로 얻은 로그인 사용자 Map(세션 키 `"sessionMemberInfo"`, `SessionUtil.setSession()`이 로그인 성공 시 채워둔 것)을 **`s_` 접두어를 붙여** 그대로 복사

```java
// genius/lms CustomMapArgumentResolver 공통 핵심 로직
Map<String, Object> memberInfo = SessionUtil.getSession(request);
for (String sessionKey : memberInfo.keySet()) {
    commandMap.put("s_" + sessionKey, memberInfo.get(sessionKey));
}
```

그래서 컨트롤러 코드에서 흔히 보이는 `input.get("s_userid")`, `input.get("s_comp")` 같은 호출은 사실 **세션에 있던 로그인 사용자 정보를 CommandMap 경유로 읽는 것**이다. (참고: genius는 `lgip`, `email`, `handphone`, `pwd` 4개 키는 이 복사 대상에서 제외한다. lms는 이런 예외 없이 전부 복사한다.)

정리하면 실제 사용자 정보의 원본 저장소는 여전히 `SessionUtil`의 세션 맵이고, `CommandMap`은 그 값을 "요청 파라미터 + `s_` 접두어 세션 값"으로 한 번에 묶어서 컨트롤러에 넘겨주는 **자동 주입 통로**다. 이 문서에서는 `CommandMap`으로 세션 값을 읽는 패턴과, `SessionUtil.getSession()`/`sessionScope.*`로 직접 읽는 패턴을 모두 다룬다.

genius/lms 두 프로젝트의 키 이름·구조가 사실상 동일하다(같은 코드 계보). 그래서 매핑표도 하나로 통합한다.

---

## 1. 이 메뉴가 어떤 session 키를 쓰는지 찾는 방법

메뉴 이관 담당자는 자기가 옮기는 파일에서 아래 grep으로 실제 사용 키만 뽑아낸다. 전체 목록(2절)은 참고용이지 전부 옮길 필요는 없다.

```bash
# 대상 Java 파일에서 세션 직접 접근 + CommandMap 사용 지점 찾기
grep -n 'getSession()\|SessionUtil\.\|RequestBox\|box\.getSession\|CommandMap' ControllerName.java

# 대상 JSP에서 세션 참조 지점 찾기
grep -n 'sessionScope\.' pageName.jsp

# 실제 읽는 키 이름만 뽑기 (대략적인 추출, 눈으로 재확인 필요)
grep -oE 'sessionScope\.[a-zA-Z0-9_.]+' pageName.jsp | sort -u
grep -oE '(memberInfo|input)\.get\("[a-zA-Z0-9_]+"\)' ControllerName.java | sort -u
```

`input.get("s_xxx")` 형태로 나오면 `CommandMap`을 통해 세션 값을 읽는 것이다 (0절 참고). `s_` 접두어를 뗀 나머지(`xxx`)를 2절 매핑표에서 찾으면 된다.

추출한 키를 2절 매핑표에서 찾아 어느 방식(JWT claim / 서버 조회 / 폐기)으로 바꿀지 정하고, 없는 키면 이 문서에 새로 추가한다.

---

## 2. session 키 → JWT claim / user context 매핑표

portal의 JWT는 이미 다음 claim을 갖고 있다 (`JwtProvider.createAccessToken`, `JwtDTO`): `userId`(subject), `userNm`, `orgCd`, `comp`, `role`.

레거시 session 키는 아래 4가지 중 하나로 분류해서 처리한다.

### A. 이미 있는 JWT claim으로 대체

| 레거시 키 (genius/lms) | 의미 | 대체 방법 |
|---|---|---|
| `userid`, `emp_id`, `oldCN` | 로그인 ID | `jwtDTO.getUserId()` |
| `name`, `emp_nm` | 사용자명 | `jwtDTO.getUserNm()` |
| `comp` | 회사/기관 코드 | `jwtDTO.getComp()` |
| `hqorgcd`, `bonbu_cd`, `dept_cd`, lms `Member.orgCd`/`Member.compCd` | 조직 코드 | `jwtDTO.getOrgCd()` — 여러 레벨의 조직코드가 섞여 있으므로 메뉴 이관 시 **대표 조직코드 하나**로 정리해야 함. 안 맞으면 아래 B 참고 |

### B. JWT claim 신규 추가가 필요할 수 있는 것

| 레거시 키 | 의미 | 판단 |
|---|---|---|
| `gadmin`, `gadmin_open` | 관리자 등급 코드(`ZZ`, `A`, `A1` 등 세분화된 코드) | 현재 `JwtDTO.role`은 단일 role 문자열이라 `hasRole()`류 이분법 체크만 가능하다. JSP/컨트롤러에서 `gadmin eq 'A1'`처럼 코드값 자체를 비교하는 로직이 남아있으면, `JwtDTO`에 `adminGrade` 같은 필드를 추가하고 `JwtProvider`에 claim을 하나 더 태워야 한다. **이 필드가 필요한 첫 메뉴를 이관할 때 공통 작업으로 추가**하고, 이후 메뉴는 재사용한다. |
| `isadmin`, `mgr_yn`, `admr_yn` | 관리자/매니저 여부 플래그 | 단순 Y/N 분기면 `role`(`ROLE_ADMIN` 등)로 흡수. 코드값 조합이 복잡하면 B의 `gadmin`과 함께 정리 |

### C. JWT에 넣지 말고, 필요할 때 서버에서 조회

JWT는 신원(누구인지) + 최소 권한만 담아야 한다. 아래처럼 자주 안 바뀌는 프로필성 정보나, 민감정보는 **토큰에 넣지 않고** userId로 DB/서비스 조회한다.

| 레거시 키 | 의미 |
|---|---|
| `email_id`, `hometel` | 이메일, 전화번호 |
| `resno` | 주민등록번호로 추정 — **절대 JWT payload에 넣지 않는다** |
| `title_nm`, `post_nm`, `position_nm`, `jobcd`/`job_nm` | 직함/직책/직급 |
| `madan_chk`, `madan_admin`, `gyungryuk`, `hrdc`, `agency_cd` | 업무별 부가 플래그 |
| `grcode_kind`, `setup_mpm`, `new_job_cd`, `new_job_nm`, `off_grcode`, `has_tutor_out`, `isretire`, `maskingNm`, `gubun1`, `org_full_nm` | 조직/근태 부가 정보 |
| lms `Member.titleNm` 등 Member VO 필드 | 위와 동일 성격 |

### D. 이관하지 않고 폐기 (파생값/캐시값)

| 레거시 키 | 이유 |
|---|---|
| `userid_enc`, `name_enc` | 세션에 캐싱해두던 암호화/URL인코딩 값. 필요하면 그때그때 계산 |
| `appCheck`, `isDevice`, `userip`, `buserip`, `service_type` | 로그인 시점 요청 정보 캐시. 매 요청 User-Agent/서버 profile에서 새로 계산해야 정확함 |

### E. 로그인 완료 이전 임시 상태 (JWT/세션 대상 아님)

| 레거시 키 | 의미 |
|---|---|
| `authUserId`, `s_encSeq`, `successRedirectURL` | OTP 인증 등 로그인 중간 단계 임시값 |
| `SAMLRequest`, `RelayState`, `SAMLChannel` | SAML SSO 진행 중 임시값 |

이 값들은 애초에 "로그인 완료 후 데이터"가 아니라 "로그인 진행 중 상태"다. JWT는 로그인 성공 후에만 발급되므로 여기 들어갈 수 없다. redirect 파라미터, hidden field, 혹은 서버 side 짧은 TTL 캐시로 대체하고(8절 참고), **로그인 완료 즉시 폐기**되게 만든다.

### F. 메뉴 권한/캐시성 데이터 → 매 요청 조회 or `@PreAuthorize`

| 레거시 키 | 의미 |
|---|---|
| `adminAuthList`, `AUTH_API_PATTERNS`, `menuUri` | 최초 1회 조회 후 세션에 캐싱해서 쓰던 관리자 메뉴/권한 목록 |

세션 캐싱 대신 `@PreAuthorize` + 매 요청 권한 조회(필요시 짧은 캐시)로 전환한다. (`migration-checklist.md` 1.5/2.3 항목과 동일한 방향)

### G. `JwtDTO` 필드 확정안

현재 `JwtDTO`(`userId`, `userNm`, `orgCd`, `comp`, `role`, `s_userid`)는 초기 뼈대로 임시로 넣어둔 값이다. genius/lms 세션 키 전수 조사 결과를 반영해 아래처럼 확정한다.

`new-genius`/`new-admin`은 portal을 복제해서 만드는 **별도 배포 프로젝트**이므로 `JwtDTO`를 두 프로젝트가 100% 공유할 필요는 없다. 다만 필드명을 통일해두면 가이드/코드 재사용이 쉬우므로, **필드 구조(이름)는 공용, 채우는 값(role/adminGrade 산출 로직)은 앱별로 다르게** 가져가는 걸 권장한다.

| 필드 | 타입 | 용도 | genius 소스 | lms 소스 |
|---|---|---|---|---|
| `userId` | String | 로그인 ID (JWT subject) | `userid` | `userid` |
| `userNm` | String | 화면 표시용 이름 | `name` | `name` |
| `comp` | String | 회사/기관 코드 | `comp` | `comp` |
| `orgCd` | String | 대표 조직코드 (**어느 레벨을 쓸지 확정 필요**) | `hqorgcd`/`bonbu_cd`/`dept_cd` 중 하나 | lms `Member.orgCd`/`dept_cd` 중 하나 |
| `role` | String | Spring Security 권한 (`ROLE_USER`/`ROLE_ADMIN`) | `isadmin`/`mgr_yn`/`admr_yn` → USER 또는 ADMIN으로 단순화해서 산출 | 대부분 관리자 성격이라 기본 ADMIN, `gadmin`이 낮은 등급(`ZZ`)이면 USER로 낮추는 식으로 산출 |
| `adminGrade` *(신규 추가)* | String, nullable | 세분화된 관리자 등급 코드 (`gadmin eq 'A1'` 같은 기존 코드 비교 로직을 당장 못 없앨 때 사용) | 있으면 채우고 없으면 `null` | `gadmin` 값 그대로 (`A`, `A1`, `ZZ` 등) — lms는 사실상 필수 |

제거 권장: `s_userid`는 `userId`와 같은 값을 스네이크케이스로 중복 보관하던 필드다. 레거시 JSP 호환 목적이 명확히 있는 게 아니면 삭제하고 `userId`만 쓴다.

JWT에 넣지 않는 나머지(C/D/E/F)는 계속 서버 조회/폐기/`@PreAuthorize`로 처리한다 — `adminGrade`도 장기적으로는 메뉴별 `@PreAuthorize`/서버 조회로 대체하는 게 목표이고, 지금은 레거시 코드값 비교를 그대로 옮기기 위한 과도기용 필드다.

`orgCd`는 genius/lms 각각 "조직코드"라 부르는 값이 여러 레벨(본부/부서/사업부)로 섞여 있어서, 실제로 메뉴 이관하면서 어떤 레벨을 화면/쿼리에서 쓰는지 확인한 뒤 대표값 하나를 정해야 한다. 첫 메뉴 이관 시 이 값을 정하고 이 절에 기록해둔다.

```java
// JwtDTO 확정안 예시
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class JwtDTO {
    private String userId;
    private String userNm;
    private String comp;
    private String orgCd;
    private String role;        // ROLE_USER / ROLE_ADMIN
    private String adminGrade;  // nullable, 과도기용 (예: lms gadmin 코드)
}
```

```java
// JwtProvider.createAccessToken() 에 claim 추가
.claim("orgCd", jwtDTO.getOrgCd())
.claim("adminGrade", jwtDTO.getAdminGrade())
```

---

## 3. 공통 인프라 — 메뉴 이관 전에 한 번만 추가

아래 두 가지는 특정 메뉴 소속이 아니라 **portal 공통 코드**다. 첫 메뉴를 이관하는 사람이 없으면 만들고, 이후 메뉴 이관자는 재사용만 한다.

### 3.1 Service 계층에서 현재 사용자를 얻는 헬퍼

`@AuthenticationPrincipal`은 컨트롤러 메서드 인자에서만 쓸 수 있다. Service/컴포넌트 안에서 필요하면 아래 유틸을 추가한다.

```java
// com.kt.ktedu.core.security.auth.SecurityUtil (신규)
public class SecurityUtil {
    public static JwtDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        return userDetails.getJwtDTO();
    }
}
```

### 3.2 JSP에서 로그인 사용자를 참조할 수 있게 노출

지금 portal에는 JSP에 로그인 사용자를 자동으로 넣어주는 장치가 없다(`WebConfig`에 인터셉터/`@ControllerAdvice` 미등록 상태). 아래처럼 공통으로 하나 추가한다.

```java
// com.kt.ktedu.core.web.CurrentUserModelAdvice (신규)
@ControllerAdvice
public class CurrentUserModelAdvice {
    @ModelAttribute("loginUser")
    public JwtDTO loginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getJwtDTO();
        }
        return null;
    }
}
```

이후 모든 JSP에서 `${loginUser.userId}`, `${loginUser.userNm}` 형태로 접근 가능해진다.

---

## 4. Controller 이관 예시

### 4.1 세션에서 기본 사용자 정보 읽기

Before (genius/lms 공통):
```java
Map<String, Object> memberInfo = SessionUtil.getSession(request);
String userid = String.valueOf(memberInfo.get("userid"));
String comp = String.valueOf(memberInfo.get("comp"));
```

Before (genius/lms 공통 `CommandMap` 자동 주입 방식 — 0절 참고):
```java
public String someList(CommandMap input) {
    String userid = (String) input.get("s_userid");
    String comp = (String) input.get("s_comp");
}
```

After (portal):
```java
@GetMapping("/some")
public String someList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    JwtDTO me = userDetails.getJwtDTO();
    String userId = me.getUserId();
    String comp = me.getComp();
    ...
}
```

### 4.2 Service 계층에서 필요할 때

Before: 세션 Map을 파라미터로 그대로 전달
```java
public List<Course> getMyCourseList(Map<String, Object> memberInfo) {
    String userid = (String) memberInfo.get("userid");
    ...
}
```

After: userId만 명시적으로 전달 (세션 객체 자체를 서비스로 넘기지 않는다)
```java
public List<Course> getMyCourseList(String userId) {
    ...
}

// 호출부
courseService.getMyCourseList(SecurityUtil.getCurrentUser().getUserId());
```

### 4.3 세션에 없는 부가 정보(이메일, 직급 등) 조회

Before:
```java
String email = String.valueOf(memberInfo.get("email_id"));
```

After: DB/서비스 조회로 전환 (2절 C항목)
```java
UserProfile profile = userProfileService.getByUserId(me.getUserId());
String email = profile.getEmail();
```

### 4.4 관리자 권한 분기 (`gadmin`/`isadmin` 코드 체크)

Before:
```java
if (input != null && "Y".equals(input.get("isadmin")) && input.get("adminAuthList") == null) {
    input.put("s_userid", input.get("userid"));
    List<Map<String, Object>> adminAuthList = adminAuthService.getAdminAuthList(input);
    input.put("adminAuthList", adminAuthList);
}
```

After: URL 단위는 `@PreAuthorize`, 화면 데이터가 필요하면 매 요청 조회(세션 캐싱 금지)
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/adm/xxx")
public String adminList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    List<MenuAuth> adminAuthList = adminAuthService.getAdminAuthList(userDetails.getJwtDTO().getUserId());
    model.addAttribute("adminAuthList", adminAuthList);
    ...
}
```

---

## 5. Interceptor/필터 이관 예시

Before (genius `SessionCheckInterceptor` 등 — 로그인 여부 체크):
```java
if (SessionUtil.getSession(request) == null) {
    RedirectUtil.redirect(request, response, "로그인 정보가 필요한 서비스입니다.", url);
    result = false;
}
```

After: 이 역할 전체가 `SecurityConfig`로 흡수된다. `SessionCreationPolicy.STATELESS` + `.anyRequest().authenticated()` + `JwtAuthenticationFilter`가 이미 처리하므로, **이런 로그인 체크 인터셉터는 이관하지 않고 삭제 대상으로 분류**한다. (`migration-checklist.md` 2.3과 동일한 판단 기준)

메뉴 접근 권한(`AdminMenuAuthInterceptor` 류)만 필요 시 `HandlerInterceptor` 또는 `@PreAuthorize`로 남기고, 그 안에서 세션 대신 `SecurityUtil.getCurrentUser()`를 쓴다.

---

## 6. JSP 이관 예시

Before:
```jsp
<input type="hidden" id="inPicAdmrId" value="${sessionScope.sessionMemberInfo.userid}">
<c:out value="${sessionScope.sessionMemberInfo.name}"/>
<c:if test="${sessionScope.gadmin eq 'A1'}">
    <button role="ans">답변자 지정</button>
</c:if>
```

After (3.2의 `loginUser` 공통 노출 사용):
```jsp
<input type="hidden" id="inPicAdmrId" value="${loginUser.userId}">
<c:out value="${loginUser.userNm}"/>
<c:if test="${loginUser.role == 'ADMIN'}">
    <button role="ans">답변자 지정</button>
</c:if>
```

`gadmin`처럼 세분화된 코드 비교(`eq 'A1'`)가 꼭 필요하면, 2절 B항목대로 `JwtDTO`에 `adminGrade` 필드를 추가한 뒤 `${loginUser.adminGrade eq 'A1'}` 형태로 바꾼다.

---

## 7. AJAX 이관 예시

Before: `JSESSIONID` 쿠키로 자동 인증되어 별도 처리 불필요

After: portal의 access token은 httpOnly 쿠키(`access_token`)에 담기므로, 브라우저 요청에 쿠키만 포함되면(`credentials`) 자동 전송된다. `common-ajax.js` 공통 요청 함수가 쿠키를 포함하는지 확인한다(`migration-checklist.md` 5.5 항목).

```js
fetch(url, {
  credentials: 'include'   // 쿠키 자동 전송
});
```

외부 앱/모바일처럼 쿠키를 못 쓰는 클라이언트만 Authorization 헤더를 명시적으로 붙인다.
```js
fetch(url, {
  headers: { 'Authorization': 'Bearer ' + accessToken }
});
```

### 토큰 만료 정책

genius/lms 공통(portal base) 정책. 상수는 `JwtProvider` 에 정의.

| 항목 | 값 | 의미 |
|---|---|---|
| Access token 수명 | 30분 (`ACCESS_EXPIRATION_MS`) | 만료되면 아래 흐름으로 조용히 재발급 |
| 유휴(idle) 만료 | 3시간 (`IDLE_EXPIRATION_MS`) | 마지막 재발급(활동) 후 3시간 동안 요청이 없으면 세션 종료. 활동할 때마다 갱신 |
| 절대(absolute) 만료 | 24시간 (`ABSOLUTE_EXPIRATION_MS`) | 최초 로그인 후 최대 24시간. 활동 중이어도 초과 시 재로그인 |

구현 방식: refresh token 의 실제 만료(`exp`) = `min(now + 유휴 3시간, 절대 마감)`. 매 재발급마다 유휴 시계는 `now+3시간`으로 리셋되지만, 절대 마감(`absExp` claim)은 최초 로그인 값을 그대로 물려받아 연장되지 않는다. 그래서 "활동 중엔 안 끊기되, 3시간 놀면 풀리고, 아무리 활발해도 24시간이면 재로그인"이 된다.

> 참고: access token 이 30분짜리라, 세션 마감 직전 발급된 access token 때문에 실제 종료가 최대 30분까지 밀릴 수 있다(예: 유휴 3시간 + 최대 30분). 초 단위로 정확히 끊어야 하면 재발급 시 access token 만료도 세션 마감으로 clamp 하면 된다(현재는 미적용).

### 토큰 만료 시 처리

이 처리는 **공통으로 이미 구현되어 있다** (`common-ajax.js`). `callAjax`/`getAjax`/`postAjax`를 쓰는 요청은 아래 흐름을 자동으로 탄다.

1. `JwtAuthenticationFilter`가 access token 만료 시 401 + `{"result":"TOKEN_EXPIRED"}`를 반환한다.
2. `common-ajax.js`의 공통 error 핸들러가 401을 받으면 `refreshAccessToken()`으로 `/auth/refresh`를 호출한다 (refresh token은 httpOnly 쿠키로 자동 전송, `_refreshPromise`로 동시 요청 시 refresh 중복 호출 방지).
3. 재발급 성공 → 원래 요청을 1회 자동 재시도(`_isRetry` 플래그로 무한 루프 방지).
4. 재발급 실패(refresh token 만료/무효) → `redirectToLogin()`으로 로그인 화면 이동.

따라서 메뉴 이관자는 **공통 AJAX 함수만 쓰면 토큰 만료 처리를 따로 신경 쓸 필요가 없다.** `$.ajax`를 직접 쓰는 레거시 코드는 이 흐름을 안 타므로, 이관 시 `callAjax` 계열로 바꾸는 것을 원칙으로 한다.

> 서버 측 주의: `JwtAuthenticationFilter`는 `/auth/**` 경로를 `shouldNotFilter`로 건너뛴다. 이렇게 하지 않으면 만료된 access token 쿠키가 `/auth/refresh` 요청에도 함께 전송되어 필터가 refresh 엔드포인트 도달 전에 401로 막아버리기 때문이다.

---

## 8. 그래도 session이 필요한 예외 상황

portal은 `SessionCreationPolicy.STATELESS`이므로 원칙적으로 `HttpSession` 사용 금지다. 유일한 예외 후보는 **로그인 완료(JWT 발급) 이전의 멀티스텝 로그인 플로우**(OTP 인증 대기, SAML 왕복 등 2절 E항목)뿐이다.

이 경우도 우선순위는:
1. 요청 파라미터/hidden field로 클라이언트가 들고 있게 한다.
2. 서버 side에 짧은 TTL의 별도 저장소(캐시/임시 테이블)로 상태를 두고 키만 클라이언트가 들고 다니게 한다.
3. 위 두 가지가 정말 불가능한 최소한의 경우에만 `HttpSession`을 예외적으로 허용하되, **로그인 완료 즉시 해당 세션 값을 명시적으로 제거**한다.

새로 어떤 메뉴가 "이건 세션이 꼭 필요하다"고 판단되면, 이 절에 사례를 추가하고 이유를 남긴다.

---

## 9. 메뉴 이관 시 체크리스트 (요약)

- [ ] 1절 grep으로 이 메뉴가 쓰는 session 키를 뽑았다.
- [ ] 뽑은 키를 2절 표에서 A~F 중 어디에 해당하는지 분류했다.
- [ ] B(신규 claim 필요) 항목이 있으면 `JwtDTO`/`JwtProvider` 공통 수정이 먼저 됐는지 확인했다.
- [ ] Controller의 세션 접근을 `@AuthenticationPrincipal CustomUserDetails`(또는 `SecurityUtil.getCurrentUser()`)로 바꿨다.
- [ ] JSP의 `sessionScope.*` 참조를 `loginUser.*`로 바꿨다.
- [ ] 로그인 여부만 체크하던 Interceptor는 삭제하고 `SecurityConfig`에 위임했다.
- [ ] 관리자/메뉴 권한 체크는 `@PreAuthorize` 또는 매 요청 조회로 바꾸고 세션 캐싱을 제거했다.
- [ ] AJAX 요청이 쿠키/헤더로 인증 정보를 정상 전달하는지 확인했다.
- [ ] 이 메뉴에 세션이 꼭 필요한 예외 케이스가 있었다면 8절에 기록했다.

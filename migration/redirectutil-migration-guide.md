# RedirectUtil 이관 가이드

분석 대상: `genious_prd`(genius), `lms_prd`(lms)
기준 프로젝트: `ktlms-portal`(portal) — [RedirectUtil.java](../src/main/java/com/kt/ktedu/core/web/RedirectUtil.java)

## 배경

genius/lms의 레거시 `RedirectUtil`은 `response` 에 직접 `<script>alert(...); location.href=...</script>` 를 써서 화면을 이동시키는 방식이다. portal은 호출부를 **기존 시그니처 그대로** 쓸 수 있도록 동일 이름의 `RedirectUtil` 을 제공하되, 레거시 방식 메서드는 전부 `@Deprecated` 로 두고 **이관 시 신규 방식으로 교체**한다.

신규 방식(권장): 컨트롤러가 `RedirectAttributes ra` 를 받고 flash 메시지 + redirect 뷰네임을 반환한다. 메시지는 이동한 화면에서 [flash-message.jsp](../src/main/webapp/WEB-INF/views/common/util/flash-message.jsp)가 자동 표시(success/info=스낵바, error/warn=알림창)한다.

```java
// 신규 권장 방식
return RedirectUtil.successRedirect(ra, "저장되었습니다.", "/board/list");
return RedirectUtil.errorRedirect(ra, "권한이 없습니다.", "/main");
```

권장 메서드(비-deprecated): `success/error/info/warn(ra, msg)`, `successRedirect/errorRedirect/infoRedirect/warnRedirect(ra, msg, url)`.

---

## 호출부 빈도 (2026-07-07 기준)

레거시 파일은 **EUC-KR + CRLF** 라 `grep` 은 `-a`(text 모드)로 집계했다.

| deprecated 메서드 | genius | lms | 대체 방향 |
|---|---:|---:|---|
| `redirect()` (오버로드 3종) | 143 | 25 | `errorRedirect`/`successRedirect(ra, msg, url)` + 필요 시 `ra.addAttribute(...)` |
| `historyBack()` | 117 | 214 | AJAX → `ApiException`/`ResponseDTO.fail`, 화면 → 폼 뷰 재반환 + model 에러 |
| `popRedirect()` | 5 | 5 | `openModal`/`closeModal` + 콜백 |
| `redirectInit()` | 5 | 0 | `errorRedirect(ra, msg, url)` |
| `popRedirectReload()` | 3 | 0 | `closeModal()` 후 목록 재조회 |
| `redirectAdmin()` | 2 | 3 | `errorRedirect`(프레임셋 없으니 일반 redirect) |
| `popRedirectFunctionActive()` | 1 | 0 | 모달 콜백 / `postMessage` |
| `windowClose()` | 0 | 1 | 프런트 `closeModal()` / `window.close()` |
| **합계** | **~276** | **~248** | |

- `RedirectUtil` 참조 파일 수: **genius 49개 / lms 19개**.
- **호출 0건이라 portal 에서 이미 삭제한 메서드**: `redirectLogonPage`, `redirectParentLogonPage`, `parentReload`.
  - 인증 만료 이동(logon 계열)은 Spring Security 가 중앙 처리하므로 애초에 대체가 아니라 제거 대상이었다.

---

## 대체 패턴 (4갈래)

### 1. 인증/로그인 이동 → 삭제
레거시 `redirectLogonPage`, `redirectParentLogonPage`. 세션 만료 시 로그인 페이지로 보내던 것.
→ **삭제.** Spring Security 가 담당한다.
- 화면 요청: `SecurityConfig` authenticationEntryPoint 가 로그인 이동.
- AJAX 요청: 401 → `common-ajax.js` 가 refresh 후 재시도, 실패 시 로그인 이동.
- 컨트롤러의 세션 유무 직접 검사 코드도 함께 제거.

### 2. alert + 이동 → `RedirectAttributes` 반환
레거시 `redirect(req,res,message,viewName)`, `redirectAdmin`, `redirectInit`.
```java
// before
RedirectUtil.redirect(request, response, "권한이 없습니다.", "/main");
// after — 컨트롤러가 RedirectAttributes 받고 반환
return RedirectUtil.errorRedirect(ra, "권한이 없습니다.", "/main");   // 오류
return RedirectUtil.successRedirect(ra, "저장되었습니다.", "/board/list"); // 성공
```
- `redirectAdmin`(`top.location`)은 프레임셋을 안 쓰는 신규에선 일반 redirect 와 동일.
- 이동 대상에 파라미터를 넘겨야 하면 `ra.addAttribute("key", value)` → redirect URL 쿼리 파라미터.

### 3. 파라미터 유지 이동 → `ra.addAttribute` / GET 재조회
레거시 `redirect(req,res,Map,viewName)`, `redirect(req,res,Map,message,viewName)`.
검색조건 등을 hidden 으로 POST 하던 패턴.
```java
ra.addAttribute("searchType", searchType);              // 유지할 파라미터 → 쿼리스트링
return RedirectUtil.successRedirect(ra, "처리되었습니다.", "/board/list"); // 메시지는 flash
```
대상 화면은 GET 파라미터로 재조회한다. 단순 이동이면 `return "redirect:" + url;`.

### 4. 팝업/부모창 조작 → 모달 or postMessage
레거시 `popRedirect`, `popRedirectReload`, `popRedirectFunctionActive`, `windowClose`.
신규는 window.open 팝업 대신 in-page 모달(`openModal`)을 쓴다.
- 처리 후 `closeModal()` + 목록 재조회 콜백으로 부모 화면 갱신.
- 실제 새 창을 유지해야 하면 서버 스크립트가 아니라 프런트에서 `window.opener.*` / `window.postMessage(...)` 로 처리.

### 별도: 검증 실패 (`historyBack`) — 가장 많음
```java
// AJAX 요청 (권장) — common-ajax 가 openAlert 로 표시, 화면 유지
throw new ApiException("입력값을 확인해주세요.");
// 또는
return ResponseDTO.fail("입력값을 확인해주세요.");
```
```java
// 화면(폼 제출) — 폼 뷰를 다시 반환하고 에러/입력값을 model 에
model.addAttribute("errorMessage", "입력값을 확인해주세요.");
return "board/form";   // history.back 대신 폼 재렌더링
```

---

## 이관 우선순위

1. **`historyBack` (lms 214 / genius 117)** — 검증 실패 처리 표준부터 확정. 대부분 AJAX면 `ApiException` 한 줄로 정형화 가능.
2. **`redirect()` (genius 143 / lms 25)** — 오버로드 3종(메시지+url / Map / Map+메시지)이 섞여 있어 **호출부마다 시그니처 확인 필요**(grep 자동 구분 불가). 2·3번 패턴으로 분기.
3. **팝업류(총 ~15건)** — 모달 재작업이라 해당 팝업 메뉴 이관 시 개별 처리.

## 참고
- 레거시 `RedirectUtil` 및 호출 파일 다수가 **EUC-KR + CRLF** → 이관 시 **UTF-8 변환** 필요(tiles-removal-guide, migration-checklist 와 동일 이슈).
- 각 deprecated 메서드의 상세 대체 안내는 [RedirectUtil.java](../src/main/java/com/kt/ktedu/core/web/RedirectUtil.java) 의 `@deprecated` Javadoc 참고(IDE 에서 호출부 hover 시 표시).

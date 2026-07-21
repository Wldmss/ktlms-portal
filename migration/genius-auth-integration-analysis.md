# genius 로그인/인증 연동 전수 분석 & portal 이관 현황

> 목적: genius(`kt_source_2607/genius_prd`, 세션 기반)의 **모든 로그인·로그인 연동** 진입점을 프로세스 순으로 정리하고,
> portal(`ktlms`, Spring Security + JWT)에 **무엇이 이관됐는지 / 더 필요한지 / JWT 전환으로 무엇이 바뀌는지** 정리한다.
>
> 관련 문서: [genius-login-migration.md](genius-login-migration.md)(표준 LoginController 이관 메모), [session-to-jwt-guide.md](session-to-jwt-guide.md), [interceptor-to-security-guide.md](interceptor-to-security-guide.md)
>
> 작성 기준일: 2026-07-15

---

## 0. 한눈에 보기 (이관 현황 요약)

| # | genius 진입점 | 종류 | portal 이관 상태 | 비고 |
|---|---|---|---|---|
| A | 표준 웹 로그인 (LDAP/DB + RSA + SMS OTP) | id/pw | ✅ **이관됨** | `LoginController`/`AuthController`/`LoginService`. 단 DB 미연동 → mock/fallback 다수 |
| B | SSO-우회 임시 로그인 (checkResult/cds, testDev/exam) | 우회 | ✅ **이관됨** | `cds/examLoginProcAjax`, `checkResult/testDev` |
| C | 레거시 폼 로그인 + KATE SSO (loginProc, mKate) | SSO | 🟡 **부분** | 폼 로그인 O / KATE는 `LoginService.isSsoLogin` 플래그만 존재, 핸드셰이크 미구현 |
| D | 앱 로그인 API (`CommonApiController`) | 앱 | ❌ **미이관** | DTO(VO) 껍데기만 이관(`auth/ldap/dto/Login*DTO`), 컨트롤러/서비스 없음 |
| E | SAML IdP (`OpenSAMLController`) | SAML | ✅ **이관됨** | `SamlController`(OpenSAML, 서명키/메타데이터). genius는 login.do가 주석 상태였음 |
| F | 외부 제휴 SSO (Kyobo/BTS/Highway/mKate/CodeGenius) | 제휴 | ❌ **미이관** | portal에 제휴 컨트롤러 없음 |
| G | 시험 JWT SSO (`getJwtToken`) | 아웃바운드 | ❌ **미이관** | genius는 JWT를 **만들지 않고** 외부 평가시스템에 위임(아웃바운드 오케스트레이터) |
| - | Entra/OIDC SSO | SSO | ⚪ **스텁만** | `EntraSSOController`/`EntraOidcConfig` 전체 주석. genius 원본 없음(portal 신규 예정) |

**핵심 결론**
- 요청받은 연동 중 **실제로 이관된 건 SAML IdP(E)뿐**이고, **앱 로그인(D)·외부 제휴(F)·시험 JWT(G)는 미이관**.
- 표준 로그인(A/B)은 골격 이관 완료됐으나 **DB 미연동 상태의 mock/fallback**이 많아 실연동 검증 필요.
- genius의 인증 상태는 전적으로 **HttpSession(멤버 세션 백) + 정적 Map + `TB_USER_SESSION`**에 있음 → portal은 이걸 **JWT(access/refresh 쿠키) + `tb_refresh_token`**로 대체. 이 전환이 아래 5장의 모든 수정 포인트의 뿌리.

---

## 1. genius 인증 모델 개요 (세션 기반)

portal이 대체해야 할 genius의 3중 세션 메커니즘:

1. **멤버 세션 백** — `SessionUtil.setSession(request, memberInfo)` 로 로그인 사용자 프로필(~50개 필드)을 HttpSession(`sessionMemberInfo` = `Constants.VL_SESSION`)에 저장. **이 백의 존재 = "로그인됨"**. 모든 하위 페이지가 `sessionScope.sessionMemberInfo`로 읽음.
   - 대표 필드: `userid`, `userid_enc`, `name`, `comp`, `jobcd`, `hqorgcd`, `isretire`, `mgr_yn`, `admr_yn`, `service_type`, `appCheck`(앱 로그인 표식), `isDevice` 등.
2. **과도기 흐름 속성(transient)** — RSA/OTP 다단계 핸드셰이크용 HttpSession 속성:
   - `s_encSeq` (RSA 키쌍 시퀀스), `authUserId` (OTP 대기 사용자), `successRedirectURL` (OTP 성공 후 이동), `sso_id`/`SAMLRequest`/`RelayState`/`SAMLChannel` (SSO 캐리오버), `gadmin`/`gadmin_open` (관리자 재인증).
3. **중복 로그인 레지스트리** — `SessionUtil` 정적 `userSessionMap`(JVM 내) + DB `TB_USER_SESSION`(`upsertUserSession`/`getSessionIdByUser`). ⚠️ 정적 Map은 클러스터링에서 안 맞음.

**인증 판정**: 회사(`comp`)별로 LDAP(`getLdapCompanyYn`=Y → `ADUtilSSL.auth_loginPeriod`) vs DB 평문 비교(`isTest`=Y) 분기. `isOk` 정수 상태머신(-9 만료, 1 성공, -6 퇴직, -3/-8 오답, -4 잠금)이 **5개 핸들러에 중복 구현**됨. dev/test 서버타입은 `isOk=1` 강제(**이관 금지**).

---

## 2. 진입점 전수 목록 (프로세스 순)

### 2.1 표준 웹 로그인 — LDAP/DB + RSA + SMS OTP
원본: `usr/login/controller/LoginController.java`, `service/impl/LoginServiceImpl.java`, `AdminAuthImpl.java`

RSA(2요청) + OTP(3요청) 스테이트풀 핸드셰이크. 순서:

1. **`GET /login`** — `removeSesssionWithoutSAML`(SAML 속성만 보존하고 세션 클리어), 쿼리/`x-error`/`%` 안티인젝션 가드, `getPopupInfo`(배너), 이미 로그인+OTP완료면 메인 리다이렉트.
2. **`POST /getEncKeyAjax`** [RSA-1] — `encSeq=random(1..1e6)`, `getRsaPublicKey`(`TB_RSA_LOGIN`) → 응답 + **session `s_encSeq` 저장**. 클라(login.jsp)는 JSEncrypt로 pwd 암호화.
3. **`POST /loginProcAjax`** [메인] — 순서:
   - RSA 가드: `s_encSeq` 없으면 `isOk=-99`. 있으면 소비 후 `getRsaPrivateKey` → `RSAEncryptUtil.decode` 로 pwd 복호화.
   - `successRedirectURL` 해석 + 오픈리다이렉트 가드(`menuUrlChk`).
   - 외부(8888/경기·KDT) 멤버 분기(`getIsEtcMember`/`getEtcMemberInfo`) → 성공 시 즉시 `setSession`.
   - 표준 멤버: `getIsMember`→`getMemberInfo`→LDAP/DB 인증→fail-count 게이트.
   - 성공 시 **OTP 게이트**: `getOtpLockCheck`(`S`/`F`) + `getCheckLoginIp`(같은 /16 대역이면 OTP 스킵) 또는 `SMS_BYPASS=Y` → 즉시 로그인. 아니면 **`authUserId`/`successRedirectURL` 세션 저장 후 `result=N`**(멤버 백 아직 안 만듦).
4. **`GET|POST /sendSmsOptAuthAjax`** [SMS 발송] — `authUserId` 필수. `getSendSmsInfo`, 6자리 serial, `getOtpLockCheck`=S면 `insertSmsAuth`(실제 SMS 게이트웨이 + `TZ_SMSAUTH`).
5. **`POST /sendSmsAuthCheckAjax`** [SMS 검증 = 실제 로그인] — `authUserId` 필수. `getSmsAuthCheck`(Y/N/F/T/null). `Y`면 `updateLoginFailInit`+`updateOptAfter`, `getMemberInfo` enrich, `insertLoginLog`, **`setSession`(여기서 멤버 백 최초 생성)**, 중복체크+`upsertUserSession`, `authUserId`/`successRedirectURL` 제거.
6. **`POST /forceLoginAjax`** — `invalidateAllSessions(userId)` 후 새 세션+`setSession`(다른 기기 강제 로그아웃).
7. **`GET|POST /confirmPwdAjax`** — 외부(8888) 멤버 초기 비번 설정(정규식 `^(?=.*[a-zA-Z])(?=.*\d)(?=.*\W).{8,}$`).
8. **`/logout`** — `appCheck=Y`면 앱 loginKey 만료, `removeUserSession`+`invalidate`+`removeSession`.

### 2.2 SSO-우회 임시 로그인
- **`/checkResult` + `POST /cdsLoginProcAjax`** — 역량진단 결과용. **RSA 없음**, OTP 챌린지 없음(`getOtpLockCheck`=S면 바로 로그인). 기본 이동 `/cds/result/...`. `setSession`+`updateLoginCnt`.
- **`/testDev` + `POST /examLoginProcAjax`** — 대량평가 결과용. 구조 동일, 기본 이동 `/exam/result/...`.

### 2.3 레거시 폼 로그인 + KATE SSO
- **`GET|POST /loginProc`** (`doLogin`) — 비-AJAX 로그인 + **SSO 우회**: `p_issso=Y`면 **`userid=session.sso_id`(비번 없이)**, `issso=Y`/`p_crmlogin=Y`면 `isOk=1` 강제. 성공 시 `setSession`, `insertLoginLog(loginPath=KATE)`.
- **`/mobile/m/loginProc.do`** (`mobile_doLogin`) — 모바일 트윈. **`mKate=Y`** 를 비번 우회 성공 조건으로 추가. `isDevice=Y`, `con_device=M`.

### 2.4 앱 로그인 API (`CommonApiController`)
원본: `usr/api/controller/CommonApiController.java`, `service/impl/CommonApiServiceImpl.java`
- 전 필드 **AES-256/CBC 암호화**(`APP_ENC_KEY=ktedu.app.enckey`), 응답 `rtnSts=aesCBCEncode(status+today)`. 비번은 추가로 RSA. 페이로드에 `yyyyMMddHHmmss` 프리픽스(리플레이 방지).
- 시퀀스: **getRsaKey(009) → loginProc(002, 최초 비번 로그인) → (신규기기) sendSmsOpt(005) → checkSmsAuth(004, loginKey 발급) → 이후 loginKeyProc(003, 비번 없이 자동로그인)**.

| IF | URL | VO | 요지 |
|---|---|---|---|
| 009 | `/api/login/getRsaKey` | `LoginRsaVo` | RSA 공개키 교환(`getRsaPublicKey`) |
| 002 | `/api/login/loginProc` | `LoginFirstVo` | id/pw 최초 로그인. LDAP/DB 인증 → 성공 시 `SessionUtil.setSession`+**`appCheck=Y`** (토큰 미발급) |
| 005 | `/api/login/sendSmsOpt` | `LoginSmsVo` | 신규기기 SMS OTP. `deviceToken`을 `lgIp`로 저장 |
| 004 | `/api/login/checkSmsAuth` | `LoginSmsAuthVo` | OTP 검증 + **기기 등록(`upsertUserDev`) + `loginKey` 발급**(`loginKey=userId+today+deviceToken`, AES) |
| 003 | `/api/login/loginKeyProc` | `LoginVo` | `loginKey`로 자동로그인(비번 없음). `getDevInfoByLoginKey`로 userId 복원 → `setSession`+`appCheck=Y` |

- **앱 인증 = 웹과 동일하게 결국 HttpSession**(`appCheck=Y` 표식) + **영속 기기 크리덴셜 `loginKey`(TB_USER_DEV)**. `con_device=A`, `lgip=devId`.
- 앱 전용 매퍼: `usr.api.upsertUserDev/getDevInfoByLoginKey/upsertUserLogin/getPwdExpireDateByUserId/checkExpUserInfo/updatePwdErrCnt`.

### 2.5 SAML IdP (`OpenSAMLController`)
원본: `usr/sso/controller/OpenSAMLController.java`, `OpenSAMLUtil.java`, `vo/SAMLProperties.java`
- genius = **SAML 2.0 IdP**. SP는 Ping Identity(`PingConnect`) → Udemy 브릿지. genius가 `SAMLResponse`를 서명해 Ping ACS로 auto-POST.
- 매핑 `/sso/{channel}*` (`{channel}`=udemy 자유형).
  - **`/sso/{channel}/login.do`** (SP-initiated 진입) — ⚠️ genius 원본에서 **주석 처리됨**. 의도: SAML 속성 세션 저장 → 네이티브 로그인 → 로그인 후 `successRedirectURL=/sso/{channel}/login.do`로 복귀.
  - **`/sso/{channel}/saml/{email}`** (TEST, 활성) — `createSamlResponse`(RSA_SHA256 서명, bearer, `email` 속성, ±10분 유효) → auto-submit HTML form.
  - **`/sso/{channel}/metadata`** / `/metadata/download` — IdP EntityDescriptor XML.
- 서명키: `conf/saml/certificate-kt.crt` + `private-kt.key`(BouncyCastle PEM). **수신 `AuthnRequest` 서명검증은 안 함**.
- 세션: `SAMLChannel`/`SAMLRequest`/`RelayState`. 로그인 성공 시 `checkSAMLSession`이 memberInfo에 채널 주입.

### 2.6 외부 제휴 SSO (`ExternalController`)
원본: `usr/external/controller/ExternalController.java`
- 공통(Kyobo/BTS/Highway): `TB_WEB_TOKEN` 토큰 검증 API. **HMAC/공유시크릿 없음**(토큰 존재+미만료 `flag='T'`가 신뢰). **genius 세션 생성 안 함** — JSON 반환, 파트너 측에서 로그인.

| 파트너 | URL | 방향 | 식별/응답 |
|---|---|---|---|
| Kyobo | `/external/kyoboView` | inbound(read) | `token`→평문 `userId`/`userName` (kb01/02/03) |
| BTS | `/external/btsView` | inbound(read) | AES 암호화 응답. ⚠️`contId` 하드코딩(`100007544`) |
| Highway | `/external/highwayView` | inbound(read) | AES 암호화 응답 |
| mKate | `/external/mKateAuth` | inbound+콜백 | `token`+`corpFlag` → 파트너 도메인 콜백 → 성공 시 **`redirect:/mobile/m/loginProc.do?userid=..&mKate=Y`**(실제 로그인은 여기서) |
| CodeGenius | (미구현) | - | `CodeGeniusVO`만 존재, 컨트롤러/서비스 없음(스텁) |

### 2.7 시험 JWT SSO (`getJwtToken`) — 아웃바운드
원본: `usr/exam/controller/ExamCourseController.java`, `service/impl/ExamCourseServiceImpl.java`
- ⚠️ **genius는 JWT를 만들지 않는다.** 코드베이스에 JWT 라이브러리 없음(`Jwts`/`signWith`/`io.jsonwebtoken` 0건). `getJwtToken`은 **외부 평가시스템에 신원을 위임하고 불투명 토큰을 받아 리다이렉트로 relay**하는 아웃바운드 오케스트레이터.
- **`GET|POST /exam/course/getJwtToken`** (param `schdId`, userId는 세션에서):
  1. 세션 `userid` 확인.
  2. `getRsaKey`: AES-GCM(`ktedu.exam.secret-key`)로 keySeq 암호화 → `POST {examDomain}/api/key/public` → RSA 공개키 수신.
  3. `RSAEncryptUtil.encode(yyyyMMddHHmmss+userId, publicKey)`.
  4. `POST {examDomain}/api/genius-login {keySeq, userId}` → **불투명 토큰 수신**(genius는 파싱 안 함).
  5. `redirect: {frontDomain}/sso-login?code=T&token=..&schdId=..` (localhost면 8082→3000 재작성).
- 설정: `ktedu.exam.secret-key`(체크인 값 빈값), `ktedu.exam.domain`(dev `http://localhost:8082`), `ktedu.exam.expiration-time`(주입되나 미사용).

---

## 3. portal 현재 이관 현황 (`ktlms`)

**인증 스택 파일**
- `auth/jwt/`: `JwtProvider`(HS256, access/refresh, 쿠키, SHA-256 해시), `dto/JwtDTO`(`userId,userNm,orgCd,comp,role,adminGrade`), `dto/RefreshTokenDTO`, `mapper/RefreshTokenMapper`(`tb_refresh_token`).
- `auth/ldap/`: `LdapService`(KT LDAP `loginperiod` HTTP API, AES256), `dto/Ldap*DTO`.
- `auth/login/`: `LoginController`(@Controller, JSP+AJAX), `AuthController`(@RestController `/auth`), `LoginService`(RSA복호화·멤버조회·LDAP·SMS·토큰발급), `mapper/LoginMapper`·`AdminAuthMapper`, `dto/LoginRequestDTO` 등.
- `auth/sso/`: `SamlController`(**SAML IdP 이관 완료**), `EntraSSOController`·`EntraOidcConfig`(**전체 주석 스텁**).
- `core/security/`: `SecurityConfig`(STATELESS, CSP, permitAll, CORS), `auth/JwtAuthenticationFilter`, `CustomLdapAuthenticationProvider`, `CustomUserDetails`, `SecurityUtil`.
- `core/filter/`: `CspNonceFilter`, `UrlSuffixRedirectFilter`.

**JWT 메커니즘**
- Access: HS256, claims `userId/userNm/orgCd/comp/role/adminGrade`, **TTL 30분**, 쿠키 `access_token`(HttpOnly, path `/`). `Authorization: Bearer`도 허용.
- Refresh: `jti`(UUID), 이중시계 idle 3h / absolute 24h(exp=min), 쿠키 `refresh_token`(HttpOnly, **path `/auth`**). 로테이션은 idle만 갱신. **재사용 탐지 시 사용자 전체 세션 삭제**(도난 대응). `tb_refresh_token`(`user_id,token_id,token(해시),expires_at`).
- 검증: `JwtAuthenticationFilter`(만료 시 401 `TOKEN_EXPIRED`, `/auth/**` 스킵).

**로그인 엔드포인트**
- `LoginController`: `/login`, `/login/getEncKeyAjax`, `/loginProcAjax`, `/loginProc`, `/sendSmsOptAuthAjax`, `/sendSmsAuthCheckAjax`, `/forceLoginAjax`, `/cdsLoginProcAjax`, `/examLoginProcAjax`, `/checkResult`, `/testDev`, `/changePw`, `/confirmPwdAjax` (+ `/mobile/m/*` 트윈). 다수 `@Deprecated` 정리 대상.
- `AuthController`(REST): `/auth/login`(LDAP 인증→bypass면 토큰, 아니면 `need2fa`), `/auth/login/step2`(OTP→토큰), `/auth/refresh`(로테이션+도난탐지), `/auth/logout`.
- SSO: `/sso/{channel}/login`, `/sso/{channel}/saml/{email}`, `/sso/{channel}/metadata`.

**⚠️ mock/미완 상태 (실연동 전 반드시 해소)**
- `CustomLdapAuthenticationProvider`: `security.dev-login.enabled` → user `test` 아무 비번 통과(`// TODO DB 연동 후 제거`).
- `LdapService.authenticate`: dev면 auto-pass, 하드코딩 테스트계정(`82047550~53`).
- `LoginService.fallbackMemberInfo()`: DB null이면 가짜 "테스트유저"(org 1001, KT, isadmin Y) — `.orElseGet(...)`로 광범위 사용.
- `MapperUtil.callOrDefault(...)`: 매퍼 예외 삼킴 → **DB 실패해도 fallback으로 로그인 진행**.
- `AuthController.refresh()`: `userNm=userId`, `role="ROLE_USER"` 하드코딩(`// TODO DB 조회`). `isBypassUser` 임시. `getUserIdFromEmail/getUserIdExists` → `return "Y"` 스텁.
- `SecurityConfig`: "pass-all" 인코더 주석 잔존.

---

## 4. Gap 분석 — 추가로 이관/구현해야 할 것

### ❌ 미이관 (신규 구현 필요)
1. **앱 로그인 API (D)** — 가장 큰 덩어리.
   - 필요: 앱 전용 컨트롤러(`/api/login/getRsaKey|loginProc|sendSmsOpt|checkSmsAuth|loginKeyProc`), AES 페이로드 계층, `loginKey`/기기등록(TB_USER_DEV) 로직, 앱 전용 매퍼(`usr.api.*`).
   - **JWT 전환 판단 필요**: 앱도 `loginKey`(기기 크리덴셜) 대신 **refresh token 방식**으로 갈지, 기존 loginKey를 유지할지. → 5장 참고.
   - 현재 portal엔 `auth/ldap/dto/Login*DTO`(VO→DTO 개명본)만 있고 실행 코드 없음.
2. **외부 제휴 SSO (F)** — Kyobo/BTS/Highway/mKate 컨트롤러 전무.
   - 필요: `TB_WEB_TOKEN` 검증 이관, 파트너별 AES(`StringEncrypter`) 이관, mKate 콜백→로그인 브릿지.
   - CodeGenius는 원본도 미구현이므로 **스킵 가능**(확인 필요).
3. **시험 JWT SSO (G)** — `getJwtToken` 아웃바운드 오케스트레이터 이관.
   - 외부 의존: `POST {examDomain}/api/key/public`, `POST {examDomain}/api/genius-login`, 최종 `redirect {frontDomain}/sso-login`.
   - **설계 변경 여지**: portal은 자체 JWT 인프라가 있으므로, 외부 평가시스템과 협의해 **portal이 자체 JWT를 발급→평가시스템이 검증**하는 방식으로 단순화 가능(현행은 평가시스템이 토큰 발급). 결정 필요.

### 🟡 부분 이관 (보완 필요)
4. **KATE SSO (C)** — `LoginService.isSsoLogin`(`issso`/`p_issso`/`mKate`, `loginPath=KATE`, 세션 `sso_id`) 플래그만 존재. genius의 `sso_id` 세션 기반 비번우회 핸드셰이크(어디서 `sso_id`가 세팅되는지)를 portal에서 어떻게 대체할지 미정.

### ✅ 이관됨 (검증만)
5. **표준/우회 로그인 (A/B)** — 골격 완료. mock/fallback(3장) 제거 + 실 DB/LDAP/SMS 연동 검증 필요.
6. **SAML IdP (E)** — 완료. genius에서 주석이던 `/sso/{channel}/login.do`(SP-initiated 왕복)를 portal `/sso/{channel}/login`이 JWT 검증 기반으로 살렸는지 동작 확인 필요.

### ⚪ 신규(genius 원본 없음)
7. **Entra/OIDC SSO** — portal 신규 예정, 현재 전체 주석 스텁. genius 이관 범위 아님.

---

## 5. JWT 전환으로 바뀌어야 하는 것 (세션 → Security/JWT)

genius의 인증 상태는 **HttpSession 멤버 백**에 있음. portal은 **STATELESS + JWT 쿠키**. 이로 인해 바뀌는 지점:

| genius (세션) | portal (JWT) 대응 | 상태/메모 |
|---|---|---|
| `SessionUtil.setSession(memberInfo)` (~50필드) | `JwtProvider.createAccessToken(JwtDTO)` — **축약 claim만**(`userId/userNm/orgCd/comp/role/adminGrade`) | ✅ 개인정보성 필드(email/resno/handphone) claim 금지. 나머지는 필요 시 DB 재조회 |
| 멤버 백 존재 = 로그인됨 | access_token 쿠키 + `JwtAuthenticationFilter` | ✅ 이관됨 |
| session `s_encSeq` (RSA 2요청 스테이트풀) | 현재 portal도 `getEncKeyAjax`에서 **session `s_encSeq` 사용** | ⚠️ STATELESS 지향과 상충. 단기 세션 유지 or 서버측 캐시(단명)로 대체 검토 |
| session `authUserId`/`successRedirectURL` (OTP 3요청) | `AuthController /auth/login`→`need2fa`→`/auth/login/step2` | ✅ 이관됨. 단 step 사이 상태 보관 방식(세션 vs 단명토큰) 점검 |
| 정적 `userSessionMap` + `TB_USER_SESSION` (단일세션) | `tb_refresh_token` + 재사용 탐지(전체 세션 삭제) | 🟡 **다중세션 허용 정책으로 변경**(중복 게이트 제거). 단일세션 강제 필요 시 재설계 |
| `forceLoginAjax`(타 세션 강제로그아웃) | refresh token 전체 삭제로 대체 가능 | 🟡 정책 결정 필요 |
| `appCheck=Y` (앱 세션 표식) | JWT claim `type`/별도 claim 또는 refresh 스코프 | ❌ 앱 이관 시 설계 |
| 앱 `loginKey` (영속 기기 크리덴셜, 비번 없는 자동로그인) | refresh token(장수명) or 기기바인딩 refresh | ❌ 앱 이관 시 핵심 결정 포인트 |
| SAML: 로그인 후 `checkSAMLSession`(세션 SAML 속성) | portal `SamlController`가 **JWT 유효성**으로 게이팅 | ✅ 이관됨(세션 SAML 속성 캐리오버는 login.jsp/`captureSamlInput` 유지) |
| 시험 SSO: 세션 `userid`만 사용 | JWT에서 userId 추출 | ❌ getJwtToken 이관 시 세션→`SecurityUtil.getCurrentUser()` 로 교체 |
| dev/test `isOk=1` 강제, LDAP auto-pass | mock/fallback(3장) | ⚠️ **운영 이관 전 전부 제거** |

**공통 원칙**
- genius `SessionUtil.getSession()` 호출 → portal `SecurityUtil.getCurrentUser()`(JWT claim) 로 치환.
- 세션에 있던 확장 프로필 필드가 필요한 화면은 **로그인 시 세션 저장이 아니라, 요청 시 DB 재조회**로 전환(claim은 최소화).
- 스테이트풀 핸드셰이크(RSA `s_encSeq`, OTP `authUserId`)는 STATELESS 원칙과 충돌 → **단명 서버 상태(짧은 세션/캐시/서명된 단명 토큰)** 중 택일 정책 필요.

---

## 6. 권장 이관 순서

1. **표준 로그인 실연동 검증** — 3장 mock/fallback 제거, 실 DB/LDAP/SMS 연동. (기반이므로 최우선)
2. **KATE SSO 보완(C)** — `sso_id` 세팅 출처 확인 후 JWT 기반으로 재설계.
3. **시험 JWT SSO(G)** — 외부 평가시스템과 토큰 방식 협의(현행 위임 vs portal 자체발급). 아웃바운드 오케스트레이터 이관.
4. **외부 제휴 SSO(F)** — Kyobo/BTS/Highway/mKate. `TB_WEB_TOKEN` + 파트너 AES 이관. (세션 미생성이라 JWT 영향 작음)
5. **앱 로그인 API(D)** — 가장 큼. `loginKey` vs refresh token 정책 확정 후 컨트롤러/서비스/매퍼/AES 계층 신규 구현.
6. **SAML SP-initiated 왕복 동작 확인(E)** — genius에서 주석이던 경로가 portal에서 완결되는지.

> ⚠️ 각 단계 착수 전 결정 필요한 정책: (a) 단일세션 강제 여부, (b) 앱 크리덴셜 방식(loginKey 유지 vs refresh), (c) 시험 SSO 토큰 발급 주체, (d) RSA/OTP 스테이트풀 상태 보관 방식.

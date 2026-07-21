# genius LoginController 이관 메모

## 이관 기준

- 원본: `genius_prd/src/com/anymobi/usr/login/**`
- 원본 JSP: `genius_prd/WebContent/WEB-INF/jsp/{web,mobile}/login/**`, `*/main/changePw.jsp`
- 원본 SQL: `genius_prd/src/conf/sql/usr/login/Login.xml`, `AdminAuth.xml`
- portal 인증 방식: Spring Security + JWT(access/refresh httpOnly cookie)

## 적용 내용

- 활성 로그인 화면은 `WEB-INF/views/login/login.jsp`를 사용한다.
- legacy URL(`/login.do`, `/mobile/m/login.do`, `/mobile/t/login.do`)은 JWT 로그인 화면으로 연결한다.
- legacy logout URL(`/logout.do`, `/mobile/m/logout.do`, `/mobile/t/logout.do`)은 `/auth/logout` POST redirect 화면으로 연결한다.
- genius iBatis SQL은 MyBatis mapper로 변환했다.
  - `mapper/auth/ldap/LoginMapper.xml`
  - `mapper/auth/ldap/AdminAuthMapper.xml`
- Java는 `com.kt.ktedu.auth.ldap` 하위 DTO/Mapper와 기존 `LoginService`로 병합했다.
- 원본 JSP는 UTF-8로 변환해 `WEB-INF/views/legacy/genius/**`에 보존했다.

## JWT 전환 메모

- genius 세션 `userid/name/comp/org_cd/isadmin/gubun` 계열만 JWT claim(`JwtDTO`)으로 축약한다.
- 개인정보성 필드(`email`, `handphone`, `resno` 등)는 JWT에 싣지 않는다.
- DB 연결이 없는 개발 단계에서는 `auth.ldap.service.LoginService`가 기존 portal 테스트 claim으로 fallback한다.
- SMS 2차 인증은 `POST /auth/login/step2`에서 `auth.ldap.mapper.AdminAuthMapper`를 통해 검증하도록 연결했다. DB/SMS가 없으면 실패하며, 관리자 또는 `SMS_BYPASS=Y` 사용자는 1단계에서 바로 JWT를 발급한다.

- 2026-07-13: `Genius/Legacy` 접두 파일명 제거, `auth.ldap` 하위로 재배치하고 기존 `LoginService`에 병합했다.

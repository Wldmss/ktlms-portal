# Genius → Portal 이관 규칙

1. Java는 genius의 원본 디렉터리 구조와 파일명을 유지해 `src/main/java/com/kt/ktedu/legacy` 아래로 이관한다. `package`는 `com.kt.ktedu.legacy...` 기준으로 변경한다.
2. JSP는 원본 구조와 파일명을 유지해 `src/main/webapp/WEB-INF/views/legacy` 아래로 이관한다.
   - `WEB-INF/jsp/web/...` → `WEB-INF/views/legacy/web/...`
   - `WEB-INF/jsp/mobile/mobile/m/...` → `WEB-INF/views/legacy/mobile/mobile/m/...`
3. XML은 원본 구조와 파일명을 유지해 `src/main/resources/mapper/legacy` 아래로 이관한다.
4. 파일명은 변경하지 않는다. 새 파일 생성이 필요한 경우에만 portal 명명 규칙을 적용한다.
5. 원본 주석, TODO, 라이선스 헤더, 코드 순서는 유지한다.
6. JDK21/Spring6/Jakarta/JSTL/MyBatis 전환에 필요한 최소 수정만 한다.
   - `javax.*` → `jakarta.*`
   - iBatis XML 문법 → MyBatis XML 문법
   - EUC-KR → UTF-8
   - Tiles 경로 → portal include 레이아웃
   - Session/CommandMap 사용자 정보 → JWT/SecurityContext
   - Interceptor 인증/인가 → Spring Security 및 `@PreAuthorize`
7. 기능 개선, 리팩터링, 임의의 클래스·메소드·파일명 변경은 이관 작업과 분리한다.
8. JSP include는 원본 상대 구조를 유지하되 portal의 `WEB-INF/views/legacy` 경로로 변경한다. 1차 이관에서는 Tiles 페이지를 portal의 기존 `common/tiles` 하위 include(`tiles-top`/`tiles-bottom`, `m-top`/`m-bottom` 등)로 변환한다. genius header/footer/GNB도 먼저 `legacy` 하위에 보존하고, 검증이 끝난 뒤에만 `common/layout`으로 흡수한다. 최종 단계에서 Tiles를 제거할 때 GNB가 있으면 `common-top`/`common-bottom`, 없으면 `default-top`/`default-bottom`으로 승격한다.
9. `.do` 접미사는 이관 시 모두 제거한다. Controller 매핑, JSP 링크/action, JavaScript URL, redirect URL을 portal clean URL 기준으로 변경한다.
10. `WEB-INF/classes`, 빌드 산출물, `class/jar/war`, IDE 설정, 백업·임시 파일, 미사용 테스트 파일은 이관하지 않는다.
11. 원본 파일과 portal 대상 파일의 경로 매칭은 `migration/guide/migration-list.md`에 폴더 단위로 기록한다. 예외·미이관·대체 파일은 별도로 표시한다.
12. 이관 전후 동작 차이는 최소화하고, 변경이 필요한 경우 원본 코드 주석 옆에 portal 전환 이유를 기록한다.
13. genius `index.jsp`의 `isMobile()` 및 동일한 모바일 판별 함수는 새로 이관하지 않는다. portal 공통 `meta.jsp`가 제공하는 `window._isMobile`을 사용한다.
14. 로그인 화면·로그인 전용 Java/XML은 portal 로그인 구현을 사용하므로 genius에서 이관하지 않는다. 로그인과 일반 업무 기능이 같은 파일에 섞인 경우에만 필요한 업무 부분을 분리 검토한다.
15. CSS/JS/이미지는 이미 이관된 `src/main/webapp/resources/legacy` 아래 원본 계열을 사용한다. JSP에서는 `${pageContext.request.contextPath}/resources/legacy/...` URL로 매핑하고, 파일을 `WEB-INF/views/legacy` 아래에 중복 복사하지 않는다. `common/core/resources.jsp` 및 `common/core/script.jsp`에는 portal 전역 리소스만 두며, 메뉴 전용 리소스는 해당 `resources/legacy` 경로에서 페이지별로 include한다.

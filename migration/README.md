# 이관 요약집 (전체 그림 한 장)

> 이 문서 하나로 **무엇을 / 어떤 순서로 / 무엇을 주의하며** 이관하는지 파악하는 것이 목표다.
> 상세는 각 항목의 링크(가이드)를 따라간다. 이 문서는 항상 **키워드 + 요약**만 유지한다.

---

## 0. 큰 그림

- `portal` = JDK21 · Spring 6 · Spring Security · JWT · JBoss 기준 **표준 베이스**.
- `genius`(사용자) / `lms`(어드민) = 레거시. → 각각 `portal` 복제본에 **메뉴 단위**로 이관 → `new-genius` / `new-admin`.
- 원칙: **Java/JSP 최소 수정**, 구기술은 대체·제거(브릿지+`@Deprecated`), **미사용은 이관 안 함**.
- 진행 흐름: **① portal 공통 확정 → ② 필수 가이드 완비 → ③ portal 복제(new-genius/new-admin) → ④ 파일럿 메뉴 1개 → ⑤ 메뉴별 대량 이관**.

전체 세부 체크리스트: [migration-checklist.md](migration-checklist.md) · 작업 순서 메모: [todo.md](todo.md)

---

## 1. 현재 상태 (portal 공통 베이스라인)

| 영역 | 상태 | 비고 |
|---|---|---|
| env/properties 일원화 (`application.properties` + `env/{profile}.env`) | ✅ | 유니코드 제거, 사용 key만 |
| conf xml 정리 (`root-context`, `servlet-context`만) | ✅ | 나머지는 properties로 이동 |
| filter → Spring Security 이관 | ✅ | 레거시 filter 전체 삭제 |
| JWT 세팅 (발급/검증/만료/refresh) | ✅ | access 30분 / idle 3h / absolute 24h |
| Exception/Response 공통화 | ✅ | 코드 수정 가이드 필요 |
| 공통 Util (문자열/날짜/파일/페이징/XSS/crypto/Excel) | ✅ | 가이드 별도 |
| 공통 JS (ajax/dialog/form/file/check/formatter/datepicker/table) | ✅ | |
| Direct JSP → `LinkController` + 접미사 필터 | ✅ | pageLink/shareLink. nsso_*는 보류 |
| **Interceptor → `@PreAuthorize`** | ⏳ | 코드 이관 후 진행 |
| **Session(CommandMap) → JWT 실적용** | ⏳ | 메뉴 이관 시 |
| **Tiles 제거 / iBatis→MyBatis / Jakarta·java.time 일괄** | ⏳ | 분리 후 |

---

## 2. 프로젝트 분리 후 진행 사항 (post-split)

분리(복제) 시·후에 해야 하는 것들. 상세: [migration-checklist.md](migration-checklist.md) §4.

- **복제/리네이밍**: 프로젝트명·artifactId·context path·app name·log path·JBoss 배포 파일명·profile sample 각각 변경 → 빈 상태 build/배포/로그인 동작 확인.
- **일괄 변환** (외주 포함): `javax.*`→`jakarta.*`, `joda-time`→`java.time`, encoding UTF-8 통일. → [edit_list.md](edit_list.md), [애저전환.md](<애저전환.md>)
- **Tiles 제거**: 레이아웃 통합 후 명시적 include로 전환. → [tiles-removal-guide.md](tiles-removal-guide.md)
- **iBatis → MyBatis**: 실제 사용 mapper만, `$` injection 점검. (가이드 미작성 ⏳)
- **미사용 정리**: JSP/JS/CSS/image/xml/property/library 중 미사용 이관 안 함.
- **라이브러리 코드 수정** (외주): `lucy-xss`, `POI`. → [edit_list.md](edit_list.md)
- **인프라/배포**: Azure Key Vault secret 통파일 방식, ArgoCD 멀티 배포. → [server_todo.md](server_todo.md), [애저전환.md](<애저전환.md>)
- **파일럿**: genius/lms 대표 메뉴 각 1개를 끝까지 이관해 가이드·체크리스트 보정 후 대량 분업 시작.

---

## 3. 코드 이관 시 주의 항목 (주제별)

메뉴 이관 시 만나는 레거시 패턴 → portal 표준 대체. 큰 키워드 중심.

| 주제 | 요약 | 가이드 |
|---|---|---|
| **Jakarta/JDK21** | `javax→jakarta`, JSTL URI 변경, POI, 파일업로드 체질 개선, joda→java.time | [edit_list.md](edit_list.md), [애저전환.md](<애저전환.md>) |
| **Tiles 제거** | definition → `common-top/bottom`(GNB) · `default-top/bottom`(GNB無) · popup 전용. mobile/nested 주의 | [tiles-removal-guide.md](tiles-removal-guide.md) |
| **Session→JWT** | `CommandMap`/session 직접접근 → JWT claim 또는 매요청 조회. JSP는 `${loginUser}`, AJAX는 공통 헤더 | [session-to-jwt-guide.md](session-to-jwt-guide.md) |
| **Interceptor→Security** | 인증/인가 인터셉터 삭제, URL권한=`SecurityConfig`·업무권한=`@PreAuthorize`. 로깅/접속통계는 미이관 | [interceptor-to-security-guide.md](interceptor-to-security-guide.md) |
| **Redirect** | `RedirectUtil` 브릿지 유지(`@Deprecated`). historyBack/redirect 다수 | [redirectutil-migration-guide.md](redirectutil-migration-guide.md) |
| **Excel** | `JxlRead/JxlWrite` → 공통 `ExcelUtil`(POI). JXL 제거 | [jxl-to-excelutil-guide.md](jxl-to-excelutil-guide.md) |
| **외부연동** | HTTP는 Spring `RestClient` 표준(util 제거). SFTP/FTP는 필요 시 | [network-util-guide.md](network-util-guide.md) |
| **Direct JSP** | `LinkController`(pageLink/shareLink). `.do`/`.jsp` 접미사는 `UrlSuffixRedirectFilter`가 흡수 | (본문 §2.5 [migration-checklist.md](migration-checklist.md)) |
| **iBatis→MyBatis** | 실사용 mapper만, `#`/`$` 차이·injection, resultMap/paging 전환 | (가이드 ⏳) |
| **공통 Util** | 문자열/날짜/숫자/파일/페이징/XSS/crypto — 아래 §4 우선순위 | [todo.md](todo.md) |
| **분석(대상 선별)** | genius/lms별 "꼭 이관/삭제/애매" 분류 | [genius-migration-analysis.md](genius-migration-analysis.md), [lms-migration-analysis.md](lms-migration-analysis.md) |

---

## 4. 공통 Util/JS 정리 우선순위

상세·대상 클래스 목록: [todo.md](todo.md) 하단.

1. **1순위 (portal 먼저 확정)**: 문자열/날짜/숫자/Map, Formatter → CurrentUser/Response/Redirect → File/Excel/Pagination → 이관용 브릿지(`LegacyParamMap` 등) 최소 제공.
2. **JS 표준**: `common-ajax` / `common-dialog` / `common-form·file·check` / `formatter` / `common-datepicker` / `common-table`(admin 전).
3. **2순위 (메뉴 이관 시 추가)**: `CodeService`, 메일/SMS, DRM/Softcamp/PDF/Image converter.

**공통으로 만들지 말 것**: SQL escape util(`makeSQL`류) → MyBatis 바인딩/화이트리스트로. JS `prototype` 확장. `RequestBox/DataBox` 영구 공통화. 도메인 팝업 함수의 common 편입.

---

## 5. 가이드 인덱스 (전체)

| 파일 | 내용 |
|---|---|
| [migration-checklist.md](migration-checklist.md) | **마스터 체크리스트** (전제~최종검증, 메뉴 작업 양식) |
| [genius-schedule.md](genius-schedule.md) | **genius 이관 스케줄** (Phase 0~5 + DB 전략) |
| [ibatis-to-mybatis-guide.md](ibatis-to-mybatis-guide.md) | iBatis 2 → MyBatis 3 전환 규약 |
| [todo.md](todo.md) | 작업 순서 메모 + Util/JS 정리 우선순위 |
| [genius-migration-analysis.md](genius-migration-analysis.md) | Genius 이관 대상 1차 분석 |
| [lms-migration-analysis.md](lms-migration-analysis.md) | LMS 이관 대상 1차 분석 |
| [session-to-jwt-guide.md](session-to-jwt-guide.md) | Session(CommandMap) → JWT |
| [interceptor-to-security-guide.md](interceptor-to-security-guide.md) | Interceptor → Security/`@PreAuthorize` |
| [tiles-removal-guide.md](tiles-removal-guide.md) | Tiles 제거 |
| [redirectutil-migration-guide.md](redirectutil-migration-guide.md) | RedirectUtil 이관 |
| [jxl-to-excelutil-guide.md](jxl-to-excelutil-guide.md) | JXL → ExcelUtil(POI) |
| [network-util-guide.md](network-util-guide.md) | 외부연동 (HTTP/SFTP/FTP) |
| [edit_list.md](edit_list.md) | 라이브러리 변경 코드 수정 리스트(Jakarta/JSTL/POI/joda) |
| [애저전환.md](<애저전환.md>) | Azure 전환 · Jakarta/java.time/Tiles/Maven |
| [server_todo.md](server_todo.md) | Azure Key Vault secret · ArgoCD 배포 |
| [프로젝트 구조.md](<프로젝트 구조.md>) | 프로젝트 구조 |
| [genius-css.md](genius-css.md) | genius css/js 메모 |

> 가이드 미작성 ⏳: **iBatis→MyBatis**, **공통 이관 원칙(메뉴 단위/최소수정/네이밍)**.

# Genius → new-genius 이관 스케줄

> 원칙: **공통 고정 → 메뉴 단위 선별 이관**, 최소 수정, 미사용 파일 스킵.
> 분석: [genius-migration-analysis.md](genius-migration-analysis.md) · 전체 그림: [README.md](README.md)

---

## DB 전략 (연결 전)

- **로컬 PostgreSQL 사용** (`local.env` = `localhost:5432/postgres`). 운영 DB 기다리지 않는다.
- **테이블은 메뉴가 쓰는 것만 점진 생성** (= 임시 테이블). 위치: `src/main/resources/db/genius/schema/*.sql`, 샘플데이터 `.../seed/*.sql`.
- Phase 1~3 은 DB 거의 불필요. Phase 4~ 부터 메뉴별로 DDL+seed 누적.
- 레거시가 **Oracle 방언 → Postgres 변환** 필요 (MyBatis 전환과 함께). 그래서 실 DB로 검증하는 편이 안전.
- 순수 화면 확인만 필요하면 Service 스텁(고정 데이터)도 허용.

---

## Phase 0 — 셋업 / 그린 빌드

- [ ] new-genius 식별자 정리: artifactId / context-path / war 이름 / log path (현재 war명 `ktlms-portal` 유지할지 결정)
- [x] EnvFile(common/local.env) 실행구성 연결 — JBoss 기동 OK
- [x] 로컬 PostgreSQL 기동 + 접속 확인 — 이미 떠 있음(docker `local-db`, 5432, postgres/1234). tb_refresh_token/tb_rsa_key 존재
- [x] **iBatis → MyBatis 규약 확정** — [ibatis-to-mybatis-guide.md](ibatis-to-mybatis-guide.md). DB 스캐폴딩: `ktlms/src/main/resources/db/genius/`
- [ ] 빈 상태 배포 그린 + 로그인 화면 렌더 확인 (EnvFile 붙이고 재배포 결과 대기)

## Phase 1 — 공통 프론트 (css / js)  *(DB 불필요)*

- [ ] `WebContent/newPortal/**` 중 실제 참조되는 css/js 선별 → portal `resources/` 규칙으로 이관
- [ ] portal 공통 js(common-ajax/dialog/form/file/check/formatter/datepicker/table)와 매핑·중복 제거
- [ ] 레거시 전역 함수(KtLms 등) 충돌 점검
- [ ] 정적 리소스 배치 규칙 확정

## Phase 2 — Tiles 제거 + Layout  *(DB 불필요, GNB 메뉴는 임시 정적/스텁)*

- 가이드: [tiles-removal-guide.md](tiles-removal-guide.md)
- [ ] `tiles-define.xml` + `web/tiles/**`(28개) 분석 → 레이아웃 통합
- [ ] genius GNB / footer → portal `common/layout` 재구성 (common-top/bottom, default-top/bottom, popup, mobile)
- [ ] 샘플 페이지 1개를 include 구조로 전환 검증
- [ ] 모바일 레이아웃 유지 (모바일 사이트 유지 방침)

## Phase 3 — 로그인 재작성

- 가이드: [session-to-jwt-guide.md](session-to-jwt-guide.md), [interceptor-to-security-guide.md](interceptor-to-security-guide.md)
- [ ] genius 로그인 / LDAP / SSO 흐름 분석
- [ ] portal JWT + Spring Security 기준 로그인 재작성 (dev-login 우회 유지)
- [ ] session key → JWT claim 매핑: `userid`, `name`, `gadmin`/`gadmin_open`, `authUserId` 등
- [ ] **첫 실테이블**: user/profile 조회 → 로컬 Postgres 테이블 DDL+seed
- [ ] 로그인 후 GNB / 권한 노출까지 동작 검증

## Phase 4 — 파일럿 메뉴 1개 (`support/notice` 또는 `support/qna`)

- [ ] 전체 이관 10단계 실검증 (URL→Controller→Service→Mapper(MyBatis)→JSP→JS/CSS→권한→테이블→검증)
- [ ] 해당 메뉴 테이블 DDL+seed 로컬 Postgres 반영
- [ ] iBatis→MyBatis / Oracle→Postgres 변환 패턴 정리 → 가이드 반영
- [ ] 나온 예외를 [migration-checklist.md](migration-checklist.md) 메뉴 체크리스트에 반영
- ⚠️ 파일럿 완료 전 대량 분업 금지

## Phase 5 — 메뉴별 반복 이관

- 순서 후보: `main` → `support` 나머지 → `education`(60 JSP) → `myclass`(37) → `course` → `learncom` → 그 외
- [ ] 메뉴마다: 인벤토리 → 코드 이관(최소수정) → 테이블 생성 → 검증
- [ ] 참조 안 되는 JSP/JS/CSS/image/property/library 는 스킵

## 보류 / 별도 트랙 (이번 new-genius 범위에서 우선 제외)

- `adm/**`, batch, SCORM, iCampus, DWR, WebSquare, SSO agent JSP, SAP/RFC → 유지 여부 결정 후 new-admin/별도 app/별도 파일럿
- 외주 일괄변환(`javax→jakarta`, `joda→java.time`, encoding): 코드가 어느정도 옮겨진 뒤 또는 옮기면서 → [edit_list.md](edit_list.md)

---

## 의존 관계 (막히면 안 되는 것)

- Phase 4 전에 **로컬 Postgres** + **MyBatis 규약** 필요
- Phase 3 로그인은 Security/JWT(완) + user 테이블 필요
- Phase 2 layout은 Phase 1 공통 리소스 위에서

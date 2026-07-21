# iBatis(2) → MyBatis(3) 전환 가이드

> 레거시 genius/lms 의 iBatis 2 SqlMap → portal MyBatis 3 규약으로 옮기는 기준.
> 메뉴 단위로 **실제 사용하는 query만** 옮긴다. 전체 파일 복사 금지.
> 관련: [session-to-jwt-guide.md](session-to-jwt-guide.md), [genius-schedule.md](genius-schedule.md)

---

## 0. portal MyBatis 구조 (이미 구성됨)

`conf/spring/root-context.xml` (구 `KT-mybatis.xml` 은 통합·삭제됨):

- **DataSource**: HikariCP, `db.*` (env). 로컬은 이미 떠 있는 Postgres(`local-db`, 5432).
- **mapperLocations**: `classpath:/mapper/**/*.xml`
- **MapperScannerConfigurer basePackage**: `com.kt.ktedu` (인터페이스에 `@Mapper`)
    - ⚠️ basePackage 가 넓어 서비스 **인터페이스**까지 매퍼로 스캔되어 빈 이름 충돌이 날 수 있음
      (예: `LoginService` ↔ `@Service("loginService")`). `annotationClass=org.apache.ibatis.annotations.Mapper`
      (= MyBatis 의 `@Mapper`, 레거시 iBATIS 아님) 를 추가해 `@Mapper` 인터페이스만 스캔하도록 제한 권장.
- **mapUnderscoreToCamelCase = true** → 컬럼 `user_id` ↔ 필드 `userId` 자동 매핑

### 규약 (기존 `RefreshTokenMapper` 기준)

| 항목           | 규약                                                                        |
|--------------|---------------------------------------------------------------------------|
| Mapper 인터페이스 | `com.kt.ktedu.<도메인>.mapper.XxxMapper` + `@Mapper`                         |
| Mapper XML   | `src/main/resources/mapper/<도메인>/XxxMapper.xml`                           |
| namespace    | 인터페이스 **FQCN** (예: `com.kt.ktedu.usr.support.notice.mapper.NoticeMapper`) |
| statement id | 인터페이스 **메서드명**과 1:1                                                       |
| 파라미터         | `#{userId}` (여러 개면 `@Param` 또는 DTO)                                       |
| 컬럼/필드        | DB snake_case ↔ Java camelCase (자동)                                       |
| 테이블          | 신규 공통 테이블은 `tb_` prefix (레거시 테이블명은 유지)                                    |

---

## 1. 문법 매핑표 (iBatis 2 → MyBatis 3)

| iBatis 2                                  | MyBatis 3                                  | 비고                                            |
|-------------------------------------------|--------------------------------------------|-----------------------------------------------|
| `<sqlMap namespace="usr.notice">`         | `<mapper namespace="...FQCN">`             | DOCTYPE 도 mybatis-3-mapper.dtd 로              |
| `resultClass="HashMap"`                   | `resultType="map"`                         | 최소수정: map 유지 가능 (권장은 DTO)                     |
| `resultClass="String"`                    | `resultType="string"`                      |                                               |
| `resultClass="com.x.FooVO"`               | `resultType="com.x.FooVO"`                 |                                               |
| `parameterClass="..."`                    | `parameterType="..."`                      | 생략 가능                                         |
| `#param#`                                 | `#{param}`                                 | **바인딩(PreparedStatement) — 기본**               |
| `$param$`                                 | `${param}`                                 | **문자열 치환 — SQL injection 위험. 정렬컬럼 등 화이트리스트만** |
| `<isNotEmpty property="x">`               | `<if test="x != null and x != ''">`        |                                               |
| `<isEqual property="x" compareValue="Y">` | `<if test="x == 'Y'">`                     |                                               |
| `<isNull>` / `<isNotNull>`                | `<if test="x == null">` / `!= null`        |                                               |
| `<iterate property="list" ...>`           | `<foreach collection="list" item="i" ...>` |                                               |
| `<dynamic prepend="WHERE">`               | `<where> ... </where>`                     | 앞 AND/OR 자동 제거                                |
| `<![CDATA[ ... ]]>`                       | 그대로 사용 가능                                  | `<`, `>` 는 `&lt; &gt;` 로도                     |

---

## 2. DAO 호출 전환

레거시 (iBatis + SqlMapClientDaoSupport):

```java
// namespace.id 문자열로 호출, 파라미터는 Map/VO
List<?> list = getSqlMapClientTemplate().queryForList("usr.notice.getNoticeList", map);
Map<?, ?> row = (Map<?, ?>) getSqlMapClientTemplate().queryForObject("usr.notice.getNotice", id);

getSqlMapClientTemplate().

insert("usr.notice.insertNotice",vo);

getSqlMapClientTemplate().

update("usr.notice.updateNotice",vo);

getSqlMapClientTemplate().

delete("usr.notice.deleteNotice",id);
```

portal (Mapper 인터페이스 주입):

```java
private final NoticeMapper noticeMapper;   // @RequiredArgsConstructor

List<NoticeDTO> list = noticeMapper.findList(cond);
NoticeDTO row = noticeMapper.findOne(id);
noticeMapper.

insert(dto);   // insert/update/delete 는 int(영향행수) 반환
noticeMapper.

update(dto);
noticeMapper.

delete(id);
```

- `queryForList` → `List<T> findXxx(...)`
- `queryForObject` → `T findXxx(...)` (단건)
- `insert/update/delete` → `int` 반환 메서드
- 공통 DAO(`SqlMapClientDaoSupport`) 상속 구조는 버리고 **Mapper 주입**으로. (드물게 동적 statement가 필요하면 `SqlSessionTemplate` 직접 주입)

---

## 2-B. 최소 수정 이관 — `CommonDAO` shim (대안 경로)

genius 는 거의 모든 쿼리를 **공통 DAO** 를 통해 호출한다:
`commonDAO.getList/getView/getPageList/insert/update(Map, "namespace.queryId")`.
§2 처럼 전부 타입 Mapper 로 바꾸면 **호출부(서비스 코드)까지 대량 수정**된다.
호출부를 보존해 **빠르게 이관**하려면, 동일 시그니처의 shim 을 MyBatis 위에 둔다.

- **구현**: [`com.kt.ktedu.common.common.dao.CommonDAO`](../src/main/java/com/kt/ktedu/common/common/dao/CommonDAO.java)
  — `@Repository("commonDAO")`, `SqlSessionTemplate` 래핑.
- **제공 메서드**(genius 전체 실사용분만): `getList` / `getView` / `getPageList` / `insert` / `update` / `delete`.
  `*2`(2nd datasource) · batch · solr · excel 변형은 해당 기능 이관 시 추가.
- **호출부**: `import` 만 portal 패키지로 교체하면 `commonDAO.getList(map, "campaign.selectList")` 는 **그대로**.
- **statement id**: genius 문자열(`"namespace.queryId"`) 유지 → mapper XML 의 `namespace`/`id` 를 **원본 그대로** 두고
  §1 문법만 변환한다. (shim 경로에선 §0 의 "namespace=FQCN" 규약을 강제하지 않음)
- **결과 키**: genius `SharedMethods.getFieldName` 과 동일하게 camelCase 자동 정규화
  (단, MyBatis map-camelCase 와 이중변환 방지를 위해 **언더스코어 있을 때만** 변환).
  `getPageList` 반환 맵 키는 genius 와 동일한 `count`(건수) / `output`(리스트).

```java
// 이관 서비스 코드 — 호출부 무수정 (import 만 교체)
Map<String, Object> p = new HashMap<>();
p.

put("compId",compId);

List<Map<String, Object>> list = commonDAO.getList(p, "campaign.selectCampaignList");
Map<String, Object> page = commonDAO.getPageList(p, "campaign.selectCount", "campaign.selectList");
Object one = commonDAO.getView(p, "campaign.selectCampaign");
commonDAO.

insert(p, "campaign.insertCampaign");
commonDAO.

update(p, "campaign.updateCampaign");
```

**주의점**

- **insert 반환형**: iBATIS 는 생성키(Object), MyBatis 는 영향 행수(int). 생성키는 mapper `<selectKey>` 가
  파라미터 맵에 채우므로 호출 후 `input.get("키")` 로 접근. (insert 반환값 자체를 키로 쓰던 코드만 점검)
- **`#s_xxx#` 세션 파라미터**(§3)는 shim/@Mapper 어느 쪽이든 동일하게 명시 전달 필요.
- **공존**: shim 과 §2 의 `@Mapper` 는 **같은 `SqlSessionFactory`** 를 공유하므로 혼용 가능.
  대량 화면을 shim 으로 빠르게 올린 뒤, 여유가 되면 화면별로 §2 타입 Mapper 로 점진 리팩터링.

**언제 무엇을**
| 상황 | 방식 |
|---|---|
| 신규 개발 / 리팩터링 여유 | §2 타입 `@Mapper` (권장 최종형) |
| 대량 레거시 화면 빠른 이관, 호출부 보존 | §2-B `CommonDAO` shim |

---

## 2-C. 예외 방어 헬퍼 `MapperUtil` (이관 과도기용)

이관 중에는 참조 테이블/매퍼가 아직 없어 매퍼 호출이 예외를 던질 수 있다.
로그인 등 흐름이 죽지 않게 **임시로** 감싸는 헬퍼.

- **구현**: [`com.kt.ktedu.common.util.core.MapperUtil`](../src/main/java/com/kt/ktedu/common/util/core/MapperUtil.java)
- `callOrDefault(Supplier<T>, T fallback)` — 예외 시 `fallback` 반환 + `debug` 로그.
  ```java
  Integer isMember = callOrDefault(() -> loginMapper.getIsMember(params), 0);
  ```
- ⚠️ **예외를 조용히 삼키므로 과도기용.** 전역 남용 금지(진짜 DB 오류도 숨겨짐).
  정상 코드는 매퍼 직접 호출(예외 전파)이 기본.

**이관 완료 후 제거** — 각 호출부의 fallback 의도를 구분해 걷어낸다:

| fallback 의도 | 전환 |
|---|---|
| 정상 기본값(조회 결과 없음 → 기본값) | `Optional.ofNullable(m.x(p)).orElse(기본값)` — 예외는 전파, "없으면 기본값"만 유지 |
| 에러 숨김용 | 래핑 제거 → `m.x(p)` 직접 호출 (`GlobalExceptionHandler` + `@Transactional` 롤백으로 처리) |

판단 기준: *"이 fallback 이 정상 상황의 기본값인가, 에러를 숨기려던 것인가?"*
모든 `callOrDefault` 참조가 사라지면 `MapperUtil` 을 삭제한다.

---

## 3. 세션 파라미터 `#s_xxx#` 처리 (중요)

레거시 쿼리에 `#s_userid#`, `#s_comp#` 같은 **세션 주입 파라미터**가 많다.
portal 은 CommandMap 세션 주입을 제거했으므로 **자동으로 안 들어온다.**

```sql
-- 레거시
WHERE T.USERID =
#s_userid#
```

→ Controller/Service 에서 현재 사용자 값을 꺼내 **명시적으로 파라미터에 담아** 넘긴다:

```java
String userId = SecurityUtil.getCurrentUserId();   // JWT 기반
cond.

setUserId(userId);
noticeMapper.

findList(cond);
```

```sql
-- 전환
WHERE T.USERID =
#{userId}
```

매핑 기준: [session-to-jwt-guide.md](session-to-jwt-guide.md) 의 session key → claim 표.

---

## 4. SQL 방언 / 타입 점검

- 레거시 SQL 에 stored function(`FN_...`), `WITH RECURSIVE`, 시퀀스, `SYSDATE`/`NOW()`, `NVL`/`COALESCE`, `ROWNUM`/`LIMIT` 등이
  섞여 있으면 **Postgres 기준으로 점검·변환**한다.
- `resultType="map"` 는 컬럼명이 **대문자 key** 로 들어올 수 있으니(드라이버/쿼리 alias 기준) JSP/코드에서 참조하는 key 대소문자 확인. 되도록 DTO + camelCase 로.
- 날짜/숫자 타입은 mapUnderscoreToCamelCase 로 필드 매핑되며, 포맷은 formatter(JS)·ExcelUtil 등 공통에서 처리.

---

## 5. 로컬 DB + 임시 테이블 워크플로우

DB 는 이미 로컬 컨테이너(`local-db`)로 떠 있다. 메뉴가 쓰는 테이블만 만든다.

- DDL: `src/main/resources/db/genius/schema/<도메인>.sql`
- 샘플데이터: `src/main/resources/db/genius/seed/<도메인>.sql`
- 적용(psql 미설치 → 컨테이너로 실행):

```bash
docker exec -e PGPASSWORD=1234 -i local-db psql -U postgres -d postgres < src/main/resources/db/genius/schema/notice.sql
```

- 테이블 구조는 레거시 mapper 의 컬럼 + 운영 스키마를 참고해 **필요한 컬럼만**. 나중에 실제 스키마로 교체.

---

## 6. 메뉴별 체크리스트

- [ ] 이 메뉴가 실제 호출하는 statement id만 추린다 (미사용 query 스킵)
- [ ] 전환 방식 선택: 타입 `@Mapper`(§2) vs `CommonDAO` shim(§2-B)
- [ ] (@Mapper) Mapper 인터페이스 생성 (`@Mapper`, 메서드=statement id) / (shim) 호출부 import 만 교체
- [ ] XML 을 `mapper/<도메인>/` 로 — namespace: @Mapper=FQCN / shim=원본 문자열 유지
- [ ] `#x#`→`#{x}`, `$x$`→`${x}`(위험 점검), isNotEmpty/iterate → if/foreach
- [ ] `#s_xxx#` → SecurityUtil 값 파라미터로 전달
- [ ] resultClass → resultType/resultMap
- [ ] DAO 호출부를 Mapper 주입으로 교체
- [ ] 테이블 DDL+seed 로컬 반영
- [ ] Postgres 방언 점검 후 쿼리 실행 검증

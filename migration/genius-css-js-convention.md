# genius CSS/JS 이관 규칙

> portal에 이미 만든 공통 구조를 기준으로, genius 리소스를 **그대로 옮기지 않고 정리**해서 이관한다.
> 불필요한 건 걷어내고, 공통화 가능한 건 공통으로 올린다.

## CSS 구조 (`webapp/resources/css/`)

| 위치 | 역할 | 로드 |
|---|---|---|
| `common/base` `common-ui` `common` | portal 공통 베이스/UI | 항상 |
| `common/common-utility` | new23 / new23_m 의 **공통 추출분** (메인) | 항상 |
| `web/web.css` , `web/web-gnb` | PC 전용 | `deviceType=WEB` |
| `mobile/mobile.css` , `mobile/mobile-gnb` | 모바일 전용 | `deviceType=MOBILE` |
| `common/components/*` | button/input/form-control/datepicker/**popup** | `common-ui`가 @import(전역) |
| `pages/<메뉴>/<메뉴>.css` | **메뉴 고유 규칙만** | 해당 페이지 |

- 메인(common-utility + web/mobile 분기)은 `meta.jsp`에서 `${deviceType}`로 로드 (완료).
- 컴포넌트는 `components/<x>.css`(소스) + `<x>.min.css`(쌍), `common-ui.css`가 `@import`, 번들 `common-ui.min.css`에 concat. meta.jsp는 `common-ui.min.css` 로드.
- 메뉴 이관 시: 레거시 메뉴 CSS에서 **공통이면 common/web/mobile로 승격, 고유면 `pages/<메뉴>/`**, 안 쓰면 버린다.

### genius 전역 CSS 흡수 현황 (defaultCssLayout 기준)

| 원본 | 처리 |
|---|---|
| `new23.css` / `new23_m.css` | → `common-utility` + `web`/`mobile` (완, 슬림 추출) |
| `w-pop.css` (팝업) | → `components/popup.css` **흡수 완료** (EUC-KR→UTF-8, 경로 정규화) |
| `new22.css` | **폐기** (4곳만 참조, new23가 대체) |
| `common.css`(236K, 67페이지) / `page.css`(116K, 58) / `common_mobile.css`(174K) | **메뉴 이관 때 흡수** — web.css가 슬림 추출본이라 500K 일괄 병합은 큐레이션 붕괴. 페이지가 실제 쓰는 규칙만 그때 common/web/mobile로 승격 |

### 정적 경로 규칙 (/resources 정규화)

- 레거시 절대경로를 `/resources/` 아래에 미러링: `/newPortal/…` → `/resources/newPortal/…`, `/anymobi/…` → `/resources/anymobi/…`, `/course/…` → `/resources/course/…`.
- CSS `url()` 재작성 = 접두어 치환. 상대경로(`../icons/`)는 구조 유지 시 그대로.
- 이미지/아이콘/폰트 실제 파일은 **메뉴 이관 때** 참조분만 가져온다(일괄 금지). 그전까지 경로만 정규화해 둠(파일 오면 동작).

## JS 구조 (`webapp/resources/js/`)

| 위치 | 역할 |
|---|---|
| `common/common-ajax` `-dialog` `-form` `-file` `-check` `-datepicker` `-table` `formatter` `common` `header` | portal 공통 UI. **최대한 재사용** |
| `pages/<메뉴>/<메뉴>.js` | 메뉴 전용 JS (css `pages/<메뉴>/`와 대칭) |

- 원칙: 레거시 JS 동작을 **공통 `common-*`로 대체**한다. (ajax→common-ajax, alert/confirm→common-dialog, 폼/체크/파일→common-form/check/file, 달력→common-datepicker, 테이블→common-table)
- 예외: 레거시 로직이 너무 복잡해 재작성 위험이 크면 **그대로 유지**하되 `js/<메뉴>/`에 두고 공통과 섞지 않는다.
- 벤더 재조정: jQuery→portal(webjars 3.7.1), selectric→portal(vendor), 차트→plotly. genius의 jquery-ui/d3/video.js/KtLms 갓오브젝트는 **기본 미이관**, 특정 메뉴가 필요할 때만 vendor로.

## 이미지/아이콘/폰트

- **나중에**: 메뉴 이관 시 참조되는 것만 가져온다. (일괄 복사 금지 — images 61M)

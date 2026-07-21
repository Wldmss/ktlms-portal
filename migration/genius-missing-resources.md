# genius 이관 — 누락 리소스 목록

> legacy CSS(`resources/legacy/**`)가 `url()`로 참조하지만 **genius 소스 repo(`kt_source_2607/genius_prd`)에 파일이 없는** 이미지 목록.
> 전체 genius를 파일명(대소문자 무시) 기준으로 탐색한 결과. 작성 2026-07-08.
> 경로 규칙은 [genius-css-js-convention.md](genius-css-js-convention.md) 참고. 실제 파일은 `resources/legacy/` 하위(gitignore로 바이너리 제외).

## 요약
- 참조 이미지 중 **6개는 genius 다른 경로에서 찾아 추가 완료**, **31개는 소스에 없어 미해결**.
- 미해결은 성격상 3부류: 외부시스템(ERP/EP, 무시 가능) · new23 디자인 에셋(확보 필요) · anymobi 잔여.

---

## 미해결 (31) — 추가 불가 (genius repo에 없음)

### A. 외부 ERP 아이콘 (12) — 무시 가능
mywork(외부 업무포털) 아이콘. genius WebContent에 미포함(외부 시스템 리소스).
- 참조 CSS: `anymobi/css/kate/main_kt.css`, `main_kt2.css` (상대경로 `../mywork/`)
- 파일: `ico_erp01.gif` ~ `ico_erp06.gif`, `ico_erp01_.gif` ~ `ico_erp06_.gif`

### B. 외부 EP 포털 이미지 (4) — 무시 가능
EP(엔터프라이즈 포털) 리소스. 예상경로 `/resources/legacy/EP/image/ktep/main/`.
- 참조 CSS: `anymobi/css/kate/main_kt.css`, `main_kt2.css`
- 파일: `tb_bg.gif`, `tb_bg02.gif`, `list_head_left.gif`, `list_head_right.gif`

### C. new23 디자인 에셋 (12) — ⚠️ 확보 필요
genius 현행 화면(new23)이 실제 사용하는데 이 repo 스냅샷엔 없음. 최신 빌드/운영 export 필요.
- 예상경로 `/resources/legacy/newPortal/{icons,images}/`

| 파일 | 참조 CSS |
|---|---|
| `images/banner-img1.png` | `newPortal/css/page.css` |
| `images/new23_edu_link_img1~4.png` | `newPortal/css/new23.css` |
| `icons/new23_admit_list1~4.svg` | `newPortal/css/new23.css`, `new23_m.css` |
| `icons/ico_bar_category_11.svg` | `newPortal/css/new23.css`, `new23_m.css` |
| `icons/ico_reason_check.svg` | `newPortal/css/new23.css` |
| `icons/m/ico_navi_top_course_exit.svg` | `newPortal/css/new23_m.css` |

### D. anymobi 잔여 (3) — 확보 필요
예상경로 `/resources/legacy/anymobi/img/`.
| 파일 | 참조 CSS |
|---|---|
| `radio_button_normal.png` | `anymobi/css/style.css` |
| `radio_button_selected.png` | `anymobi/css/style.css` |
| `icon_darkgray_circle_sm.png` | `anymobi/css/main.css` |

---

## 해결 완료 (6) — genius 다른 경로에서 찾아 추가
| 배치 경로 | genius 원본 위치 |
|---|---|
| `common/images/helpdesk/bg_table_01.gif` | `common/images/common/bg_table_01.gif` (동명 8개 중 택 — ⚠️헬프데스크 화면 확인) |
| `anymobi/img/copboard/icon_good.png` | `anymobi/img/icon_good.png` |
| `anymobi/adm/images/arrow_2_close.png` / `arrow_2_open.png` | `anymobi/adm/images/icampus/…` |
| `anymobi/adm/img/arrow_1_close.png` / `arrow_1_open.png` | `anymobi/adm/images/icampus/…` |

---

## 처리 방침
- **A·B (외부 ERP/EP, 16개)**: 무시. 외부 시스템 리소스라 new-genius에서 불필요할 가능성 높음. 해당 메뉴(ERP연동/EP) 이관 여부 결정 시 재검토.
- **C·D (new23·anymobi, 15개)**: 최신 genius 소스(운영/빌드 export)를 확보하면 파일명으로 찾아 예상경로에 채운다. 없으면 해당 화면 이관 시점에 실물 확보.
- 미해결 참조는 렌더 시 404만 날 뿐 CSS 구조/포맷엔 영향 없음.

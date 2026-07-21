# genius JS 인벤토리 & 이관 목록

> genius(genius_prd)가 **실제로 로드하는** JS 파일 목록. genius 전체 354개 `.js` 중, JSP/HTML에서 `<script src>`로 참조되는 것만 추림(고유 108경로, 노이즈 제외).
> 분류·방침은 [genius-css-js-convention.md](genius-css-js-convention.md)의 JS 원칙을 따른다.

> **이관 현황 (2026-07-09):** custom JS **37개 → `webapp/resources/legacy/`** 이관 완료 (원본 디렉터리 구조 유지, EUC-KR 21개 → UTF-8 변환, 오류 0). selectric은 vendor 이관 완료. 라이브러리/`ktlms.common.js`는 방침대로 미이관. 상세는 각 섹션의 상태 표기 참고.

## 핵심 원칙 (요약)

- 라이브러리는 **파일째 옮기지 않는다.** portal 표준으로 대체: jQuery→webjars 3.7.1, selectric→vendor, 차트→plotly.
- jquery-ui / d3 / video.js / KtLms 갓오브젝트 = **기본 미이관**, 특정 메뉴가 필요할 때만 vendor.
- custom JS는 "그대로 이관"이 아니라 portal 공통(`common-*`)으로 **재작성/흡수**가 원칙. 너무 복잡하면 `js/<메뉴>/`에 격리 유지.
- 페이지 전용 JS는 **메뉴 이관 시점에** 참조분만 가져온다(일괄 금지).

---

## A. 전역 로드 스택 (항상 로드) — 최우선 대상

`defaultJsLayout.jsp` · `educationJsLayout.jsp` · `examCourseJsLayout.jsp` · web/mobile `KT_Header.jsp`. CSS의 new23 스택과 대칭.

### 📚 라이브러리
| 파일 | 정체 | 크기 | 방침 |
|---|---|---|---|
| `newPortal/js/jquery-3.5.1.min.js` | jQuery 3.5.1 | 89K | ❌ portal webjars **3.7.1**로 대체 |
| `newPortal/js/jquery-ui.min.js` | jQuery UI | 253K | ❌ 기본 미이관 |
| `newPortal/js/datapicker-ko.js` | jQuery UI datepicker 한글 로케일 | 1.2K | ❌ jquery-ui 종속 → portal `common-datepicker` |
| `newPortal/js/d3.v3.min.js` | D3 v3 | 151K | ❌ 기본 미이관 (차트→plotly) |
| `newPortal/js/jquery.selectric.min.js` | Selectric | 14K | ✅ portal **vendor**로 이관 |

### ✏️ Custom
| 파일 | 역할 | 크기 | 방침 | 상태 |
|---|---|---|---|---|
| `newPortal/js/common.js` | UA/모바일 판별 + 공통 유틸 | 15K | 🔁 portal `common/*`로 흡수 | ✅ legacy 이관(EUC-KR→UTF-8) |
| `newPortal/js/gnb.js` | GNB 네비게이션 | 4.8K | 🔁 portal `header`/`common` | ✅ legacy 이관 |
| `newPortal/js/new23.js` | new23 메인 로직(Swiper 등) | 25K | 🔁 공통→common-*, 고유→`pages/` | ✅ legacy 이관 |
| `newPortal/js/new23_m.js` | new23 모바일 메인 | 16K | 🔁 상동(모바일) | ✅ legacy 이관 |
| `anymobi/js/ktlms.common.js` | **KtLms 갓오브젝트**(layerAlert 등) | 4.6K | ❌ 기본 미이관 → `common-dialog` 등으로 대체 | ⏸ 미이관 |

> 전역 custom 4개는 `legacy/newPortal/js/`에 원본 보존(추후 portal `common-*`로 재작성/흡수 예정). selectric은 vendor 이관 완료, `ktlms.common.js`는 미이관.

---

## B. 라이브러리 전체 (vendor) — 대부분 미이관/대체

메뉴별 페이지에서 추가로 참조되는 것 포함. portal 표준으로 대체하거나, 해당 메뉴가 실제 필요할 때만 vendor로.

| 파일(경로) | 정체 | portal 대체/방침 |
|---|---|---|
| `newPortal/js/jquery-1.11.2.min.js`, `newPortal/js/jquery-1.11.3.min.js`(dead ref), `anymobi/**/jquery-1.11.3.min.js`, CDN `jquery/1.8.3`,`1.11.3` | 구 jQuery 다수 버전 | ❌ webjars 3.7.1로 통일 |
| `newPortal/js/jquery-ui.min.js`, `newPortal/js/jquery-ui.min(1).js`(dead), `anymobi/**/jquery-ui-1.11.4/jquery-ui.min.js` | jQuery UI | ❌ 기본 미이관 |
| `newPortal/js/jquery.selectric.min.js` | Selectric | ✅ vendor |
| `newPortal/js/jquery.form.min.js`, `anymobi/adm/js/jquery.form.min.js` | jQuery Form (ajax submit) | 🔁 `common-ajax`/`common-form`로 대체 |
| `anymobi/adm/js/jquery.validate.js`, `anymobi/lib/validator/jquery.validate.min.js`, `anymobi/adm/js/messages_ko.js` | jQuery Validate + 한글 msg | 🔁 `common-check`로 대체 |
| `newPortal/js/jquery.rwdImageMaps.min.js` | 반응형 이미지맵 | ❌ 필요 메뉴만 |
| `anymobi/adm/js/jquery.number.min.js`, `anymobi/lib/js/jquery/jquery.numeric.min.js`, `anymobi/adm/js/jquery.dimensions.min.js` | 숫자 포맷/입력, dimensions | 🔁 `formatter` 등으로 대체 |
| `anymobi/lib/placeholder/jquery.placeholder.js` | placeholder 폴리필(IE) | ❌ 폐기(JDK21/모던 브라우저) |
| `anymobi/lib/scrollbar/jquery.mCustomScrollbar.concat.min.js`, `anymobi/adm/js/jquery.scrollableFixedHeaderTable.js` | 스크롤바/고정헤더 테이블 | ❌ 필요 메뉴만 (`common-table` 검토) |
| `anymobi/lib/jquery.rateit/jquery.rateit.min.js` | 별점 | ❌ 필요 메뉴만 |
| `anymobi/mobile/js/jquery/jquery.mobile-1.4.5.min.js` | jQuery Mobile | ❌ 폐기 |
| `newPortal/js/d3.min.js`, `d3.v3.min.js`, `d3.v4.js`, `d3.v4.min.js`, `d3.v4.tip.js`, CDN `d3js.org/d3.v4` | D3 (v3/v4 혼재) | ❌ 차트→plotly |
| `newPortal/js/plotly-3.0.3.min.js`, `plotly-latest.min.js`, `plotly.min.js` | Plotly | ✅ **portal 차트 표준** (버전 정리 필요) |
| `newPortal/js/chart.js` | Chart.js | ❌ 필요 메뉴만 (plotly 우선) |
| `newPortal/js/swiper.min.js`, `swiper4.5.1.min.js` | Swiper 슬라이더 | ❌ 필요 메뉴만 vendor (new23 종속) |
| `newPortal/js/video.js` | Video.js (1.9M) | ❌ 기본 미이관, 필요 메뉴만 |
| `../lib/js/slick/slick.min.js`, `anymobi/lib/slick/slick.min.js`, `anymobi/mobile/js/lib/slick/slick.min.js` | Slick 슬라이더 | ❌ 필요 메뉴만 |
| `../lib/js/isotope/isotope.pkgd.js` | Isotope 레이아웃 | ❌ 필요 메뉴만 |
| `anymobi/lib/amcharts/amcharts.js`, `serial.js` | amCharts | ❌ 차트→plotly |
| `anymobi/lib/iCheck-master/iCheck-master.min.js` | iCheck 체크박스 | ❌ `common-check`로 대체 |
| `anymobi/adm/js/underscore-min.js` | Underscore | ❌ 필요 메뉴만 |
| `common/js/jsencrypt.min.js` | RSA 암호화 | ⚠️ 로그인/보안 메뉴 이관 시 검토(대체 여부) |
| `bootstrap/plugins/summernote/summernote-lite.js`, `.../lang/summernote-ko-KR.js` | Summernote 에디터 | ❌ 필요 메뉴만 vendor |
| `anymobi/lib/magiclock/MaypleHD-5.0.0.7.js`, `MaypleHD-parameter.js`, `Mayple5.js`, `Mayple5skin.js` | MagicLock/Mayple DRM 플레이어 | ⚠️ 동영상 재생 메뉴 이관 시 별도 검토 |
| `anymobi/lib/js/shortcut.js` | 키보드 단축키 lib | ❌ 필요 메뉴만 |
| CDN `html5shiv`, `respond.min.js` | IE 폴리필 | ❌ 폐기 |
| CDN `dmaps.daum.net/postcode.v2.js` | 다음 우편번호 | ⚠️ 주소검색 메뉴 이관 시 그대로 외부 로드 |

---

## C. Custom JS 전체 — 실제 이관 대상 (메뉴별, 참조분만)

디렉터리별 정리. **A(전역)에 포함된 것 제외.**

> **✅ 이관 완료:** 아래 목록의 존재하는 custom 파일은 전부 `webapp/resources/legacy/<원본경로>`에 원본 구조 유지로 이관됨(EUC-KR→UTF-8 변환 포함). 이후 각 파일은 **해당 메뉴 이관 시점에** legacy에서 꺼내 `common-*` 흡수 / `pages/<메뉴>/`(또는 복잡 시 `js/<메뉴>/`)로 재배치·재작성. dead ref(취소선)는 이관 대상 아님.

### newPortal/js (신포털)
| 파일 | 추정 역할 |
|---|---|
| `commonDev.js` | 개발용 공통 (운영 이관 시 정리/폐기 검토) |
| `deptstatgraph.js` | 부서 통계 그래프 (d3 종속) |
| `stdChartVer4.js`, `wrChartVer4.js` | 표준/작성 차트 v4 (d3 종속 → plotly 재작성) |
| `miniClassSlider.js`, `miniClassSlider2.js` | 미니 클래스 슬라이더 |
| `shortsPlayer.js`, `shortsPlayer_m.js` | 숏츠 플레이어 (web/mobile) |
| `searchDetail_shorts_player.js`, `mobileSearchDetail_shorts_player.js` | 검색상세 숏츠 플레이어 (web/mobile) |
| `mobileShare.js` | 모바일 공유 |
| `videoRunning.js`, `videoRunning2.js` | 동영상 학습 진도 |
| ~~`common2.js`~~, ~~`ktlms.common.js`~~, ~~`jquery-1.11.3.min.js`~~, ~~`jquery-ui.min(1).js`~~ | **dead ref** (newPortal/js에 파일 없음) — 이관 대상 아님 |

> 디렉터리엔 있으나 위 grep에 없는 것(`main.js`,`mainSlider.js`,`myclass.js`,`quickMenu.js`,`sqisoft.*.js`,`new23_test.js` 등)은 현재 미참조 → 이관 대상 아님(메뉴 발견 시 재확인).

### anymobi/js
| 파일 | 역할 |
|---|---|
| `datepicker_custom.js` | 커스텀 달력 → `common-datepicker` 대체 |
| `popup.js`, `popup_main.js`, `popup_main1.js` | 팝업 (→ `common-dialog`/popup 컴포넌트) |

### anymobi/adm/js (관리자)
| 파일 | 역할 |
|---|---|
| `common.js` | 관리자 공통 |
| `diag.js` | 진단 |
| `admSelfDev.js`, `admSelfDevCourseRegByRoadMap.js` | 자기계발/로드맵 과정등록 (adm) |

### anymobi/lib/js
| 파일 | 역할 |
|---|---|
| `common.js` | (구)공통 유틸 |
| `commonScroll.js` | 스크롤 |
| `deptList.js`, `newDeptList.js` | 부서 목록/트리 |

### anymobi/mobile/js (모바일)
| 파일 | 역할 |
|---|---|
| `common.js` | 모바일 공통 |
| `Osinfo.js` | OS 판별 |
| `gnb.js` | 모바일 GNB |
| `my_room.js` | 마이룸 |

### anymobi/roadmap/js
| 파일 | 역할 |
|---|---|
| `roadmap.js` | 로드맵 |

### 기타
| 파일 | 역할 |
|---|---|
| `common/js/calendar.js` | 달력 → `common-datepicker` 검토 |
| `script/studyPopup.js` | 학습 팝업 |
| `assets/js/common2.js`, `assets/js/newDeptList.js` | (assets 하위) 공통/부서목록 |
| `../lib/js/main.js` (roadmap 계열) | 메인 |

---

## D. 외부/노이즈 (이관 무관)

- `chrome-extension://…/copy.js` — 브라우저 확장 주입, 소스 아님. 무시.
- `http://ehrd.ollehslp.com/jsp/common/viewImg.jsp` — 외부 이미지 뷰어(JS 아님).
- 위 B의 CDN 항목(googleapis jQuery, d3js.org, maxcdn, daum postcode)은 파일 이관 아님.

---

## 다음 액션 후보

1. ~~**selectric** → portal vendor 이관~~ ✅ 완료.
2. ~~custom JS 37개 → `legacy/` 원본 보존 이관 (EUC-KR→UTF-8)~~ ✅ 완료 (2026-07-09).
3. **plotly** 버전 정리(3.0.3 / latest / min 중 표준 확정) 후 portal vendor 확정.
4. 전역 custom(`common.js`,`gnb.js`,`new23.js`,`new23_m.js`) → portal `common-*`/`header` 흡수 매핑표 작성.
5. 이후는 메뉴 이관 때 C 목록에서 legacy 파일을 pull → 재작성/재배치.

# genius Tiles → include 전환 가이드 (인벤토리·실무)

대상: `genius_prd`(genius) → `ktlms`(portal, new-genius).
전략·통합 원칙·페이지 이관 절차는 **[tiles-removal-guide.md](tiles-removal-guide.md)** 를 따른다.
이 문서는 그 보조로, genius `WEB-INF/spring/tiles-define.xml`(76개 정의) 기준의 **① 선(先)이관 대상 조각 목록, ② subGNB↔gnb 병합 판단, ③ definition 레벨 중복·통합표**를 정리한 것이다.

---

## 0. portal include 컨벤션 (기준)

portal에 이미 아래가 구축되어 있다(`src/main/webapp/WEB-INF/views/common/layout/`).

| 파일 | GNB | 용도 |
|---|---|---|
| `common-top.jsp` / `common-bottom.jsp` | **포함** | 일반 화면(대부분) |
| `default-top.jsp` / `default-bottom.jsp` | 없음 | 팝업/최소 화면 |
| `gnb.jsp` | — | 전역 GNB fragment (독립) |
| `footer.jsp` | — | 푸터 fragment (독립) |

페이지 패턴:
```jsp
<c:set var="pageTitle" value="화면명"/>
<%@ include file="/WEB-INF/views/common/layout/common-top.jsp" %>
... 본문(기존 fragment 그대로) ...
<%@ include file="/WEB-INF/views/common/layout/common-bottom.jsp" %>
```

---

## 1. 선(先)이관 대상 — 레이아웃 "조각"을 먼저 옮긴다

tiles의 `<put-attribute>` 조각들이 include 방식에선 **공통 include 파일**이 된다. 개별 화면 JSP보다 **이 조각들을 먼저** portal에 확정해야 나머지가 이를 참조한다.

### ① 웹 공통 조각 — `web/tiles/`
| genius 파일 | 역할 | portal 대응 |
|---|---|---|
| `gnbLayout.jsp` | GNB(헤더) | `gnb.jsp` (재구현됨) |
| `defaultSubGNBLayout.jsp` | 서브 비주얼 배너 | sub-gnb fragment (§2 참조, 조건 include) |
| `defaultFooterLayout.jsp` | 푸터 | `footer.jsp` |
| `defaultCssLayout.jsp` | CSS 번들 | `common/core/resources.jsp` 흡수 |
| `defaultJsLayout.jsp` | JS 번들 | `common/core/resources.jsp` 흡수 |
| `defaultGNBLayout.jsp` / `mainLayout.jsp` / `miniLectureLayout.jsp` / `myCourseLayout.jsp` | 템플릿 셸 | `common-top/bottom` 조합으로 재구성 |
| `web/common/newDeptList.jsp` | 전 템플릿 공통 include | 공통 include 유지 |

### ② 교육/시험 전용 조각
- `web/tiles/education/` : `registrationLayout.jsp`(셸), `educationCssLayout.jsp`, `educationJsLayout.jsp`, `educationFooterLayout.jsp`
- `web/tiles/exam/` : `examCourseLayout.jsp`(셸), `examCourseCssLayout.jsp`, `examCourseJsLayout.jsp`, `examCourseFooterLayout.jsp`

### ③ 모바일 조각 — `web/tiles/mobile/` + `mobile/common/`
- CSS/JS/푸터: `defaultMobileCssLayout.jsp`, `defaultMobileJsLayout.jsp`, `defaultMobileFooterLayout.jsp`
- 셸: `mobileLayout` / `mobileContLayout` / `mobileSearchLayout` / `mobileMainLayout` / `mobileExamCourseLayout` / `mobileExamCourseLnbLayout.jsp`
- 모바일 GNB: `mobile/common/KT_GNB_Mobile.jsp`, `KT_GNB_Mobile_Main.jsp`
- 공통 include: `mobile/common/KT_Common_Styles.jspf`

### ④ Java / XML (뷰 리졸버 — 반드시 함께)
- `src/conf/spring/KT-servlet.xml` (+ 복제본 `WEB-INF/classes/conf/spring/KT-servlet.xml`)
  - `tilesViewResolver`(order=1), `tilesConfigurer` 제거 → `InternalResourceViewResolver`로 일원화.
  - ⚠️ 컨트롤러가 `"campaign/campaignList"` 같은 **definition 이름**을 리턴한다. include 방식에선 이 값이 그대로 실제 JSP 경로와 매핑돼야 하므로, `main/portalMain`처럼 definition명 ≠ jsp경로인 화면은 컨트롤러 리턴값 점검 필요.

> **최우선**: `gnbLayout` / `defaultSubGNBLayout` / `defaultFooterLayout` / `defaultCssLayout` / `defaultJsLayout` (+ 모바일 대응). 이를 include 파일로 확정한 뒤 개별 화면을 붙인다.

---

## 2. subGNB ↔ gnb 병합 판단

**결론: 물리적으로 한 파일로 합치지 않는다. GNB는 항상 include, subGNB는 조건부 include로 "조합"한다.**

| | gnbLayout.jsp | defaultSubGNBLayout.jsp |
|---|---|---|
| 역할 | 전역 GNB(헤더 네비) | 현재 메뉴 sub-visual(subTitle/subDesc 배너) |
| AJAX | `/a/layout/gnbListAjax.do` | `/a/getMenuInfoAjax2.do` (현재 URL 기반) |
| 적용 범위 | **모든** 화면 | **sub-page만** (pageLayout·miniLecture 계열) |

`mainLayout`·`myCourseLayout`·`examCourse`·`registration` 계열은 subGNB가 **의도적으로 없다**. 물리 병합 시 문제:

1. **범위 불일치** — sub-visual이 없어야 할 수십 개 화면(메인/내강의실/시험/수강신청)에 배너가 강제로 붙어 회귀.
2. **불필요한 AJAX** — 그 화면들에서도 `getMenuInfoAjax2.do`가 매번 호출 → 성능·에러로그 노이즈.
3. **DOM 구조 차이** — examCourse는 gnb를 `l-inner`로 감싸는 등 레이아웃별 wrapper가 달라 한 파일로 표현 불가.
4. **관심사 분리** — 서로 다른 엔드포인트·DOM, 재사용성 저하.

**권장 패턴** (개별 화면의 subGNB 반복 선언 중복은 제거하면서 회귀는 없음):
```jsp
<%-- sub-page: subGNB 경로만 넘김 --%>
<c:set var="pageTitle" value="캠페인"/>
<c:set var="subGNB" value="/WEB-INF/views/common/layout/sub-gnb.jsp"/>
<%@ include file="/WEB-INF/views/common/layout/common-top.jsp" %>
```
```jsp
<%-- common-top.jsp 내부 --%>
<%@ include file="/WEB-INF/views/common/layout/gnb.jsp" %>          <%-- 항상 --%>
<c:if test="${not empty subGNB}"><jsp:include page="${subGNB}"/></c:if>  <%-- sub-page만 --%>
```
→ main/myCourse 등은 `subGNB`를 안 넘기면 자동으로 배너 없음.

---

## 3. definition 레벨 중복 · 통합

### (A) 죽은 정의 → 이관 제외/삭제
- **`miniLecLayoutTiles`** : template `miniLecGNBLayout.jsp` **파일 없음** + extends하는 정의 **0개**.
- `web/tiles/education/educationLayout.jsp`, `web/tiles/mobile/mobileMainFooterLayout.jsp` : tiles-define 미참조 → 사용처 확인 후 정리.
- `common/commonSearchMemPop` : 미참조 팝업(확인 후 default-top 기반으로만 필요 시 이관).

### (B) 웹 템플릿 4종 → 통합
`pageLayoutTiles` / `mainLayoutTiles` / `miniLectureLayoutTiles` / `myCourseLayoutTiles` 는 css·js·gnb·footer·title이 **전부 동일**. 차이는 두 가지뿐:
- subGNB 유무 (page·miniLecture = 있음 / main·myCourse = 없음) → `${subGNB}`
- body 래퍼 class (`l-body` / `l-inner l-body` / 중첩) → `${contentClass}`

→ `common-top/bottom` 한 벌 + 변수로 흡수.

### (C) subGNB put-attribute 반복 (최대 중복)
`miniLectureLayoutTiles`를 extends하는 **약 40개** 정의가 동일한
`<put-attribute name="subGNB" value=".../defaultSubGNBLayout.jsp"/>` 한 줄을 반복.
→ §2 조합 패턴으로 전부 소멸.

### (D) 개별 선언 → 와일드카드/통합
| 통합 | 현재 개별 정의 | 비고 |
|---|---|---|
| `diagnosis/*` | `diagnosis/diagResultControlDetail` | **완전 중복**, `{1}`로 이미 매칭 → 개별 제거 |
| `education/list/*` | courseList, externalCourseList, techCourseList, shortsList | mainLayout, body만 다름 |
| `dept/newDept/*` | newDeptList, deptStatusList, diagPersonalReport | mainLayout |
| `exam/result/*` | List, Detail, ItemDetail | mainLayout |
| `cds/result/*` | cdsPersonalResultDetail, cdsLeaderResultDetail | mainLayout |
| `academy/facilitiesGuide/*` | Wonju, Seoul, Deajeon, Training | miniLecture |
| `academy/facilitiesRsv/*` | facilitiesList, ChkinList, AllChkinList | miniLecture |
| 모바일 다수 | cop, exlicense, hrdIbox, setting, market, exam 등 | `mobile/m/.../*` 통합 |

### (E) 모바일 템플릿 6종 → 2종
`mobileLayoutTiles` = `mobileContLayoutTiles` = `mobileSearchLayoutTiles` = `mobileExamCourseLayoutTiles` = `mobileExamCourseLnbLayoutTiles` : css/js/gnb(`KT_GNB_Mobile`)/footer **4개 attribute 완전 동일**.
`mobileMainLayoutTiles` 만 gnb=`KT_GNB_Mobile_Main`.
→ **「모바일 공통 top」 + 「모바일 메인 top」 2종**으로 축소. 셸의 wrapper div 차이만 페이지별로.

### ⚠️ 합치면 안 되는 것
- `expert/*` 는 `expert/{1}.jsp` **한 depth만** 매칭 → `expert/recommend/expertRecomList`(두 depth)는 매칭 불가하므로 개별 선언 **유지**.

---

## 4. 확정 템플릿 구성 (최소 공통 + 나머지 개별 include)

원칙: **최소한의 공통 템플릿만 만들고, 소수 예외(엄청난 중복이 아닌 것)는 페이지에서 개별 include.**

### 4.1 웹 — `tiles-top` / `tiles-bottom` **1쌍**
`page`/`main`/`miniLecture`/`myCourse` 4종은 css·js·gnb·footer 동일 → **subGNB 분기를 포함한 공통 템플릿 1쌍**으로 흡수.
- GNB: 항상 include
- subGNB: `${subGNB}` 있을 때만 조건 include (§2)
- 콘텐츠 래퍼: `${contentClass}` (기본 `content`)
- css/js/footer: portal `core/meta` + `core/resources`(head) / `core/script`(bottom)로 대체

**예외 2계열(개별 include)**: `education`(registration)·`exam`(examCourse)은 css·js·footer 번들이 달라(`educationCssLayout`·`examCourseCssLayout` 등) 새 템플릿을 만들지 않고, **해당 페이지에서 전용 css/js/footer만 개별 include**.

→ 구현 위치: `common/tiles/tiles-top.jsp`, `common/tiles/tiles-bottom.jsp` (아래 §6).

### 4.2 모바일 — GNB 기준 **2종**
모바일 셸 6종은 GNB만 `KT_GNB_Mobile`(5종) / `KT_GNB_Mobile_Main`(mobileMain 1종) 두 갈래 → **top 2종**.

| GNB 파일 | 성격 | 특징 |
|---|---|---|
| `KT_GNB_Mobile.jsp` (114줄) | **서브페이지용 경량 헤더** | 뒤로가기+홈 버튼 · 새로고침 · `.gnb-category`(현재 페이지 타이틀) · AJAX `getMenuInfoAjax.do`(현재 메뉴명만) · 메뉴 트리 없음 |
| `KT_GNB_Mobile_Main.jsp` (437줄) | **메인/전체메뉴용 풀 GNB** | 햄버거 전체메뉴(1·2·3depth 드로어) · 알림 버튼+미읽음 카운트 · QR체크 · 사용자명 · AJAX `gnbListAjax.do`+`alarmCntAjax.do` · linkUrl/ssoLoginToHunet/goOutLink 등 함수 다수 |

기능 차이가 커(114 vs 437줄) 2종 유지가 맞고, 하단 네비바·lnb·search wrapper 차이는 페이지별 class/조건 include로 처리.

### 4.3 definition → 템플릿 매핑

| genius 레이아웃(extends) | subGNB | 대응 템플릿 |
|---|---|---|
| `mainLayoutTiles`, `myCourseLayoutTiles` | 없음 | `tiles-top/bottom` (subGNB 미전달) |
| `pageLayoutTiles`, `miniLectureLayoutTiles` | 있음 | `tiles-top/bottom` + `${subGNB}` |
| `registrationLayoutTiles`, `examCourseLayoutTiles` | 없음 | `tiles-top/bottom` + 전용 css/js/footer 개별 include |
| `mobile*LayoutTiles` (일반 5종) | — | 모바일 공통 top (`KT_GNB_Mobile`) |
| `mobileMainLayoutTiles` | — | 모바일 메인 top (`KT_GNB_Mobile_Main`) |
| `common/commonSearchMemPop` (팝업) | — | `default-top` |

---

## 6. 구축 현황 (portal, JSP 이관 前)

기존 genius 소스는 그대로 두고, portal `common/tiles/` 하위에 웹 템플릿과 이관 프래그먼트를 선구축했다. (임시 `GNBLayout.jsp`는 삭제)

| 파일 | 내용 |
|---|---|
| `common/tiles/tiles-top.jsp` | 웹 공통 top. `core/meta`+`core/resources` head, `gnbLayout` 항상 include, `${subGNB}` 조건 include, `${contentClass}` 래퍼 |
| `common/tiles/tiles-bottom.jsp` | 웹 공통 bottom. `defaultFooterLayout` + `core/script` |
| `common/tiles/gnbLayout.jsp` | genius GNB 이관(UTF-8, taglib jakarta). AJAX `/a/layout/gnbListAjax.do` |
| `common/tiles/defaultSubGNBLayout.jsp` | genius 서브 비주얼 이관(UTF-8). AJAX `/a/getMenuInfoAjax2.do`. `${subGNB}` 값으로 사용 |
| `common/tiles/defaultFooterLayout.jsp` | genius 푸터 이관(UTF-8). goOpenSource/goPrivacyPolicy |
| `common/tiles/mobile/m-top.jsp` / `m-bottom.jsp` | 모바일 **공통** top/bottom. `KT_GNB_Mobile`(서브 헤더), `${contentClass}` |
| `common/tiles/mobile/m-main-top.jsp` / `m-main-bottom.jsp` | 모바일 **메인** top/bottom. `KT_GNB_Mobile_Main`(풀 GNB) + 하단 네비바 + 교육링크 모달 |
| `common/tiles/mobile/KT_GNB_Mobile.jsp` | 모바일 서브 헤더 이관(UTF-8) |
| `common/tiles/mobile/KT_GNB_Mobile_Main.jsp` | 모바일 메인 풀 GNB 이관(UTF-8) |
| `common/tiles/mobile/defaultMobileFooterLayout.jsp` | 모바일 푸터 이관(UTF-8) |

페이지 사용 패턴:
```jsp
<%-- sub-page 예 --%>
<c:set var="subGNB" value="/WEB-INF/views/common/tiles/defaultSubGNBLayout.jsp"/>
<%@ include file="/WEB-INF/views/common/tiles/tiles-top.jsp" %>
... 본문(기존 body fragment) ...
<%@ include file="/WEB-INF/views/common/tiles/tiles-bottom.jsp" %>
```

> 참고: 이관 프래그먼트가 호출하는 AJAX 엔드포인트는 아래 §7 목(mock)으로 응답한다.

---

## 7. java/DB — 레이아웃 AJAX 목(mock) 처리

### 배경
- tiles→include 전환의 필수 java는 **뷰 리졸버 설정뿐**(DB 무관). 컨트롤러는 뷰네임만 리턴.
- GNB/subGNB/알림 "데이터"는 java+DB 의존인데, 해당 `.do` 컨트롤러가 genius_prd에 어노테이션 소스로 없음(647개 java 중 `@Controller` 1개 → `anymobi` 프레임워크 계층). ⇒ 실제 컨트롤러+메뉴 DB 이관은 별도 트랙.
- **DB 없이 레이아웃 골격을 검증**하기 위해, 레거시 JS 계약(응답 형태)을 그대로 흉내내는 목 컨트롤러를 둠.

### 구현: `common/layout/controller/LayoutMockController.java` (임시)
| 엔드포인트 | 대상 | 응답(목) |
|---|---|---|
| `/a/layout/gnbListAjax.do` | 웹 GNB / 모바일 메인 GNB | **device 분기**: 웹=HTML 조각, 모바일=`{menuList,tutorYn,appCheck,hrdCheck}` |
| `/a/getMenuInfoAjax2.do` | 웹 subGNB | `{menu:{menuNm,menuText,linkUrl,imgFileNm}, serverType}` |
| `/mobile/m/a/getMenuInfoAjax.do` | 모바일 서브 헤더 | `{menu:{menuNm}}` |
| `/a/alarm/alarmCntAjax.do` | 모바일 알림 카운트 | `{alarmCnt:{unreadCnt:0}}` |
| `/a/insertMenuLogAjax.do` | 메뉴 로그 | `{}` (no-op) |

- 웹/모바일이 같은 `gnbListAjax.do`를 공유 → `DeviceResolver.resolve(request).isMobile()`로 분기.
- 응답은 레거시 계약이라 `ResponseDTO` 미사용. **실제 메뉴 컨트롤러+DB 이관 시 이 클래스 삭제.**
- 시큐리티: `anyRequest().authenticated()`라 로그인 세션에서 호출됨(GNB 자체가 로그인 후 노출). 비로그인 검증이 필요하면 `SecurityConfig` permitAll에 `/a/**` 임시 추가.

---

## 5. 선이관 체크리스트

- [x] 웹 공통 `tiles-top` / `tiles-bottom` 확정 (`core/meta·resources·script` 활용).
- [x] genius `gnbLayout` / `defaultSubGNBLayout`(subGNB) / `defaultFooterLayout` 이관(UTF-8) + `tiles-top`에 subGNB 조건 include.
- [x] 임시 `GNBLayout.jsp` 삭제.
- [x] 모바일 top 2종(`m-top` 일반 / `m-main-top` 메인) + 대응 bottom(`m-bottom` / `m-main-bottom`) 확정, GNB·footer 이관.
- [x] 레이아웃 AJAX 목 컨트롤러(`LayoutMockController`) 구축 — DB 없이 GNB/subGNB/알림 렌더 검증.
- [ ] education/exam 전용 css/js/footer 개별 include 방식 확정.
- [ ] `KT-servlet.xml`에서 tilesViewResolver·tilesConfigurer 제거, InternalResourceViewResolver 정리.
- [ ] 죽은 정의(§3-A) 이관 대상에서 제외.
- [ ] 와일드카드 통합(§3-D) 반영, `expert/*` 예외 확인.
- [x] 이관 프래그먼트 EUC-KR → UTF-8 통일.

# Tiles 제거 가이드 (genius / lms)

분석 대상: `genious_prd`(genius), `lms_prd`(lms). 둘 다 Apache Tiles 3 사용.
기준 프로젝트: `ktlms-portal`(portal) — Tiles 미사용. **JSP include 레이아웃** 방식.

이 문서는 genius/lms의 Tiles를 걷어내는 실전 가이드다. 방식은 **명시적 include로 완전히 제거**(뷰 리졸버 매핑 없음)이며, 코드 이관 전에 **중복을 제거한 최소 레이아웃 세트를 미리 만들어두는** 것을 전제로 한다.

---

## 0. 방식 — 명시적 include로 완전 제거

레거시 tiles body JSP는 **전부 fragment**(`<html>/<head>/<body>` 없음, 콘텐츠 조각만)이고 `<tiles:getAsString>/<tiles:useAttribute>` 같은 attribute 읽기도 **0건**이다. 그래서 각 페이지에 상/하단 레이아웃을 **직접 include**해서 자립 페이지로 만든다. portal의 실제 페이지(sample/login)가 이미 쓰는 방식이다.

```jsp
<c:set var="pageTitle" value="집합관리"/>
<%@ include file="/WEB-INF/views/common/layout/common-top.jsp" %>
... 본문(기존 fragment 그대로) ...
<%@ include file="/WEB-INF/views/common/layout/common-bottom.jsp" %>
```

**뷰 리졸버 매핑(브릿지) 방식은 채택하지 않는다.** 그 방식은 `tiles-define.xml`을 자바로 재구현하는 셈이라(패턴→레이아웃 매핑을 영원히 유지·동기화) 유지보수 부담이 크다. `CustomViewResolver`는 폐기·삭제되었다.

**"중복 include 2줄" 오해**: 레이아웃 마크업은 중복되지 않는다. 공통 부분(head/gnb/footer/script)은 `common-top.jsp`/`common-bottom.jsp` 한 벌에만 있고, 페이지엔 `<%@ include %>` 2줄만 반복된다. 레이아웃 변경은 top/bottom 한 파일만 고치면 된다.

---

## 1. 레이아웃 통합 설계 (중복 제거) — 이관 전에 확정·구축

tiles 레이아웃이 많아 보이지만 **뼈대는 거의 같고, 차이는 몇 개 변수뿐**이다. 그 변수를 페이지가 `<c:set>`으로 넘기고 공통 top/bottom이 읽게 하면 레이아웃 수가 확 준다.

### 1.1 genius: 웹 8 + 모바일 6 → **웹 1 + 모바일 1** (파라미터화)

genius 웹 레이아웃(pageLayout / miniLecture / main / registration / myCourse / examCourse)의 실제 차이는 셋뿐이다:

| 차이 | tiles에서 | 통합 후 (변수) |
|---|---|---|
| 콘텐츠 래퍼 class | `l-body` vs `l-wrap mini-class` vs `pt64`… | `${contentClass}` (기본 `l-body`) |
| subGNB 유무 | 43개가 `subGNB` override | `${subGNB}` 있으면 include |
| css/js 번들 | default / education / exam | `${extraBundle}` 또는 페이지가 추가 css include |

→ **웹은 `web-top.jsp` / `web-bottom.jsp` 한 쌍**으로 통합. head+gnb+(subGNB)+래퍼+body영역 / footer+script.

genius 모바일 레이아웃(mobileLayout / mobileMain / mobileCont / mobileSearch / mobileExamCourse / mobileExamCourseLnb)의 차이도 gnb 변형·하단 네비바·lnb 유무뿐 →

| 차이 | 통합 후 (변수) |
|---|---|
| gnb 종류 (일반/메인) | `${mGnb}` (기본 일반 gnb) |
| 하단 네비게이션바 | `${bottomNav}` true면 include |
| lnb | `${lnb}` 있으면 include |

→ **모바일은 `m-top.jsp` / `m-bottom.jsp` 한 쌍**으로 통합.

### 1.2 lms: 6 → **3** (기본 / 팝업 / 운영)

| 통합 레이아웃 | 흡수하는 tiles 레이아웃 | 파라미터 |
|---|---|---|
| **admin-page** (기본 관리자, 44개) | `pageLayoutTiles` | `pageTitle` |
| **admin-full** (팝업/모달, GNB·footer 없음, 8개) | `fullPageLayoutTiles` | `pageTitle` |
| **admin-oper** (운영 화면) | `blendOper` + `mpackOper` + `examOper` + `capability3` **(4개가 구조 동일)** | `operTop`, `operSubMenu`, `operContext`(JS 컨텍스트), `operJs` |

운영 레이아웃 4개는 "기본 + 상단(top) + 좌측(subMenu) + JS 컨텍스트 객체(Blend/Mpack/Exam/Capability) 초기화"로 **뼈대가 같다.** 차이나는 top/subMenu JSP 경로와 JS 객체만 변수로 받으면 **하나(admin-oper)로 통합**된다.

### 1.3 통합 요약

| | tiles 레이아웃 | 통합 후 |
|---|---|---|
| genius | 14 (웹8+모바일6) | **2** (web, mobile) — 파라미터화 |
| lms | 6 | **3** (admin-page, admin-full, admin-oper) |

미사용/깨진 레이아웃은 통합 대상에서 제외: genius `miniLecLayoutTiles`(참조 JSP 없음), lms `blendRegLayout`·`blendExpenseLayout`.

### 1.4 파라미터 전달 패턴 (핵심)

페이지가 top include **전에** `<c:set>`으로 값을 넣고, top이 기본값과 함께 읽는다.

페이지(genius miniLecture 계열 예):
```jsp
<c:set var="pageTitle" value="캠페인"/>
<c:set var="contentClass" value="l-wrap mini-class"/>
<c:set var="subGNB" value="/WEB-INF/views/common/layout/subGNB.jsp"/>
<%@ include file="/WEB-INF/views/common/layout/web-top.jsp" %>
... 기존 fragment 그대로 ...
<%@ include file="/WEB-INF/views/common/layout/web-bottom.jsp" %>
```

web-top.jsp(발췌):
```jsp
<title>${empty pageTitle ? '지니어스' : pageTitle}</title>
<%@ include file="/WEB-INF/views/common/core/resources.jsp" %>
</head><body>
<jsp:include page="/WEB-INF/views/common/layout/gnb.jsp"/>
<c:if test="${not empty subGNB}"><jsp:include page="${subGNB}"/></c:if>
<div class="${empty contentClass ? 'l-body' : contentClass}">
```

lms 운영 화면 예:
```jsp
<c:set var="pageTitle" value="신청과정운영"/>
<c:set var="operTop" value="/WEB-INF/views/adm/educontents/blend/operation/top.jsp"/>
<c:set var="operSubMenu" value="/WEB-INF/views/adm/educontents/blend/operation/left.jsp"/>
<c:set var="operContext" value="Blend"/>
<%@ include file="/WEB-INF/views/common/layout/admin-oper-top.jsp" %>
... 본문 ...
<%@ include file="/WEB-INF/views/common/layout/admin-oper-bottom.jsp" %>
```

### 1.5 만들어둘 것 (이관 전)

- portal은 이미 `common-top/bottom`(GNB), `default-top/bottom`(GNB 없음)을 갖고 있다. 이를 **파라미터화**(`pageTitle`/`contentClass`/`subGNB` 지원)하여 재사용한다.
- genius용(new-genius) / lms용(new-admin)은 portal 복제본이므로, 위 통합 레이아웃(genius web/mobile, lms admin-page/full/oper)을 **각 프로젝트에서 1회 구축**한 뒤 페이지를 이관한다.
- css/js는 `resources.jsp`(head)/`script.jsp`(bottom)로 흡수(레거시 `defaultCssLayout`/`defaultJsLayout` 역할). 레이아웃별 추가 번들만 해당 top에서 개별 include.
- **charset**: genius 웹 레이아웃 일부가 EUC-KR → 전부 **UTF-8**로 통일.

---

## 2. 페이지 이관 절차 (definition 1건)

1. tiles-define.xml에서 그 definition의 레이아웃(extends)·body 경로·override(title/subGNB 등)를 확인.
2. body fragment를 portal `WEB-INF/views/**` 규칙 위치로 이동(경로=뷰네임), charset UTF-8.
3. body 맨 위에 `<c:set>`(필요한 변수) + `<%@ include ...-top.jsp %>`, 맨 아래에 `...-bottom.jsp` 추가. **본문 내용은 그대로.**
4. 어느 통합 레이아웃을 쓸지 1.1~1.2 표로 결정. 없으면(첫 사용) 그 레이아웃 top/bottom을 만든다.
5. body 안에 `<tiles:*>` 태그가 있으면 제거(조사상 거의 없음).
6. 컨트롤러는 뷰네임만 반환(대개 수정 불필요). 와일드카드는 자립 페이지라 불필요해짐.
7. 화면 확인: 레이아웃/gnb/subGNB/footer/css/js/한글 정상.

---

## 3. genius 주의점

- **웹/모바일 통합 레이아웃 2쌍**(1.1)만 만들면 대부분 커버된다.
- **subGNB**(43개): `${subGNB}` 변수로. 레이아웃 내부에서 조건 include.
- **레이아웃 내부 include**: 웹 레이아웃이 `common/newDeptList.jsp`를, 모바일이 `KT_Common_Styles.jspf`를 include → 통합 top/bottom에 유지.
- **모바일 하단 네비바**(mobileExamCourse의 `.navigation-tab-bar`)·lnb: `${bottomNav}`/`${lnb}` 변수로 조건 include.
- **EUC-KR → UTF-8** 변환 필수.
- **제외**: `miniLecLayoutTiles`(깨짐, 미사용), `common/commonSearchMemPop`(미참조) 이관 안 함.
- inline `<style>`(일부 레이아웃)은 통합 top에 유지하거나 CSS로 추출.

## 4. lms 주의점

- **3쌍**(admin-page / admin-full / admin-oper)만 만들면 된다.
- **운영 레이아웃 통합**(admin-oper): top/subMenu는 `<jsp:include page="${operTop}"/>`/`${operSubMenu}`로. **JS 컨텍스트 객체**(Blend/Mpack/Exam/Capability)는 tiles가 심던 값을 컨트롤러 `model` 또는 `<c:set>`으로 넘겨 admin-oper-top의 `<script>`에서 초기화. 로딩 오버레이·컨테이너 폭은 레이아웃에 유지.
- **admin-full**(팝업): GNB/footer 없는 최소 top/bottom.
- **제외**: `blendRegLayout`·`blendExpenseLayout`(미사용) 이관 안 함.
- css/js: `defaultCssLayout`/`defaultJSLayout` → resources.jsp/script.jsp로 흡수.

---

## 5. 페이지 이관 체크리스트

- [ ] definition의 레이아웃·body·override를 확인했다.
- [ ] body를 `WEB-INF/views/**`로 옮기고 UTF-8로 바꿨다(내용 유지).
- [ ] 맨 위 `<c:set>`+top include, 맨 아래 bottom include를 붙였다.
- [ ] 통합 레이아웃(1.1~1.2)을 골랐다. 첫 사용이면 그 top/bottom을 만들었다.
- [ ] (lms 운영) JS 컨텍스트 객체를 model/`<c:set>`→script로 옮겼다.
- [ ] body 안 `<tiles:*>` 태그가 없다.
- [ ] 컨트롤러는 뷰네임만 반환하며 수정이 최소다.
- [ ] 화면에서 레이아웃/gnb/subGNB/footer/css/js/한글이 정상이다.
- [ ] (전체 완료 후) TilesConfigurer·tilesViewResolver·tiles-define.xml·tiles jar를 제거했다.

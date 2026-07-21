# genius 메뉴별 이관 계획 (JSP / Java / XML)

> genius(genius_prd, Spring MVC 3.2 / JDK1.7)의 화면·기능을 portal(ktlms, JDK21)로 **메뉴 단위로 하나씩** 이관하기 위한 기준·순서·파일 번들·제외 목록.
> CSS/JS 공통은 이관 완료([genius-css-js-convention.md](genius-css-js-convention.md), [genius-js-inventory.md](genius-js-inventory.md)). 이 문서는 **JSP/Java/SQL(query)** 이관 대상.
> 작성일 2026-07-09. 코드 조사 기반이며 DB 미연결 상태 — 실제 이관 시 각 메뉴에서 재확인 필요.

---

## 0. 아키텍처 요약 (이관 판단의 전제)

| 항목 | genius 현황 | portal 이관 방향 |
|---|---|---|
| 프레임워크 | Spring MVC 3.2, `*.do` DispatcherServlet, component-scan `com.*` | Spring Boot(JDK21) |
| 뷰 | JSP + **Tiles 3** (`WEB-INF/spring/tiles-define.xml`) | 메뉴별 결정 (JSP 유지 or 전환) |
| 뷰 리졸버 | device-aware(`LiteDeviceDelegatingViewResolver`): PC=`web/`, 모바일=`mobile/` prefix. **한 컨트롤러가 web/mobile 양쪽 렌더링** | 동일 컨트롤러 → web+mobile JSP 쌍 함께 이관 |
| 컨트롤러 파라미터 | `CommandMap input` (Map 래퍼) — **`CustomMapArgumentResolver`가 바인딩** | ⚠️ `CommandMap`은 **외부 jar(소스 없음)** — 재구현/shim 선행 |
| 영속성 | **iBATIS(SqlMapClient)**, SQL은 `conf/sql/**/*.xml`. 전용 `*Dao.java` 없음 = XML이 DAO | MyBatis 전환 + `conf/sql` XML을 쿼리 원본으로 |
| DataSource | **2개**: Oracle JNDI `zpack`(주), MySQL `ktlms`(보조). batch는 별도 EDB | 로컬 개발용 DB로 통합(§3 DDL) |
| 인코딩 | 요청 EUC-KR(필터) / JSP contentType EUC-KR / 뷰 출력 UTF-8 혼재 | ⚠️ UTF-8 통일 (이관 시 정규화) |
| 인증 | 인터셉터 체인(`SessionCheckInterceptor` + `CommonMenuAuth*`), 세션키 `Constants.VL_SESSION="sessionMemberInfo"` | Phase 1에서 일괄 이관 |
| URL | `@RequestMapping`은 **메서드 레벨, path만**(`.do`는 서블릿 매핑). `/a/*`=AJAX, `/mobile/m|t/*`=모바일 | — |

---

## 1. 이관 단위(기준)

**한 메뉴 = 다음 4계층을 한 세트로 함께 이관한다.**

```
com/anymobi/usr/<menu>/          ← Controller + Service(+Impl) + VO      (Java)
WebContent/WEB-INF/jsp/web/<menu>/          ← PC 화면                    (JSP)
WebContent/WEB-INF/jsp/mobile/mobile/m/<menu>/   ← 모바일 화면            (JSP)
src/conf/sql/usr/<menu>/*.xml    ← 쿼리(iBATIS SQL map)                  (XML→query)
```

- 메뉴 경계는 4계층에서 대체로 일치하지만 **예외가 많다**(§5 플래그). SQL·JSP가 다른 메뉴 폴더에 있는 경우가 있어 번들표를 그대로 따를 것.
- 컨트롤러의 `.do` 매핑에서 web + `/mobile/m` 경로가 쌍으로 나오면 **양쪽 JSP를 같이** 가져온다.
- VO는 해당 패키지 `vo/` + 컨트롤러가 참조하는 공통 VO(`JsonResponse` 등)만.

---

## 2. 이관 순서 (의존성 기반)

의존 방향: **공통 기반 → 인증/메인 → 단순 리프 메뉴 → 복합/고결합 메뉴 → 관리자**. 상위 Phase가 하위의 전제.

### Phase 0 — 공통 기반 (1회, 이게 없으면 아무것도 안 됨)
1. **빌드/인코딩 전략**: EUC-KR 요청↔UTF-8 뷰 정규화, Jasypt 프로퍼티 복호화(`cresys.properties`, `menu_*.properties`).
2. **영속성**: DataSource(§3), iBATIS→MyBatis, `CommonDAO`/`CommonDAOImpl`, 트랜잭션 AOP(`KT-service.xml`), `conf/sql/common/*` + `conf/sql/usr/common/*`.
3. **프레임워크 코어**: `common.Constants`, ⚠️**`CommandMap` 재구현**, `CustomMapArgumentResolver`, `com.credu.library`(138파일 참조 — shim/이식), `SessionUtil`/`SessionListener`, `Utils`/`SharedMethods`, `PropertyService`, 페이징(`PageGenerator2`), `com.ktlms.common.*`(코드서비스·파일다운로드·util).
4. **웹 인프라**: 필터 체인(인코딩·lucy-xss·https·multipart·`CrossScriptingFilter`), `BaseExceptionResolver`+에러페이지, 뷰 리졸버(device-aware+Tiles+jsonView+downloadView).
5. **레이아웃 셸**: Tiles 베이스 템플릿(`pageLayoutTiles`,`mainLayoutTiles`,`miniLectureLayoutTiles`,`myCourseLayoutTiles`, mobile 변형) + `defaultCss/Js/Gnb/SubGnb/Footer` fragment + `jsp/web/common`·`jsp/mobile/common` include (CSS/공통JS는 이미 완료).

### Phase 1 — 인증/메인 (인증 메뉴 전부의 잠금 해제)
6. **`usr/common`**: `CommonUserController`(GNB `/a/layout/gnbListAjax.do` — 모든 페이지가 AJAX 로드), `CommonService`, security(RSA/AES), validation, solr.
7. **`usr/login`** (+`AdminAuth`) + **인터셉터 체인**(`SessionCheckInterceptor`, `Cont/Exam/MicroSessionCheck`, `Controller(Execute|ContectInfo)`, `CommonMenuAuthInterceptor`+`CommonMenuAuthCheckInterceptor`, `@MenuAuth`).
8. **`usr/main`**(portalMain + cash), **`usr/api`**(모바일앱 로그인/푸시 REST), **`usr/sso`**(SAML, 로그인 뷰 재사용).

### Phase 2 — 단순 리프 (패턴 검증용, 먼저)
`support`(notice→faq→qna→alarm) · `setting` · `search` · `external` · `selfDev` · `academy` · `campaign`
> **최초 이관 추천: `support/notice`** — 공통 기반에만 의존, 표준 게시판 CRUD, 전용 auth 인터셉터 없음, 일반 `support/*/*` tiles 규칙. faq/qna와 함께 기반을 end-to-end 검증.

### Phase 3 — 중간 복잡도
`educontents`(courseData/hrdIbox/leaders) · `expert` · `tutor` · `course`(offline/online/system) · `deptmng`(commonAppr) · `dept`

### Phase 4 — 복합/고결합 (마지막)
`education`(최대 모듈, 학습 플레이어 course/task + azure 시험) · `exam` · `myclass` · `learncom`(cop/copBoard 74매핑) · `cds`(+capability 확인)

### Phase 5 — 관리자 (`adm/*`)
`adm/educontents`(intellect/miniLecture) · `adm/system`(connectStatic) · `adm/common`
> ⚠️ 선행 확인: adm 화면 JSP가 `web/`·`mobile/` 트리에 없음 — **관리자 뷰 루트 위치부터 파악**.

---

## 3. DDL 전략 (로컬 DB 미연결 대응)

메뉴 이관 시 **그 메뉴의 SQL부터 처리**한다:

1. 대상 메뉴의 `conf/sql/.../<menu>/*.xml`에서 참조 테이블을 추출(`FROM`/`JOIN`/`INSERT INTO`/`UPDATE`).
2. 쿼리에서 사용하는 컬럼·타입을 역추론해 **`CREATE TABLE` DDL**을 `migration/ddl/<menu>.sql`로 정리(그때그때 로컬 DB에 테이블 생성).
3. 화면 동작 확인용 **최소 seed 데이터**(INSERT) 동반.
4. 공통 테이블(코드/메뉴/회원/부서 등, `conf/sql/common`·`usr/common`)은 **Phase 0에서 `migration/ddl/_common.sql`** 로 먼저.
5. **방언 주의**: 원본은 Oracle(`zpack`) 위주 + 일부 MySQL(`ktlms`). 로컬 대상 DB에 맞게 함수/시퀀스/`SYSDATE`/`NVL`/페이징(ROWNUM↔LIMIT) 변환. 이건 쿼리 이관과 함께 처리.

> 원칙: "메뉴 이관 = Java+JSP 이식 + 그 메뉴 쿼리의 DDL/seed 생성 + 방언 변환"을 한 묶음으로.

---

## 4. 메뉴별 파일 번들

표기: **C**=컨트롤러(매핑수), **JSP**=web/mobile jsp 개수, **SQL**=`conf/sql` 경로, **deps**=크로스-메뉴 의존.

### Phase 1 (인증/메인)
| 메뉴 | 컨트롤러(매핑) | Service/VO | JSP (web / mobile) | SQL | deps · 플래그 |
|---|---|---|---|---|---|
| common | CommonController(16), CommonUserController(22), solr/SolrController(3) | CommonService, CommonUserService / solr·validation·security·CodeInfoVo | web/common 20, layout 13, tiles 28, error 3 · mobile/common 5 | usr/common/{common,CommonUser}.xml (+root common/{CommonSql,CommonService}) | **허브**. →deptmng,login,myclass,search |
| login | LoginController(19) | LoginService, AdminAuth / — | web/login 5, main/changePw · mobile/login | usr/login/{Login,AdminAuth}.xml | →api,common. RSA복호·OTP·LDAP·SAML |
| main | MainController(27), CashController(12) | MainService, CashService / — | web/main 2 + web/cash 5 · mobile/m/main 3 | usr/main/{NewMain,cash}.xml **+ usr/main/search/Search.xml** | **허브**. →common,external,learncom,myclass,support |
| api | CommonApiController(15) | CommonApiService / vo 8(login/*) | **없음(REST)** | usr/api/CommonApi.xml | →common,education,login · **API-only** |
| sso | OpenSAMLController(5) +OpenSAMLUtil | **service 없음** / SAMLProperties | 없음(login 재사용) | **없음** | →login · **SQL/service 없음** |

### Phase 2 (단순 리프)
| 메뉴 | 컨트롤러(매핑) | Service/VO | JSP (web / mobile) | SQL | deps · 플래그 |
|---|---|---|---|---|---|
| support | alarm/AlarmController(5), faq/FaqController(2), notice/NoticeController(5), qna/QnaController(8) | Alarm/Faq/Notice/Qna Service / — | web/support 8(faq1,notice3,qna4) · mobile/m/support 7 + mobile/m/alarm 1 | usr/support/{faq/Faq,notice/Notice,qna/Qna}.xml **+ usr/alarm/alarmList.xml** | alarm=**모바일전용**. **첫 이관 추천(notice)** |
| setting | SettingController(3) `/a/main/*` | SettingService / — | **web 없음** · mobile/m/setting 2 | usr/setting/Setting.xml | **모바일 전용** |
| search | SearchUserController(2) `/a/search/*` (전문검색은 common/solr) | SearchService / — | web/search 1 · mobile/m/search 1 | **없음** → usr/main/search/Search.xml | →common.solr · SQL 타 폴더 |
| external | ExternalController(4) | ExternalService / vo 5(Bts/Highway/Kyobo…) | web/external 3 · mobile/m/external 4 | usr/external/External.xml | →main. 외부제휴(bts/highway/kyobo) |
| selfDev | UsrSelfDevController(11) `/selfDev/personal` | UsrSelfDevService / — | web/selfDev 5 · mobile/m/selfDev 4 | usr/**selfDevelop**/UsrSelfDevelopSql.xml | →course. SQL 폴더명 상이 |
| academy | AcademyController(27) | AcademyService / FacilityChkinVO | web/academy 20(guide8,rsv10,history2) · mobile/m/academy 4 | usr/academy/facilities.xml | 없음. 연수원 시설예약 |
| campaign | CampaignListController(14) | CampaignListService / — | web/campaign 6 · mobile/m/campaign 5 | usr/campaign/CampaignList.xml | →education,educontents,main |

### Phase 3 (중간)
| 메뉴 | 컨트롤러(매핑) | Service/VO | JSP (web / mobile) | SQL | deps · 플래그 |
|---|---|---|---|---|---|
| educontents | courseData/CourseDataController(10), hrdIbox/HrdIboxController(17), leaders/LeadersController(16) | 3 Service / newLecture/JsonResponse | web/educontents 14(+qrClass1) · mobile/m/educontents 17 | usr/educontents/{courseData,hrdIbox,leaders}.xml | →main. `newLecture`=**dead(VO만)**. qrClass는 CommonController |
| expert | ExpertController(10), ExpertManageController(2), ExpertRecomController(7) | 3 Service / ExpertRecomVO,InputExpertVo | web/expert 9(+popup) · mobile/m/expert 6 | usr/expert/{Expert,ExpertRecom}.xml | →common,education |
| tutor | EftController(4), TutorController(26) | Eft/TutorService / vo 5 | web/tutor 14(eft2,tutor5,tutorOffline2,tutorOut1,outlect4) · mobile/m/tutor 2 | usr/tutor/{Eft,Tutor}.xml | tutorOffline JSP는 **course가 렌더링** |
| course | OfflineClassController(7), OnlineCourseController(5), CourseSystemController(4) | 3 Service / — | web/course 5(online2,system3) · mobile/m/course 3 | usr/course/{offline/OfflineClass,online/Online,system/courseSystem}.xml | offline→main. **offline 화면=web/tutor/tutorOffline**(크로스) |
| deptmng | commappr/CommApprController(5), selfdevelop/SelfDeveolpController(2) | CommApprService / — | **web/deptmng 없음** → web/commonAppr 2 + web/selfDev 재사용 | usr/deptmng/commAppr/commappr.xml | SelfDeveolp=**service 없음** |
| dept | DeptController(15) | DeptService / vo 4 | web/dept 7(stat2,newDept5) · **mobile 없음** | usr/dept/Dept.xml | **고결합** →cds,common,educontents,exam,login |

### Phase 4 (복합/고결합)
| 메뉴 | 컨트롤러(매핑) | Service/VO | JSP (web / mobile) | SQL | deps · 플래그 |
|---|---|---|---|---|---|
| education | Course(29), CourseList(7), CourseLiveq(11), CourseRegister(5), CourseTask(41), ExternalCourse(4), TechCourse(7), **AzureExam(5)** | 7 Service / vo 4 | web/education 60(course37,list21…) **+ web/azure 3** · mobile/m/education 43 | usr/education/{Course,CourseList,CourseLiveq,CourseRegister,CourseTask,TechCourse}.xml | **최대**. →main,common,support. task=학습/시험응시 플레이어 |
| exam | ExamCourseController(20) | ExamCourseService / ExamRestApiVO | web/exam 13(course5,list1,result7) · mobile/m/exam 9 | usr/exam/ExamCourse.xml | →common,education. education/cds에서도 호출 |
| myclass | course/MyCourseController(25), course/NewMyCourseController(18), mylearncom/MyLearnComController(27) | 3 Service / MyClassVO | web/myclass 37(course25,mylearncom12) · mobile/m/myclass 17 | usr/myclass/{course/MyCourse,course/NewMyCourse,learncommunity/MyLearnCom}.xml | **고결합** →educontents,expert,learncom,selfDev,common,main |
| learncom | cop/CoPController(15), cop/CopBoardController(74), exlicense/ExLicenseController(11) | 3 Service / — | web/learncom 26(cop21,exlicense5) · mobile/m/learncom 37 | usr/learncom/{common/Common,cop/CoP,cop/CopBoard,exlicense/ExLicense}.xml | cop→myclass. copBoard 방대 |
| cds | CdsController(11), CdsAdjustController(16), CdsResultController(6) | 3 Service / — | web/cds 19(control9,diag7,result3) · **mobile 없음** | usr/cds/{Cds,CdsAdjust,CdsResult}.xml | →dept,educontents. **capability 폴더 소유 확인**(§6) |

### Phase 5 (관리자 `adm/*`) — 관리자 뷰 루트 확인 후
| 메뉴 | 컨트롤러(매핑) | Service/VO | JSP | SQL | 플래그 |
|---|---|---|---|---|---|
| adm/educontents | intellect/IntellectManagerController(14), miniLecture/MiniLectureManagerController(16) | 2 Service / MiniLectureManagerVO | **web/mobile 트리에 없음** | adm/educontents/{intellect/IntellectManager,miniLecture/MiniLectureManager}.xml | 관리자 뷰 위치 확인 |
| adm/system | connectStatic/ConnectController(3) | ConnectStaticService / ConnectStaticListExcelVo | 미확인 | adm/system/connectStatic/ConnectStatic.xml | 통계 |
| adm/common | AdminController(10) `/adm/common/*`,`/test/testQuery` | AdminService / ExcelBaseVO,MailVO | 미확인 | 전용맵 없음(root common/CommonSql) | `/test/testQuery` 제거 검토 |

---

## 5. 이관 주의 플래그 (요약)

| 플래그 | 메뉴 |
|---|---|
| **API/REST 전용(JSP 없음)** | `api`, `sso` |
| **SQL 맵 없음** | `sso`; `adm/common`(root fallback) |
| **모바일 전용(web/ 폴더 없음)** | `setting`, `support/alarm`, `icampus`(제외후보) |
| **SQL이 자기 폴더 밖** | `search`→`usr/main/search`, `alarm`→`usr/alarm`, `selfDev`→`usr/selfDevelop`, `icampus`→`usr/icampus` |
| **JSP를 다른 메뉴 폴더에서 렌더링** | `course(offline)`→`web/tutor/tutorOffline`, `deptmng`→`web/commonAppr`+`web/selfDev`, `main(cash)`→`web/cash`, `education(azure)`→`web/azure` |
| **dead/stub Java** | `adm/selfDevelop`(VO만), `usr/educontents/newLecture`(VO만) |
| **service 없는 컨트롤러** | `deptmng/selfdevelop/SelfDeveolpController` |

---

## 6. 제외 리스트 (이관 대상 아님)

### 확정 제외 (근거 있음)
| 대상 | 근거 | 신뢰도 |
|---|---|---|
| **batch 모듈** `com/anymobi/batch/*` + `KT-{batch,eojtBatch,kCampusBatch,lmsBatch,logBatch}.xml` | `@Scheduled` 배치, `isSchedulerServer()`(특정 노드만), 별도 DataSource/SqlMapClient. 포털 WAR와 분리 → **별도 JDK21 서비스로** | 높음 |
| **WebSquare** `websquareDispatcher`(`*.wq`) + `WebContent/websquare_home` | `*.wq` 파일 0개, JSP에서 websquare 참조 0. 죽은 설정 | 높음 |
| **JBoss invoker** 서블릿(`/servlet/*`, `InvokerServlet`) | web.xml에서 **주석 처리됨**(비활성) | 높음 |
| **web.bak.xml / cresys.properties.bak** | 백업 파일 | 높음 |
| **주석 처리된 spring 설정** | KT-servlet.xml 39-55(device 인터셉터), KT-batch.xml 60-65(구 DSN) | 높음 |
| **에셋 잔재** `*_old.gif/jpg`, `temp*.gif`, `tit_test*.gif` | 레거시 이미지(메뉴 이관 시 참조분만) | 중 |

### 제외 후보 (오너 확인 필요)
| 대상 | 상황 | 신뢰도 |
|---|---|---|
| **iCampus** `icampus/*` + `jsp/mobile/micampus` + `WebContent/icampus/html` | 구 모바일 메인(→`usr/main` NewMain으로 대체 추정). 현 모바일앱(`mobile/m/*`)에서 `micampus` 참조 0. 단 git 이력 없어 폐기 시점 미확인 | 중~높 |
| **DWR** `dwr.xml`(`/dwr/*`) + SCORM2004 client | SeqEngine/ClientRTS만 노출, 소비 페이지 0. **SCORM2004 플레이어 이관 범위와 연동 결정** | 중~높 |

---

## 7. 확인 필요 (UNCERTAIN)

1. **커스텀 invoker** `common/invoker/InvokerLoadListener` + `invoker.packages`(~45개 `controller.*`) + `src/controller/*`(library/mypage/scorm2004/study 서블릿) — **여전히 live 참조**(`forward:/servlet/controller.exam.ExamUserServlet` 등, copBoard/homeworkBoard JSP). 어떤 `controller.*`가 실제 사용되는지 확인 후 이식/제외.
2. **`capability` 폴더**(`web/capability`+`mobile/m/capability`) — Java에 `capability` 문자열 0인데(매핑 없음) live 모바일 JSP가 `usrPostDataPop.do` 등 호출. cds 진단/경품주소 팝업 추정 — 컨트롤러 소재/생사 오너 확인.
3. **DWR/SCORM2004 플레이어** 이관 여부 — 드롭 시 DWR + `com/credu/scorm2004/*` + `src/controller/scorm2004/*` 동반 제외.
4. **관리자(adm/*) 뷰 루트** — `web/`·`mobile/` 트리에 adm JSP 없음. 관리자 화면 위치 파악 선행.

---

## 8. 핵심 리스크 (Phase 0에서 해결)

- ⚠️ **`CommandMap`**: 61개 컨트롤러가 쓰는 파라미터 타입인데 **프로젝트에 소스 없음(외부 doitframe/anymobi jar)** → 재구현/shim 필수, 최우선.
- ⚠️ **`com.credu.library`**: 138파일이 참조(`Log`,`RequestBox`,`RequestManager`,`StringManager`…), `SessionUtil`도 의존 → 이식/shim 필요.
- ⚠️ **세션 중복로그인 맵**: `SessionUtil`의 static in-memory `ConcurrentHashMap`(단일 JVM 가정) → 다중 인스턴스 시 깨짐, 이관 시 재설계.
- ⚠️ **인코딩**: 요청 EUC-KR / 뷰 UTF-8 혼재 → UTF-8 통일.
- ⚠️ **GNB 런타임 AJAX**: 정적 include 아님(`/a/layout/gnbListAjax.do`) → Phase 1 `CommonUserController`에 종속.
- ⚠️ `Constants.VL_SERVER_TYPE` 하드코딩 `"prod"`.

---

## 다음 액션
1. 이 계획 리뷰 → 제외/확인 항목(§6·§7) 오너 확정.
2. Phase 0 착수: `CommandMap`/`credu` shim + DataSource/MyBatis + `_common.sql` DDL.
3. Phase 2 `support/notice`로 end-to-end 파일럿(메뉴 번들 + DDL/seed 절차 검증).

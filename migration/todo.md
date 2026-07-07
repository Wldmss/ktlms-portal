## env 설정 정리 (완)

- appilcation.properties 하나로 작업
- /env/{profile}.env 파일을 로드해서 작업

## 라이브러리 정리 (완-개발하면서 추가)

- portal, admin build.xml
- 대부분 라이브러리 완료

## conf xml 필요한 내용 추가 (완)
- 꼭 필요한게 아니라면 삭제
- servlet-context.xml ,root-context.xml 두개만 사용
- pagingProperties, systemProperties, appConfig, naverMapKey 등은 application.properties 로 이동
- interceptor > spring security @PreAuthorize 방식으로 변경

## exception 설정 (완)

- 대량평가, 기존 exception 처리 공통화
- 이에 따른 코드 수정 가이드 작성

## filter, interceptor 설정

- filter 전체 삭제 - spring security 로 처리 (완)
- interceptor -> @PreAuthorize 로 변경 - 코드 이전 후 진행
- 가이드 정리

## session -> jwt 변경

- jwt 세팅 (완)
- 메뉴 권한/캐시성 데이터 → 매 요청 조회 or @PreAuthorize 작업
- 가이드 문서 작성

## 공통 util 정리

- 가이드 정리

## interceptor -> @PreAuthorize 로 변경

- 코드 이전 후 진행

## properties 필수적인거만 설정하기

- 유니코드 삭제
- 코드 옮기면서 추가

## tiles 삭제 가이드

## css, js 정리

---
여기까지 portal, admin 통일
---

## 미사용 파일(jsp, js, java, xml, css ..) 정리

## tiles 설정

## direct 설정

## jdk21 변경에 따른 코드 수정

- jakarta (-> 일괄처리)
- java.time (-> 일괄처리)
- encoding (-> 일괄처리)
- 코드 전체 (외주)
- 가이드 작성

## properties 정리

- 미사용 properties drop
- 기본적인 property 만 넣어두고, 페이지 하나씩 옮기면서 필요하면 추가

## 라이브러리 변경에 따른 코드 수정 (외주)

- lucy-xss (xss 관련)
- poi

## ibatis -> mybatis 변경에 따른 수정 (외주)

- 가이드 작성

---
1순위: portal 공통으로 먼저 확정할 것
문자열/숫자/날짜 util
대상: StringManager, Utils, NumberManager, FormatDate, DateUtil, 일부 SharedMethods
portal 확장 대상: StringUtil, DateUtil, 신규 NumberUtil
필요 기능: nvl/defaultString, isEmpty, trimToNull, toInt, comma, 날짜 포맷/가감/차이, 시작일/종료일 보정
단, StringManager.makeSQL, SqlChk, SqlChkN 같은 SQL escape 계열은 그대로 만들지 않는 게 좋음. MyBatis 바인딩/화이트리스트 방식으로 전환하는 쪽이 맞음.

요청 파라미터/Map 변환 util
대상: RequestBox, DataBox, PreparedBox, RequestManager, CommandMap, MapUtil, BeanUtil
권장 방향: 신규 코드는 @RequestParam, DTO binding, @ModelAttribute 사용
이관용으로만 RequestParamUtil 또는 LegacyParamMap 같은 브릿지 제공
이건 많이 쓰이지만 장기적으로는 제거 대상이라 deprecated 성격으로 두는 게 좋음.

세션/로그인 사용자 util
대상: SessionUtil, JSP/Controller의 session 직접 접근
portal 기준: Spring Security SecurityContext 기반 CurrentUserUtil 또는 LoginUserUtil
필요 기능: 현재 사용자 ID, 권한, 회사/교육기관 정보, 관리자 여부 조회
기존 HttpSession 중심 util은 최소화.

응답/예외/리다이렉트 util
대상: RedirectUtil, ErrorManager, AlertManager, LMSServiceException, ErrorCode
portal 기준: ResponseDTO, GlobalExceptionHandler, ApiException 확장
JSP 화면용으로는 RedirectAttributes 기반 메시지 처리 helper 정도만 공통화
historyBack, alert 후 이동 같은 건 JS dialog 공통과 역할을 나누는 게 좋음.

파일 업로드/다운로드 util
대상: FileManager, FileUtil, FileUtil2, DownloadUtil, UploadDir, FileVO, MultipartRequest, FileDownAccessSessionUtil
portal 확장 대상: 현재 FileUtil
신규 공통 후보: FileStorageService, FileDownloadService, FilePolicy, UploadDirectory
필요 기능: 확장자/용량 검증, 저장 경로 정책, 파일명 정규화, 다운로드 권한 체크, ZIP, 삭제/이동.

페이징 util
대상: PageUtils, PageNavigator, Pagination, PageGenerator, PageList
신규 공통 후보: PageRequest, PageResponse, PaginationUtil, JSP용 pagination renderer
화면/JSP 이관 시 반복될 가능성이 높아서 초기에 규격을 잡는 게 좋음.

암호화/보안 util
대상: StringEncrypter, VRStringEncrypter, Encryption, AesUtil, RSAEncryptUtil, EncrypManagerNew, UtilCryptoOuter
portal에는 이미 crypto 패키지가 있으니 거기로 흡수
레거시 암복호화 데이터가 남아 있으면 “구 암호화 adapter”만 제한적으로 제공.

XSS/HTML sanitizer
대상: shieldXSS, htmlSpecialChar, Lucy filter 계열, 문자열 escape 함수
portal의 HtmlSanitizer 확장
필요 기능: 일반 text escape, editor HTML sanitize, 파일명 sanitize.

2순위: 메뉴 이관하면서 추가할 것
Excel util
대상: JxlRead, JxlWrite, POI 기반 업로드/다운로드 코드
portal의 ExcelUtil 확장
JXL은 걷어내고 Apache POI 기준으로 통일.

공통 코드/Enum util
대상: CommonCode, ParentCode, 각종 *Cd, formatter
신규 후보: CodeService, CodeEnum, CodeFormatter
관리자 화면에서 셀렉트박스/코드명 표시가 많아서 admin 이관 전에 정리 필요.

메일/SMS/알림 util
대상: MailSet, FormMail, AutoMailBean, SmsSendBean, MMSSendBean, FreeMailBean
util보다는 NotificationService, MailService, SmsService로 분리 권장.

외부 연동 util
대상: Ftp, Sftp, HTTP 호출 util
portal의 HttpClientUtil, SftpUtil 기준으로 정리.

DRM/Softcamp/PDF/Image converter
대상: DRMFileUtil, Softcamp*, PDFToImageConverter, ImageConvertExecutor
공통으로 먼저 만들기보다는 해당 메뉴 이관 시 실제 사용 여부 확인 후 adapter/service로 추가.

JS 공통으로 정리할 것
Ajax
대상: sqisoft.ajax.js, raw $.ajax
portal 기준: common-ajax.js
이미 postAjax, getAjax, CSRF/JWT 처리가 있으니 공식 표준으로 잡으면 됨.

Alert/Confirm/Dialog
대상: layerAlert, layerAlert2, layerConfirm, layerConfirm2
신규 후보: common-dialog.js
화면 이관 시 가장 많이 바꿀 부분이라 초기에 API를 확정하는 게 좋음.

Formatter
대상: SQISoft.Util.Strings, Date.prototype.format, numberWithCommas
portal 기준: formatter.js
날짜, 숫자, 전화번호, byte length, escape 정도 확장.

Datepicker
대상: KtLms.initDatePicker, KtLms.initDateRangePicker
portal 기준: common-datepicker.js
이미 구조가 있으니 이쪽으로 통일.

Form/File/Checkbox util
대상: SQISoft.Util.Form, SQISoft.Util.File, checkbox helper
신규 후보: common-form.js, common-file.js, common-check.js
기능: 동적 form submit, serialize, checked values, 파일 확장자/용량 검증.

DataTable wrapper
대상: KtLms.datatable
lms에서 사용량이 많음.
admin 이관 전에 common-table.js로 표준 옵션을 잡는 게 좋음.

공통으로 만들지 말아야 할 것
DBConnectionManager, DatabaseExecute, SQLString
StringManager.makeSQL류 SQL escape util
JS String.prototype, Date.prototype, Number.prototype 확장
RequestBox, DataBox를 영구 공통 모델로 유지하는 방식
업무 도메인 팝업 함수 전체를 common에 넣는 방식
예: showExamPreview, showSurveyPopup, Blend.goPage는 common이 아니라 exam, survey, blend 같은 도메인 JS로 분리하는 게 좋음.

추천 순서는 이거야.
String/Date/Number/Map, Formatter 먼저 정리  
common-ajax, common-dialog, common-form/file/check 확정  
CurrentUserUtil, Response/Exception, RedirectMessage 정리  
File/Download, Excel, Pagination 공통화  
LegacyParamMap 같은 이관용 브릿지 최소 제공  
admin 이관 전에 CodeService, common-table.js 정리  
메일/SMS/DRM/Converter는 실제 메뉴 이관 시 추가
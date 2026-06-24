1. 암호화 및 복호화 유틸 (CryptoUtil)
   보안이 철저한 KT 프로젝트 특성상, 사내 DB의 개인정보(비밀번호, 주민번호, 전화번호 등)를 암호화하거나 외부 API 연동 시 암복호화가 필수입니다.

주요 기능: * jasypt 기반의 프로퍼티/문자열 암복호화 (PBE 알고리즘)

bcprov (Bouncy Castle) 기반의 표준 AES-256 / SHA-256 단방향·양방향 암호화

(필요 시) KISA 표준 암호화(ARIA, SEED) 래퍼 클래스

2. 파일 업로드 및 다운로드 핸들러 (FileUtil)
   포탈의 과제 제출, Admin의 콘텐츠 등록 등 양쪽 모두에서 가장 많이 쓰이는 핵심 유틸리티입니다.

주요 기능:

commons-io 기반의 파일 복사, 삭제, 디렉터리 생성 및 용량 체크

확장자 화이트리스트 체크 (웹 쉘, 악성 파일 업로드 원천 차단 보안 로직)

파일 다운로드 시 브라우저별(IE, Chrome, Safari) 한글 파일명 깨짐 방지 인코딩 처리

3. 모던 엑셀 업로드/다운로드 컴포넌트 (ExcelUtil)
   어드민 시스템의 통계 다운로드나 포탈의 대량 사용자 일괄 등록 등 엑셀 처리는 엔터프라이즈의 필수 모듈입니다.

주요 기능:

poi-ooxml 기반의 대용량 엑셀 다운로드용 SXSSF 핸들러 (메모리 릭 방지)

Java 객체(VO/DTO) 리스트를 넣으면 자동으로 엑셀 파일로 변환해 주는 공통 메서드

엑셀 업로드 시 폼 데이터 파싱 및 가이드라인 밸리데이션(hibernate-validator 연동)

4. 외교/연동 및 SFTP 통신 모듈 (NetworkUtil)
   배치 파일 전송이나 타 시스템 연동을 위해 commons-net과 jsch를 세팅해 두셨으니, 이를 캡슐화한 모듈이 필요합니다.

주요 기능:

FTP/SFTP 클라이언트: 원격 서버 접속 ➡️ 인증 ➡️ 파일 업로드/다운로드 ➡️ 세션 종료를 안전하게 처리하는 템플릿 코드

Http Request 유틸: httpclient5 기반으로 외부 REST API나 KT 사내 타 시스템 API를 호출하고 JSON 결과를 받아오는 공통 통신 메서드 (Timeout 설정 필수)

5. JWT 토큰 및 인증 관리 유틸 (JwtTokenProvider)
   로그인 세션 공유 및 인증 처리를 위해 JWT 관련 의존성을 넣어두셨으니, 양쪽 시스템이 동일한 비밀키(Secret Key)를 바라보도록 모듈화해야 합니다.

주요 기능:

jjwt 기반의 Access Token / Refresh Token 생성 및 파싱

토큰 만료 여부 및 위변조 검증 (isValidToken)

토큰 내부의 Claims에서 사용자 정보(사번, 권한 등) 추출

6. HTML 스트립 및 XSS 방어 유틸 (XssUtil)
   게시판 본문이나 에디터 입력을 처리할 때 웹 취약점을 방어하기 위한 공통 게이트웨이입니다.

주요 기능:

jsoup 기반의 HTML 태그 화이트리스트 정제 (허용된 태그 외 스크립트 강제 제거)

lucy-xss-servlet과 연동하여 String 타입 파라미터가 들어올 때 위험 문자(<, >, & 등)를 치환(&lt;, &gt;)하는 공통 유틸

7. 문자열 및 날짜 연산 유틸 (StringUtil & DateUtil)
   commons-lang3를 기반으로, 비즈니스 로직 전반에 깔끔함을 더해주는 유틸리티 세트입니다.

주요 기능:

String Null 체크 및 디폴트 값 바인딩 (StringUtils.defaultString)

자바 8+ java.time (LocalDateTime) 기반의 날짜 포맷 변환, 날짜 차이 계산, 영업일 계산 등
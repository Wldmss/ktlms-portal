# 외부 연동 방침 (HTTP / SFTP / FTP)

분석 대상: `genious_prd`(genius), `lms_prd`(lms)
기준 프로젝트: `ktlms-portal`(portal), Spring 6.2

레거시의 외부 연동 코드(HTTP 호출 / 파일 전송)를 신규에서 어떻게 다룰지에 대한 방침. **공통 유틸을 새로 만들지 않는다** — 표준 하나를 정하고, 연동별로 그 표준을 직접 쓴다.

## 요약

| 종류 | 방침 |
|---|---|
| **HTTP 호출** | **Spring `RestClient` 직접 사용** (별도 유틸/래퍼 없음). 신규에 `HttpURLConnection`/`RestTemplate`/Apache HttpClient 4.x 도입 금지 |
| **SFTP 파일 전송** | 필요한 시점(배치 이관)에 **JSch 로 직접 구현**. 미리 공통 유틸을 두지 않음 |
| **FTP** | **미사용** (레거시 `Ftp` 는 죽은 코드) — 신규에 두지 않음 |

> portal 에 있던 `HttpClientUtil`, `SftpUtil`, `FtpUtil` 은 모두 제거했다. HttpClientUtil 은 `RestClient` 로 대체되고, SftpUtil/FtpUtil 은 현재 호출처가 없어(YAGNI) 필요할 때 만든다.

---

## 1. HTTP 호출 → Spring RestClient

레거시 HTTP 호출은 방식이 난립한다: exam(Azure) 연동은 Apache HttpClient 4.x(REST/JSON/SSL, `ExamOperApisServiceImpl`), SLP 배치는 XML, genius `ExternalController`/스케줄러는 RestTemplate, SCORM/표절검사/메일 등은 `HttpURLConnection`.

**신규 표준: `RestClient`** (Spring 6.1+ 내장, RestTemplate 후속). 별도 유틸로 감싸지 않고 연동 서비스에서 바로 쓴다 — 객체↔JSON 직렬화/역직렬화가 자동이라 래퍼가 불필요하다.

```java
// 연동별로 RestClient Bean 하나 (baseUrl/타임아웃/SSL 은 여기서)
@Bean
public RestClient examRestClient(@Value("${exam.azure.domain}") String domain,
                                 @Value("${exam.azure.uri.api}") String preUri) {
    return RestClient.builder()
            .baseUrl(domain + preUri)
            .requestFactory(clientHttpRequestFactory()) // 타임아웃/커넥션풀/커스텀 SSL
            .build();
}
```
```java
// 연동 서비스에서 직접 호출 (DTO 그대로 재사용)
ResponseResultVO res = examRestClient.post().uri("/apply")
        .contentType(MediaType.APPLICATION_JSON).body(vo)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (rq, rs) -> { throw new ApiException("exam 연동 실패"); })
        .body(ResponseResultVO.class);
```
- GET/POST/PUT/DELETE 모두 지원(exam 의 PUT 포함).
- 내부 request factory 로 Apache HttpClient 5 를 얹어 커넥션풀/타임아웃/SSL(exam 의 커스텀 인증서) 처리 → HTTP 라이브러리도 5.x 하나로 수렴.
- 반복 호출이 많은 연동(exam 등)은 RestClient Bean 하나가 곧 "그 연동 클라이언트" 역할. 규모가 커지면 그때 `XxxClient` 클래스로 분리(YAGNI).

### 이관 매핑
| 레거시 | 신규 |
|---|---|
| `ExamOperApisServiceImpl` (Apache 4.x, REST/JSON/PUT/SSL) | exam 서비스 안 `RestClient`(examRestClient Bean) |
| `SlpClient` (batch, XML) | 배치 이관 시 `RestClient`(XML은 `.body(String)` + 컨버터/수동) |
| genius `ExternalController`/스케줄러 (RestTemplate) | `RestClient` 로 교체 |
| SCORM/표절검사/`AutoMailBean`/소켓 (`HttpURLConnection`) | 해당 기능 이관 시 개별 판단(대부분 레거시·배치·특수) |

---

## 2. SFTP 파일 전송

- 레거시 실사용: lms `SchedulerService`(배치)가 영상/콘텐츠를 내부 AI/Streaming 서버로 전송. 단 `ChannelSftp`(SFTP)가 아니라 `ChannelExec`(SSH 원격 명령=SCP)를 씀.
- **방침**: 이건 **배치 영역**(웹앱과 분리 검토 대상)이라 지금 공통 유틸을 두지 않는다. 배치를 이관하고 SSH 파일 전송이 실제로 필요해지면 그 시점에 **JSch 로 직접 구현**한다(세션 open/close 보일러플레이트만 있는 작은 코드). 대상 서버가 SFTP 를 허용하는지(현재 SCP) 확인 필요.

## 3. FTP

- 레거시 `com.credu.library.Ftp` 는 import/`new`/참조가 모두 0인 **죽은 코드** → 이관하지 않는다. 신규에 FTP 유틸도 두지 않는다.
- SCORM 콘텐츠 `sco_ftp` 서버 전송이 실제 운영 중이라면 웹앱 밖 별도 경로이며, 필요 시 SFTP 로 신규 구현.

---

## 4. 정리 / 후속

- **HTTP = RestClient 직접**, 공통 유틸 없음. `HttpURLConnection`/`RestTemplate`/Apache 4.x 신규 금지.
- **SFTP/FTP = 현재 미사용**. SFTP 는 배치 이관 시 JSch 로, FTP 는 폐기.
- 의존성: `commons-net`(FTP) 은 사실상 불필요해짐 → 최종 라이브러리 정리 때 제거 검토. `httpclient5` 는 RestClient request factory 로 활용, `jsch` 는 SFTP 재구현 시 사용.

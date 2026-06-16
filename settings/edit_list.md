# 라이브러리 변경으로 인해 수정 필요한 코드 리스트

구형 레거시(Java 7~8, Spring 3~4, Java EE)에서 최신 규격(Java 21, Spring 6, Jakarta EE 10)으로 점프하면서 패키지 구조와 내부 명세가 완전히 개편되었습니다. 이로 인해 소스코드를 복사해 넣었을 때 자바 파일(`.java`)과 JSP 파일에서 빨간 줄(컴파일 에러)이 대거 발생할 것입니다.

이관 후 빌드를 성공시키기 위해 **반드시 수정해야 하는 코드 변경점 4가지**를 핵심만 명확하게 정리해 드립니다.

---

## 🛑 1. Java EE에서 Jakarta EE로 전면 전환 (가장 중요 ★)

오라클이 자바 자산권을 이관하면서 기존의 `javax.*`로 시작하던 웹 표준 패키지명이 `jakarta.*`**로 통째로 변경되었습니다. 이 작업을 안 하면 서블릿, 필터, 세션 관련 코드에서 전부 컴파일 에러가 납니다.

### 자바 파일 수정 (`.java`)

자바 소스코드 맨 상단의 `import` 구문을 모두 찾아서 변경해야 합니다.

* **기존 코드 (구형):**

```java

```


* **수정 코드 (현대 표준):**
```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

```



> 💡 **꿀팁:** 인텔리제이에서 **`Ctrl + Shift + R`** (맥은 `Cmd + Shift + R`)을 눌러 전체 치환 창을 열고, `import javax.servlet`을 `import jakarta.servlet`으로 **전체 변환(Replace All)** 하시면 몇 초 만에 해결됩니다.

---

## 📝 2. JSP 상단 JSTL 태그 라이브러리 주소(URI) 변경

JSP 화면에서 반복문(`<c:forEach>`)이나 조건문(`<c:if>`)을 쓰기 위해 상단에 선언하던 JSTL 주소도 `javax` 기반에서 `jakarta` 기반으로 변경되었습니다. 이를 수정하지 않으면 JSP 화면이 열릴 때 백색 화면과 함께 500 에러가 터집니다.

* **기존 JSP 상단 (구형):**
```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

```


* **수정 JSP 상단 (현대 표준):**
```jsp
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

```



> 💡 기존의 `http://java.sun.com/...` 형태의 기나긴 URL 주소가 최신 규격에서는 깔끔하게 `jakarta.tags.core` 형태로 축소되었습니다.

---

## 🖨️ 3. Apache POI 엑셀 컴파일 코드 수정

기존 레거시 프로젝트에서 너무 낡은 POI 버전(3.x 이하)이나 `jxl` 라이브러리를 쓰고 있었다면, 셀을 스타일링하거나 폰트를 지정하는 내부 메서드 명칭이 100% 바뀌었기 때문에 에러가 납니다.

대표적으로 엑셀 정렬 및 테두리를 지정하는 상수(Enum) 선언부가 변경되었습니다.

* **기존 코드 (POI 구버전):**
```java
// 구버전은 클래스의 상수(static final)를 직접 호출했습니다.
cellStyle.setAlignment(CellStyle.ALIGN_CENTER); 
cellStyle.setBorderBottom(CellStyle.BORDER_THIN);

```


* **수정 코드 (POI 5.2.5 버전):**
```java
// 최신 버전은 독립된 Enum(열거형) 클래스를 명확히 호출해야 합니다.
cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
cellStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

```



---

## 📁 4. 파일 업로드 구현체 변경 (Spring 6 필수 체질 개선)

우리가 구성한 스프リング 6 환경에서는 예전에 쓰던 `commons-fileupload.jar` 라이브러리를 더 이상 지원하지 않습니다. 따라서 `spring-context.xml` 이나 서블릿 설정 XML 파일에 등록되어 있던 구형 업로드 빈(Bean) 설정을 걷어내고 톰캣/와일드플라이 자체 내장 서블릿 엔진(Standard Multipart)을 바라보도록 교체해야 합니다.

### 설정 XML 파일 수정 (`context-common.xml` 또는 `servlet-context.xml`)

* **기존 설정 (구형 - 삭제 대상):**
```xml
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    <property name="maxUploadSize" value="10485760"/>
</bean>

```


* **수정 설정 (Spring 6 표준 - 대체 등록):**
```xml
<bean id="multipartResolver" class="org.springframework.web.multipart.support.StandardServletMultipartResolver"/>

```

## 5. joda time 변경

기존 레거시 코드의 `joda-time`을 자바 8부터 내장된 표준 `java.time` (JSR-310)으로 바꾸기로 결심하셨군요! 아주 훌륭한 결정입니다.

자바 21 + 스프링 6 환경으로 이관하는 지금이 이 무거운 구형 유틸리티를 완전히 걷어낼 최적의 타이밍입니다. `java.time`은 자바 표준이라 별도의 라이브러리 로딩이 필요 없고, 자바 21의 가상 스레드
환경에서 완벽한 멀티스레드 안전성(Immutable)을 보장합니다.

가장 많이 쓰이는 핵심 문법 매핑 테이블과 실무 리팩토링 단계를 명쾌하게 정리해 드릴게요.

---

## 📅 1. Joda-Time ➡️ Java.Time 1:1 매핑 가이드

두 라이브러리는 클래스 이름이 거의 비슷하지만, 핵심 몇 가지가 이름이 바뀌었습니다. `import` 구문을 바꾸실 때 아래 매핑을 참고하시면 눈 감고도 바꿀 수 있습니다.

| 기능              | Joda-Time (구형 ❌)                      | Java.Time (신형 ⭕)                         | 비고                 |
|-----------------|---------------------------------------|------------------------------------------|--------------------|
| **날짜+시간**       | `org.joda.time.DateTime`              | **`java.time.ZonedDateTime`**            | 타임존이 포함된 날짜/시간 데이터 |
| **로컬 날짜+시간**    | `org.joda.time.LocalDateTime`         | **`java.time.LocalDateTime`**            | 타임존이 없는 날짜/시간 데이터  |
| **날짜만**         | `org.joda.time.LocalDate`             | **`java.time.LocalDate`**                | 년-월-일 데이터          |
| **시간만**         | `org.joda.time.LocalTime`             | **`java.time.LocalTime`**                | 시:분:초 데이터          |
| **절대 시간 (초)**   | `org.joda.time.Instant`               | **`java.time.Instant`**                  | 타임스탬프 처리용          |
| **시간차 (밀리초)**   | `org.joda.time.Duration`              | **`java.time.Duration`**                 | 시/분/초 단위의 간격 측정    |
| **날짜차 (년/월/일)** | `org.joda.time.Period`                | **`java.time.Period`**                   | 일/월/년 단위의 간격 측정    |
| **포맷 변환기**      | `org.joda.time.format.DateTimeFormat` | **`java.time.format.DateTimeFormatter`** | 문자열 ↔ 날짜 변환기       |

---

## 🛠️ 2. 가장 자주 쓰는 실무 코드 변환 예시

실제 자바 코드에서 자주 쓰이는 문법들이 어떻게 바뀌는지 비교해 보세요.

### ① 현재 시간 구하기 및 특정 날짜 지정

```java
// Joda-Time
DateTime now = DateTime.now();
DateTime specific = new DateTime(2026, 6, 4, 12, 0, 0);

// Java.Time
ZonedDateTime now = ZonedDateTime.now(); // 타임존 포함
LocalDateTime localNow = LocalDateTime.now(); // 일반적인 로컬 시간
LocalDateTime specific = LocalDateTime.of(2026, 6, 4, 12, 0, 0);

```

### ② 날짜 연산 (더하기 / 빼기)

```java
// Joda-Time
DateTime tomorrow = now.plusDays(1);
DateTime lastMonth = now.minusMonths(1);

// Java.Time (메서드명이 완벽히 똑같아서 import만 바꾸면 거의 그대로 돌아갑니다!)
ZonedDateTime tomorrow = now.plusDays(1);
LocalDateTime lastMonth = localNow.minusMonths(1);

```

### ③ 문자열 포맷팅 및 파싱 (String ↔ Date)

> **🚨 주의:** Java.Time의 `DateTimeFormatter`는 문법이 아주 미세하게 다릅니다.

```java
// Joda-Time
String text = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(now);
DateTime dt = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime("2026-06-04");

// Java.Time
String text = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
LocalDate ld = LocalDate.parse("2026-06-04", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

```

### ④ 데이터베이스 / 마이바티스(MyBatis) 연동 시 변환

과거에는 `java.util.Date`나 `java.sql.Timestamp`로 억지 변환해서 DB에 넣었지만, 이제는 그럴 필요가 없습니다. 우리가 맞춰놓은 **MyBatis 3.5.19
버전은 `java.time`을 완벽하게 네이티브로 인식**합니다.

```java
// MyBatis 매퍼(XML)나 DTO에서 그냥 자바 표준 타입을 그대로 쓰시면 됩니다.
private LocalDateTime regDate;

```

---

## ibatis -> mybatis 변경

```
<sqlMap> ➡️ <mapper>, <select id="..." parameterClass="..."> ➡️ parameterType="..."
```

---

## header.jsp

- 공통 css, js 관련 부분 자동 import 처리 -> 기존 소스에서 모두 삭제

---

## jsp 이동 - 보류

1. /pages 하위에 /WEB-INF/jsp/* 넣는다
2. include 된 것들만 찾아서 jspf 로 변경한다
   인텔리제이에서 Ctrl + Shift + R (맥은 Cmd + Shift + R)을 눌러 전체 소스코드 치환 창을 켭니다. 그리고 상단의 Regex (정규표현식 체크박스 .*)를 체크한 뒤 아래와 같이
   입력합니다.

🔍 검색할 조건 (Match): include file="([^"]+)\.jsp"

🔄 바꿀 내용 (Replace): include file="$1.jspf"

=> /pages 하위에 모두 header, gnb, footer 가 들어갈건데,include된 곳에는 import되지 않기 위함
---

## KT-properties.xml

변수 값들 application.properties 로 이전

---

## Device import 변경

기존 구형 패키지 주소: import org.springframework.mobile.device.Device;
새로운 우리 패키지 주소: import com.kt.ktedu.common.device.Device; 



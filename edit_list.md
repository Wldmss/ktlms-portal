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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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



---

## 🎯 결론 및 행동 요약

JSP, Java 파일을 완전히 이사시키고 나면 가장 먼저 **1번(`javax` ➡️ `jakarta` 치환)** 작업과 **2번(JSP JSTL 주소 갱신)** 작업을 진행해 주세요. 대다수의 컴파일 에러가 이 두 군데서 발생하므로, 인텔리제이 전체 치환 기능을 활용해 싹 잡아놓고 시작하면 마이그레이션 속도가 훨씬 빨라질 것입니다!
# JxlRead / JxlWrite → 공통 ExcelUtil 이관 매핑 가이드

분석 대상: `genious_prd`(genius), `lms_prd`(lms) — 둘 다 `com.credu.library.excel.JxlRead`/`JxlWrite` **동일** 사용.
기준 프로젝트: `ktlms-portal`(portal) — [ExcelUtil](../src/main/java/com/kt/ktedu/common/excel/util/ExcelUtil.java) (Apache POI, JXL 미사용).

레거시 JXL(`jxl.jar`) 을 걷어내고 POI 기반 공통 `ExcelUtil` 로 통일한다. Excel 은 대부분 admin(lms) 기능이므로, portal 분리 시 사용하지 않는 쪽(new-genius)에서는 관련 코드를 삭제하면 된다.

---

## 0. 구조 차이 (먼저 이해)

| | 레거시 Jxl | 공통 ExcelUtil |
|---|---|---|
| 대상 | **서버 디스크 파일 경로** (`new JxlRead("/path.xls")`) | **스트림/response** (`InputStream`, `HttpServletResponse`, `OutputStream`) |
| 포맷 | .xls (BIFF, 구버전만) | **.xls + .xlsx 모두** 읽기 / 쓰기는 .xlsx |
| 데이터 형태 | 위치 기반 `List` 제목 + `List` 행, 읽기는 `DataBox`/`ArrayList` | 위치 기반 `List` 또는 key 기반 `Map`/DTO |
| 라이브러리 | `jxl.jar` | Apache POI |

핵심: Jxl 은 "파일 경로"로 동작하므로, 이관 시 **경로 ↔ 스트림/response 변환**이 필요하다. 대부분의 export 는 파일을 만든 뒤 별도로 다운로드했는데, 신규는 **임시 파일 없이 response 로 바로 스트리밍**하는 게 낫다.

---

## 1. JxlWrite (엑셀 생성) 매핑

레거시 API: `new JxlWrite(path)` → `makeExcel(제목List, 내용List)` / `makeExcelNoTitle(컬럼수, 내용List)` → 디스크에 .xls 저장.

### 1.1 만든 파일을 곧바로 다운로드하는 경우 (대부분) → `downloadExcelRows`

Before:
```java
String path = excelDir + "/excel_" + year + ".xls";
JxlWrite jxl = new JxlWrite(path);
jxl.makeExcel(arrTitles, arrContent);   // arrTitles: List<String>, arrContent: List<List<?>>
// 이후 DownloadServlet 등으로 path 파일을 내려받게 함
```
After (임시 파일 없이 response 스트리밍):
```java
// arrTitles(List<String>), arrContent(List<List<?>>) 를 그대로 사용
ExcelUtil.downloadExcelRows(response, arrTitles, arrContent, "excel_" + year);
// 확장자(.xlsx)는 ExcelUtil 이 자동으로 붙임
```

- `makeExcelNoTitle(컬럼수, arrContent)` → 헤더를 `null` 로: `ExcelUtil.downloadExcelRows(response, null, arrContent, fileName)`
- 컨트롤러는 `HttpServletResponse` 를 인자로 받고, 뷰 대신 `void`(또는 `@ResponseBody` 없이 response 직접 씀)로.

### 1.2 파일을 서버 디스크에 저장해야 하는 경우 (배치 결과물 등) → `writeExcelRows`

Before:
```java
JxlWrite jxl = new JxlWrite(filePath);
jxl.makeExcel(arrTitles, arrContent);
```
After:
```java
try (OutputStream os = java.nio.file.Files.newOutputStream(java.nio.file.Path.of(filePath))) {
    ExcelUtil.writeExcelRows(os, arrTitles, arrContent);   // 파일은 .xlsx 로 저장 권장
}
```
> 저장 파일도 .xlsx 로 통일한다. 파일명이 `.xls` 로 하드코딩돼 있으면 `.xlsx` 로 바꾼다.

### 1.3 key/DTO 로 정리하는 경우 (신규 작성 권장)
데이터가 Map/DTO 리스트로 이미 있으면 위치 기반 대신:
```java
Map<String,String> header = new LinkedHashMap<>(); // key → 표시 헤더
header.put("userId", "사번"); header.put("userNm", "이름");
ExcelUtil.downloadExcelMap(response, header, dataList, fileName);       // List<Map>
ExcelUtil.downloadExcelDto(response, header, dtoList, MemberDTO.class, fileName); // List<DTO>
```

### JxlWrite 기타 메서드
| 레거시 | 대체 |
|---|---|
| `setSheet()` (A4 가로 페이지 설정) | 화면 표시용 export 엔 불필요 → 제거. 인쇄 레이아웃이 꼭 필요하면 POI `sheet.getPrintSetup()` 로 개별 처리 |
| `setMergeCells(c1,r1,c2,r2)` | 병합이 필요한 특수 양식만 POI `sheet.addMergedRegion(new CellRangeAddress(r1,r2,c1,c2))` 로 직접. 공통 util 대상 아님 |

---

## 2. JxlRead (엑셀 읽기) 매핑

레거시 API: `new JxlRead(path, startRowNum)` → `getSheetInfo(idx)`(DataBox) / `readExcelData(idx)`(ArrayList) / `getCell(r,c)` / `getIntCell(r,c)` → `closeExcel()`.

공통 대체: `ExcelUtil.readRows(InputStream)`(원시 2차원) 또는 `ExcelUtil.uploadExcel(InputStream, columnKeys)`(컬럼→key Map). **경로는 InputStream 으로 열어서 넘긴다.** ExcelUtil 은 `WorkbookFactory` 라 기존 .xls 도 읽는다. `closeExcel()` 은 try-with-resources 로 자동 처리되어 불필요.

### 2.1 행/셀을 순회하며 읽던 경우 → `readRows`

Before:
```java
JxlRead jxl = new JxlRead(uploadPath, 0);
DataBox info = jxl.getSheetInfo(0);
ArrayList list = jxl.readExcelData(0);
String name = jxl.getCell(1, 0);
int cnt = jxl.getIntCell(1, 2);
jxl.closeExcel();
```
After:
```java
List<List<String>> rows;
try (InputStream is = java.nio.file.Files.newInputStream(java.nio.file.Path.of(uploadPath))) {
    rows = ExcelUtil.readRows(is);   // 헤더 포함 모든 행
}
int rowCount = rows.size();                         // getSheetInfo 의 행 수 대체
String name = rows.get(1).get(0);                   // getCell(1,0)
int cnt = NumberUtil.toInt(rows.get(1).get(2));     // getIntCell(1,2)
```
- **MultipartFile 업로드**면 경로 대신 바로: `try (InputStream is = multipartFile.getInputStream()) { ... }`
- `startRowNum`(머리글 건너뛰기)은 `rows.subList(startRowNum, rows.size())` 로.

### 2.2 고정 양식(정해진 컬럼)을 읽던 경우 → `uploadExcel(is, columnKeys)`

```java
List<String> keys = List.of("userId", "email", "name"); // A,B,C 열 순서
List<Map<String,String>> data;
try (InputStream is = multipartFile.getInputStream()) {
    data = ExcelUtil.uploadExcel(is, keys);   // 첫 행(헤더) 자동 skip
}
for (Map<String,String> row : data) {
    String userId = row.get("userId");
}
```
- 헤더 텍스트를 그대로 key 로 쓰려면 `ExcelUtil.uploadExcel(is)`.

### JxlRead 반환 타입 주의
- `getSheetInfo` 가 반환하던 `DataBox`, `readExcelData` 의 `ArrayList` 는 레거시 구조다. 신규는 `List<List<String>>` / `List<Map<String,String>>` 로 받으므로, 이 값을 쓰던 후속 코드(`box.getString(...)` 등)도 함께 정리한다.
- 셀 숫자 값은 문자열로 반환되므로 정수는 `NumberUtil.toInt(...)` 로 변환.

---

## 3. 매핑 요약표

| 레거시 (Jxl) | 공통 ExcelUtil |
|---|---|
| `new JxlWrite(path)` + `makeExcel(titles, content)` → 다운로드 | `ExcelUtil.downloadExcelRows(response, titles, content, fileName)` |
| `makeExcelNoTitle(cols, content)` | `downloadExcelRows(response, null, content, fileName)` |
| `new JxlWrite(path)` + `makeExcel(...)` → 디스크 저장 | `writeExcelRows(outputStream, titles, content)` |
| (신규) Map/DTO 데이터 export | `downloadExcelMap` / `downloadExcelDto` |
| `setSheet()` / `setMergeCells()` | 제거 또는 POI 직접 처리(특수 양식만) |
| `new JxlRead(path)` + `readExcelData`/`getCell` | `ExcelUtil.readRows(inputStream)` + 인덱스 접근 |
| 고정 컬럼 양식 읽기 | `ExcelUtil.uploadExcel(inputStream, columnKeys)` |
| `getIntCell(r,c)` | `NumberUtil.toInt(rows.get(r).get(c))` |
| `closeExcel()` | 불필요 (try-with-resources) |

---

## 4. 이관 체크리스트

- [ ] `import jxl.*`, `import com.credu.library.excel.JxlRead/JxlWrite` 제거.
- [ ] 파일 경로 기반 → `InputStream`/`HttpServletResponse`/`OutputStream` 로 변환.
- [ ] export 는 임시 파일 없이 `downloadExcelRows`(또는 Map/DTO) 로 response 직접 스트리밍. 디스크 저장이 꼭 필요할 때만 `writeExcelRows`.
- [ ] 저장/다운로드 파일 확장자를 `.xls` → `.xlsx` 로.
- [ ] `DataBox`/`ArrayList` 로 받아 쓰던 후속 코드를 `List`/`Map` 기준으로 정리.
- [ ] 숫자 셀은 `NumberUtil` 로 변환.
- [ ] 병합/인쇄설정 등 특수 기능은 해당 화면만 POI 로 개별 처리(공통 util 에 넣지 않음).
- [ ] 이관 완료 후 `jxl.jar` 의존성과 `com.credu.library.excel.*` 제거.

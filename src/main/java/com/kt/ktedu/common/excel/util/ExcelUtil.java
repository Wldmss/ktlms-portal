package com.kt.ktedu.common.excel.util;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 엑셀 업로드/다운로드 공통 유틸 (Apache POI 기준. 레거시 JXL 은 사용하지 않는다).
 * <ul>
 *   <li>다운로드: {@link #downloadExcelMap}(Map 리스트) / {@link #downloadExcelDto}(DTO 리스트) — SXSSF 스트리밍(대용량 안전)</li>
 *   <li>업로드 파싱: {@link #uploadExcel}(헤더명 key) / {@link #uploadExcel(InputStream, List)}(컬럼 순서→key) / {@link #readRows}(원시 2차원)</li>
 * </ul>
 * 업로드 파싱은 {@code WorkbookFactory} 를 써서 <b>.xls(97-2003) 와 .xlsx 를 모두</b> 읽는다(JXL 대체).
 */
public class ExcelUtil {

    private ExcelUtil() {
        // 인스턴스화 방지
    }

    /**
     * Map 리스트 데이터 -> 엑셀 다운로드
     * @param headerMap  엑셀에 그릴 Key와 한글 헤더 매핑 데이터 (반드시 순서가 보장되는 LinkedHashMap 권장)
     * 예: { "userId" : "사용자 ID", "userNm" : "회원명" }
     * @param dataList   DB에서 뽑아온 List<Map<String, Object>> 결과물
     */
    public static void downloadExcelMap(HttpServletResponse response, Map<String, String> headerMap, List<Map<String, Object>> dataList, String fileName) throws IOException {
        if (headerMap == null || headerMap.isEmpty()) throw new IllegalArgumentException("엑셀 헤더 설정이 비어있습니다.");
        if (dataList == null) dataList = new ArrayList<>();

        List<String> bodyKeys = new ArrayList<>(headerMap.keySet());    // 데이터 매핑용 Key 리스트 (eg. userId)
        List<String> headerNames = new ArrayList<>(headerMap.values()); // 엑셀 타이틀 표출용 명칭 (eg. 사용자 ID)

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet("Data List");
            sheet.trackAllColumnsForAutoSizing();

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle bodyStyle = createBodyStyle(workbook);

            // 1. 헤더 타이틀 로우 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headerNames.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headerNames.get(i));
                cell.setCellStyle(headerStyle);
            }

            // 2. 데이터 본문 채우기
            int rowIdx = 1;
            for (Map<String, Object> data : dataList) {
                Row bodyRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < bodyKeys.size(); i++) {
                    Cell cell = bodyRow.createCell(i);
                    cell.setCellStyle(bodyStyle);

                    Object value = data.get(bodyKeys.get(i));
                    setCellValueByType(cell, value);
                }
            }

            // 열 너비 자동 조절
            for (int i = 0; i < headerNames.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // 3. 응답 스트림 출력
            flushExcelResponse(response, workbook, fileName);
        }
    }

    /**
     * DTO 리스트 데이터 -> 엑셀 다운로드
     *
     * @param headerMap 매핑할 DTO 필드명과 엑셀 표출 헤더명 (LinkedHashMap)
     *                  예: { "studentId" : "학생일련번호", "email" : "이메일주소" }
     */
    public static <T> void downloadExcelDto(HttpServletResponse response, Map<String, String> headerMap, List<T> dataList, Class<T> clazz, String fileName) throws IOException {
        if (headerMap == null || headerMap.isEmpty()) throw new IllegalArgumentException("엑셀 헤더 설정이 비어있습니다.");
        if (dataList == null) dataList = new ArrayList<>();

        List<String> bodyFields = new ArrayList<>(headerMap.keySet());
        List<String> headerNames = new ArrayList<>(headerMap.values());

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet("Data List");
            sheet.trackAllColumnsForAutoSizing();

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle bodyStyle = createBodyStyle(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headerNames.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headerNames.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (T data : dataList) {
                Row bodyRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < bodyFields.size(); i++) {
                    Cell cell = bodyRow.createCell(i);
                    cell.setCellStyle(bodyStyle);

                    try {
                        Field field = clazz.getDeclaredField(bodyFields.get(i));
                        field.setAccessible(true);
                        Object value = field.get(data);
                        setCellValueByType(cell, value);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        cell.setCellValue(""); // 없는 필드를 주입하려 했을 때 방어
                    }
                }
            }

            for (int i = 0; i < headerNames.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            flushExcelResponse(response, workbook, fileName);
        }
    }

    /**
     * 업로드된 엑셀 파일(.xls/.xlsx)을 파싱하여 Map 리스트로 반환.
     * 첫 행을 헤더로 보고, 각 데이터 행을 <b>헤더 텍스트를 key</b> 로 하는 Map 으로 만든다.
     * (헤더 텍스트가 아니라 정해진 컬럼 순서로 매핑하려면 {@link #uploadExcel(InputStream, List)} 사용)
     */
    public static List<Map<String, String>> uploadExcel(InputStream inputStream) throws IOException {
        List<Map<String, String>> excelData = new ArrayList<>();

        // WorkbookFactory: .xls(HSSF)/.xlsx(XSSF) 자동 판별
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount <= 1) return excelData;

            Row headerRow = sheet.getRow(0);
            int cellCount = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < cellCount; i++) {
                headers.add(getCellValueAsString(headerRow.getCell(i)).trim());
            }

            for (int r = 1; r < rowCount; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Map<String, String> rowMap = new LinkedHashMap<>();
                boolean hasData = false;

                for (int c = 0; c < cellCount; c++) {
                    String headerName = headers.get(c);
                    String cellValue = getCellValueAsString(row.getCell(c));

                    if (!cellValue.isEmpty()) hasData = true;
                    rowMap.put(headerName, cellValue);
                }

                if (hasData) {
                    excelData.add(rowMap);
                }
            }
        }
        return excelData;
    }

    /**
     * 업로드된 엑셀(.xls/.xlsx)을 <b>지정한 컬럼 key 순서</b>로 파싱하여 Map 리스트로 반환.
     * 첫 행(헤더)은 건너뛰고, 각 데이터 행을 columnKeys 순서(왼쪽 컬럼부터)대로 매핑한다.
     * 헤더 텍스트가 바뀌어도 안전한, 고정 양식 업로드에 사용.
     * <pre>uploadExcel(is, List.of("userId", "email", "name"))
     *   → A열=userId, B열=email, C열=name</pre>
     *
     * @param columnKeys 컬럼(왼쪽부터) → map key
     */
    public static List<Map<String, String>> uploadExcel(InputStream inputStream, List<String> columnKeys) throws IOException {
        List<Map<String, String>> excelData = new ArrayList<>();
        if (columnKeys == null || columnKeys.isEmpty()) return excelData;

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 0행(헤더) 건너뛰고 데이터 행부터
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Map<String, String> rowMap = new LinkedHashMap<>();
                boolean hasData = false;
                for (int c = 0; c < columnKeys.size(); c++) {
                    String cellValue = getCellValueAsString(row.getCell(c));
                    if (!cellValue.isEmpty()) hasData = true;
                    rowMap.put(columnKeys.get(c), cellValue);
                }

                if (hasData) excelData.add(rowMap);
            }
        }
        return excelData;
    }

    /**
     * 업로드된 엑셀(.xls/.xlsx)을 원시 문자열 2차원 리스트로 파싱 (헤더 포함, 모든 행).
     * 컬럼 매핑 없이 직접 검증/배치 처리할 때 사용. 빈 행은 빈 리스트로 유지되어 행 번호가 보존된다.
     */
    public static List<List<String>> readRows(InputStream inputStream) throws IOException {
        List<List<String>> rows = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    rows.add(new ArrayList<>());
                    continue;
                }

                List<String> cells = new ArrayList<>();
                int lastCell = row.getLastCellNum(); // -1 이면 빈 행
                for (int c = 0; c < lastCell; c++) {
                    cells.add(getCellValueAsString(row.getCell(c)));
                }
                rows.add(cells);
            }
        }
        return rows;
    }

    /* ── 공통 컴포넌트 ── */

    private static void setCellValueByType(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static void flushExcelResponse(HttpServletResponse response, SXSSFWorkbook workbook, String fileName) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + ".xlsx\"");

        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
    }

    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static CellStyle createBodyStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
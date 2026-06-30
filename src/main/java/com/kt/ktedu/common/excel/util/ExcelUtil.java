package com.kt.ktedu.common.excel.util;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
     * 업로드된 엑셀 파일(InputStream)을 파싱하여 Map 리스트로 반환
     */
    public static List<Map<String, String>> uploadExcel(InputStream inputStream) throws IOException {
        List<Map<String, String>> excelData = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
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
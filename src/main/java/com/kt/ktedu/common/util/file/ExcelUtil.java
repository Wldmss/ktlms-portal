package com.kt.ktedu.common.util.file;

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
     * 데이터 리스트를 받아 대용량 엑셀 파일로 클라이언트에게 즉시 다운로드
     * (SXSSFWorkbook 적용으로 수십만 건 데이터도 서버 메모리 방어선 작동)
     */
    public static <T> void downloadExcel(HttpServletResponse response, List<T> dataList, Class<T> clazz, String fileName) throws IOException {
        if (dataList == null) dataList = new ArrayList<>();

        // 1. 리플렉션을 이용해 @ExcelColumn 설정이 된 필드들을 순서대로 추출
        List<Field> excelFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                .sorted(Comparator.comparingInt(field -> field.getAnnotation(ExcelColumn.class).order()))
                .toList();

        // 2. 대용량용 SXSSF 워크북 생성 (메모리에 100줄만 들고 있고 나머지는 디스크 임시파일 처리)
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet("Data List");
            sheet.trackAllColumnsForAutoSizing(); // 자크 자동 조절 활성화

            // 헤더 스타일 정의 (연한 회색 배경, 굵은 글씨, 테두리선)
            CellStyle headerStyle = createHeaderStyle(workbook);
            // 본문 스타일 정의 (테두리선, 좌측 정렬)
            CellStyle bodyStyle = createBodyStyle(workbook);

            // 3. 타이틀 헤더 로우 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < excelFields.size(); i++) {
                Cell cell = headerRow.createCell(i);
                ExcelColumn anno = excelFields.get(i).getAnnotation(ExcelColumn.class);
                cell.setCellValue(anno.headerName());
                cell.setCellStyle(headerStyle);
            }

            // 4. 데이터 로우 채우기
            int rowIdx = 1;
            for (T data : dataList) {
                Row bodyRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < excelFields.size(); i++) {
                    Cell cell = bodyRow.createCell(i);
                    cell.setCellStyle(bodyStyle);

                    Field field = excelFields.get(i);
                    field.setAccessible(true);
                    try {
                        Object value = field.get(data);
                        if (value != null) {
                            if (value instanceof Number) {
                                cell.setCellValue(((Number) value).doubleValue());
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                    } catch (IllegalAccessException e) {
                        cell.setCellValue("");
                    }
                }
            }

            // 열 너비 자동 조절
            for (int i = 0; i < excelFields.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // 5. 브라우저 응답 헤더 세팅 및 다운로드 스트림 출력
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + ".xlsx\"");

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    /**
     * 업로드된 엑셀 파일(InputStream)을 파싱하여 Map 리스트로 반환
     */
    public static List<Map<String, String>> uploadExcel(InputStream inputStream) throws IOException {
        List<Map<String, String>> excelData = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 고정
            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount <= 1) return excelData; // 헤더 빼고 데이터가 없으면 즉시 반환

            // 1. 첫 번째 줄(헤더) 읽어서 Key 값 매핑 정의
            Row headerRow = sheet.getRow(0);
            int cellCount = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < cellCount; i++) {
                headers.add(getCellValueAsString(headerRow.getCell(i)).trim());
            }

            // 2. 두 번째 줄부터 데이터 셀 추출해서 Map에 담기
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

                // 빈 줄이 아니라면 리스트에 적재
                if (hasData) {
                    excelData.add(rowMap);
                }
            }
        }
        return excelData;
    }

    /* ── 내부 스타일 지원 헬퍼 메서드 ── */
    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
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
        return style;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // 소수점 절삭 처리 필요 시 보정 가능 (기본 문자열 처리)
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
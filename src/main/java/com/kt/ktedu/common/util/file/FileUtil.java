package com.kt.ktedu.common.util.file;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    private static final String UPLOAD_DIR = System.getProperty("user.home") + File.separator + "ktlms_uploads" + File.separator;

    // 업로드 허용 확장자 화이트리스트
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "zip");

    // 업로드 허용 MIME Type 화이트리스트 (웹쉘 jsp, php, exe 등 원천 차단)
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "application/pdf",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "text/plain", "application/zip", "application/x-zip-compressed"
    );

    private FileUtil() {
        // 인스턴스화 방지
    }

    /**
     * 단일 파일 업로드
     */
    public static FileDTO uploadFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // 파일 확장자 및 MIME Type 이중 화이트리스트 체크
        if (!isAllowedFile(multipartFile)) {
            throw new SecurityException("업로드가 허용되지 않는 파일 형식입니다.");
        }

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일명 취약점 방어 (경로 조작 문자 제거 및 순수 파일명 추출)
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName != null) {
            originalFileName = new File(originalFileName).getName();
        } else {
            originalFileName = "unknown_" + System.currentTimeMillis();
        }

        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        String fullPath = UPLOAD_DIR + storedFileName;

        File targetFile = new File(fullPath);
        multipartFile.transferTo(targetFile);

        return FileDTO.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath(fullPath)
                .fileSize(multipartFile.getSize())
                .build();
    }

    /**
     * 파일 다운로드
     */
    public static void downloadFile(String originalFileName, String storedFileName, HttpServletResponse response) throws IOException {
        // 다운로드 파일명 경로 조작 공격(../../ 방어)
        if (storedFileName.contains("..") || storedFileName.contains("/") || storedFileName.contains("\\")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        File file = new File(UPLOAD_DIR + storedFileName);
        if (!file.exists() || !file.isFile()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setContentLengthLong(file.length());

        try (InputStream in = new BufferedInputStream(new FileInputStream(file));
             OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            FileCopyUtils.copy(in, out);
            out.flush();
        }
    }

    /**
     * 다중 파일 ZIP 압축 다운로드
     */
    public static void downloadZipFile(String[] originalNames, String[] storedNames, String zipFileName, HttpServletResponse response) throws IOException {
        if (originalNames == null || storedNames == null || originalNames.length != storedNames.length) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String encodedZipName = URLEncoder.encode(zipFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedZipName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {
            byte[] buffer = new byte[4096];

            for (int i = 0; i < storedNames.length; i++) {
                // 경로 조작 검증
                if (storedNames[i].contains("..") || storedNames[i].contains("/") || storedNames[i].contains("\\")) {
                    continue;
                }

                File file = new File(UPLOAD_DIR + storedNames[i]);
                if (file.exists() && file.isFile()) {
                    ZipEntry zipEntry = new ZipEntry(originalNames[i]);
                    zos.putNextEntry(zipEntry);

                    try (InputStream fis = new BufferedInputStream(new FileInputStream(file))) {
                        int readBytes;
                        while ((readBytes = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, readBytes);
                        }
                    }
                    zos.closeEntry();
                }
            }
            zos.flush();
        }
    }

    /**
     * 파일 사이즈 체크 연산
     */
    public static long checkFileSize(String[] storedNames) {
        if (storedNames == null || storedNames.length == 0) {
            return 0L;
        }

        // 단일 파일
        if (storedNames.length == 1) {
            File file = new File(UPLOAD_DIR + storedNames[0]);
            return file.exists() ? file.length() : 0L;
        }

        // 파일 여러개
        long estimatedZipSize = 0L;
        for (String storedName : storedNames) {
            if (storedName.contains("..")) continue; // 보안 방어

            File file = new File(UPLOAD_DIR + storedName);
            if (file.exists() && file.isFile()) {
                // ZIP 파일 내부에 들어갈 파일 기본 헤더 용량 (약 30~46 바이트)
                estimatedZipSize += 46;
                estimatedZipSize += file.getName().getBytes(StandardCharsets.UTF_8).length;
                estimatedZipSize += file.length();
            }
        }

        // ZIP 파일 맨 뒤에 붙는 중앙 디렉토리 종단 레코드(EOD) 용량 추가 (약 22 바이트)
        estimatedZipSize += 22;
        return estimatedZipSize;
    }

    /**
     * 확장자 및 진짜 파일 MIME Type 검증
     */
    private static boolean isAllowedFile(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            return false;
        }

        // 1. 확장자 체크
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            return false;
        }

        // 2. MIME Type 체크 (변조된 파일 완벽 검출)
        String contentType = multipartFile.getContentType();
        return contentType != null && ALLOWED_MIME_TYPES.contains(contentType.toLowerCase());
    }
}
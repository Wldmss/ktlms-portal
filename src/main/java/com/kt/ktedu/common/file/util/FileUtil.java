package com.kt.ktedu.common.file.util;

import com.kt.ktedu.common.file.dto.FileDTO;
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

/**
 * 파일 업로드/다운로드/zip 압축다운로드/용량계산
 */
public class FileUtil {

    // 호출부가 어떤 allowedExtensions 를 넘기든 항상 차단하는 확장자 (웹쉘 등 원천 차단, 최후 방어선)
    private static final List<String> ALWAYS_BLOCKED_EXTENSIONS = Arrays.asList(
            "jsp", "jspx", "jspf", "php", "php3", "php4", "php5", "phtml",
            "asp", "aspx", "exe", "sh", "bat", "cmd", "war", "jar", "class", "htaccess"
    );

    private FileUtil() {
        // 인스턴스화 방지
    }

    /**
     * 단일 파일 업로드
     *
     * @param uploadPath        저장 디렉토리 (호출부에서 결정)
     * @param multipartFile     업로드 파일
     * @param allowedExtensions 허용 확장자 목록 (null/empty 면 ALWAYS_BLOCKED_EXTENSIONS 외에는 제한 없음)
     */
    public static FileDTO uploadFile(String uploadPath, MultipartFile multipartFile, List<String> allowedExtensions) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // 파일명 취약점 방어 (경로 조작 문자 제거 및 순수 파일명 추출)
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName != null) {
            originalFileName = new File(originalFileName).getName();
        } else {
            originalFileName = "unknown_" + System.currentTimeMillis();
        }

        String ext = extensionOf(originalFileName);
        if (ALWAYS_BLOCKED_EXTENSIONS.contains(ext)) {
            throw new SecurityException("업로드가 허용되지 않는 파일 형식입니다.");
        }
        if (allowedExtensions != null && !allowedExtensions.isEmpty() && !allowedExtensions.contains(ext)) {
            throw new SecurityException("업로드가 허용되지 않는 파일 형식입니다.");
        }

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        File targetFile = new File(dir, storedFileName);
        multipartFile.transferTo(targetFile);

        return FileDTO.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath(targetFile.getAbsolutePath())
                .fileSize(multipartFile.getSize())
                .build();
    }

    /**
     * 파일 다운로드
     *
     * @param downloadPath 저장 디렉토리 (호출부에서 결정, uploadFile 때 쓴 경로와 짝이 맞아야 함)
     */
    public static void downloadFile(String downloadPath, String originalFileName, String storedFileName, HttpServletResponse response) throws IOException {
        // 다운로드 파일명 경로 조작 공격(../../ 방어)
        if (isPathTraversal(storedFileName)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        File file = new File(downloadPath, storedFileName);
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
     * 다중 파일 ZIP 압축 다운로드 (디스크에 임시 zip 파일을 만들지 않고 response 로 바로 스트리밍)
     *
     * @param downloadPath 저장 디렉토리 (호출부에서 결정)
     */
    public static void downloadZipFile(String downloadPath, String[] originalNames, String[] storedNames, String zipFileName, HttpServletResponse response) throws IOException {
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
                if (isPathTraversal(storedNames[i])) {
                    continue;
                }

                File file = new File(downloadPath, storedNames[i]);
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
     * 파일 사이즈 체크 연산 (webview 앱 호출 시 파일이 너무 커서 크래시 나는 경우 사전 방지용)
     *
     * @param downloadPath 저장 디렉토리 (호출부에서 결정)
     */
    public static long checkFileSize(String downloadPath, String[] storedNames) {
        if (storedNames == null || storedNames.length == 0) {
            return 0L;
        }

        // 단일 파일
        if (storedNames.length == 1) {
            if (isPathTraversal(storedNames[0])) return 0L;
            File file = new File(downloadPath, storedNames[0]);
            return file.exists() ? file.length() : 0L;
        }

        // 파일 여러개 (zip 압축시 예상 용량)
        long estimatedZipSize = 0L;
        for (String storedName : storedNames) {
            if (isPathTraversal(storedName)) continue; // 보안 방어

            File file = new File(downloadPath, storedName);
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

    private static boolean isPathTraversal(String name) {
        return name == null || name.contains("..") || name.contains("/") || name.contains("\\");
    }

    private static String extensionOf(String fileName) {
        int idx = fileName.lastIndexOf(".");
        return idx == -1 ? "" : fileName.substring(idx + 1).toLowerCase();
    }
}
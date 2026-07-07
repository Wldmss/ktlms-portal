package com.kt.ktedu.common.file.util;

import com.kt.ktedu.common.file.dto.FileDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
     * 단일 파일 업로드 (용량 제한 없음)
     *
     * @param uploadPath        저장 디렉토리 (호출부에서 결정)
     * @param multipartFile     업로드 파일
     * @param allowedExtensions 허용 확장자 목록 (null/empty 면 ALWAYS_BLOCKED_EXTENSIONS 외에는 제한 없음)
     */
    public static FileDTO uploadFile(String uploadPath, MultipartFile multipartFile, List<String> allowedExtensions) throws IOException {
        return uploadFile(uploadPath, multipartFile, allowedExtensions, 0L);
    }

    /**
     * 단일 파일 업로드 (용량 제한 포함)
     *
     * @param uploadPath        저장 디렉토리 (호출부에서 결정)
     * @param multipartFile     업로드 파일
     * @param allowedExtensions 허용 확장자 목록 (null/empty 면 ALWAYS_BLOCKED_EXTENSIONS 외에는 제한 없음)
     * @param maxBytes          허용 최대 바이트 (0 이하면 제한 없음)
     * @throws SecurityException 확장자 미허용/차단, 또는 용량 초과 시
     */
    public static FileDTO uploadFile(String uploadPath, MultipartFile multipartFile, List<String> allowedExtensions, long maxBytes) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // 용량 검증
        if (maxBytes > 0 && multipartFile.getSize() > maxBytes) {
            throw new SecurityException("허용된 파일 용량을 초과했습니다.");
        }

        // 파일명 정규화 (경로 조작 문자/불용 문자 제거)
        String originalFileName = normalizeFileName(multipartFile.getOriginalFilename());

        String ext = getExtension(originalFileName);
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

    /**
     * 저장된 파일 삭제. (파일 수정/삭제 시 첨부 제거용)
     *
     * @param dirPath         저장 디렉토리
     * @param storedFileName  저장 파일명 (path traversal 검증됨)
     * @return 삭제 성공 여부 (파일이 없거나 경로 위반이면 false)
     */
    public static boolean deleteFile(String dirPath, String storedFileName) {
        if (isPathTraversal(storedFileName)) {
            return false;
        }
        File file = new File(dirPath, storedFileName);
        return file.exists() && file.isFile() && file.delete();
    }

    /**
     * 파일 삭제 (예외 무시). 임시 파일 정리 등에 사용.
     *
     * @return 삭제 성공 여부
     */
    public static boolean deleteQuietly(File file) {
        try {
            return file != null && file.exists() && file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 파일 이동. (임시 저장 → 최종 저장 디렉토리 이동 등). 대상 폴더가 없으면 생성, 동일명 존재 시 덮어씀.
     *
     * @param source        원본 파일
     * @param targetDirPath 이동할 디렉토리
     * @return 이동된 파일
     */
    public static File moveFile(File source, String targetDirPath) throws IOException {
        File dir = new File(targetDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File target = new File(dir, source.getName());
        Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return target;
    }

    /**
     * 파일 복사. 대상 폴더가 없으면 생성, 동일명 존재 시 덮어씀.
     *
     * @param source        원본 파일
     * @param targetDirPath 복사할 디렉토리
     * @return 복사된 파일
     */
    public static File copyFile(File source, String targetDirPath) throws IOException {
        File dir = new File(targetDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File target = new File(dir, source.getName());
        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return target;
    }

    /**
     * 파일명 정규화. 경로(디렉토리) 제거 + 파일 시스템 불용 문자/제어 문자 제거.
     * <pre>normalizeFileName("../a/b<c>.pdf") → "bc.pdf"</pre>
     *
     * @return 정규화된 파일명 (null/빈값이면 "unknown_시각")
     */
    public static String normalizeFileName(String fileName) {
        if (fileName == null) {
            return "unknown_" + System.currentTimeMillis();
        }
        // 경로 제거(순수 파일명) 후 불용 문자 제거
        String name = new File(fileName).getName()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\p{Cntrl}", "")
                .trim();
        return name.isEmpty() ? "unknown_" + System.currentTimeMillis() : name;
    }

    /**
     * 확장자 추출 (소문자, 점 제외. 없으면 "")
     * <pre>getExtension("보고서.PDF") → "pdf"</pre>
     */
    public static String getExtension(String fileName) {
        if (fileName == null) return "";
        int idx = fileName.lastIndexOf(".");
        return idx == -1 ? "" : fileName.substring(idx + 1).toLowerCase();
    }

    private static boolean isPathTraversal(String name) {
        return name == null || name.contains("..") || name.contains("/") || name.contains("\\");
    }
}
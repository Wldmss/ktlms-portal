package com.kt.ktedu.common.util.file;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    // 💡 실제 운영서버(JBoss) 환경에서는 OS별 절대경로(eg. /app/upload/ktlms/)를 properties에서 땡겨오는 것을 권장합니다.
    private static final String UPLOAD_DIR = System.getProperty("user.home") + File.separator + "ktlms_uploads" + File.separator;

    /**
     * 📤 단일 파일 업로드 기능
     */
    public static FileDTO uploadFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // 1. 디렉토리 없으면 자동 생성
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 파일명 중복 방지를 위한 UUID 결합
        String originalFileName = multipartFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        String fullPath = UPLOAD_DIR + storedFileName;

        // 3. 서버 실제 물리 공간에 저장
        File targetFile = new File(fullPath);
        multipartFile.transferTo(targetFile);

        // 4. 저장된 정보 DTO 바구니에 담아서 반환
        return FileDTO.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath(fullPath)
                .fileSize(multipartFile.getSize())
                .build();
    }

    /**
     * 📥 파일 다운로드 기능 (브라우저로 스트림 전송)
     */
    public static void downloadFile(String originalFileName, String storedFileName, HttpServletResponse response) throws IOException {
        File file = new File(UPLOAD_DIR + storedFileName);

        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 브라우저별 한글 깨짐 방지 인코딩
        String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        // HTTP 헤더 세팅 (이 파일은 다운로드 전용 브라우저 창을 띄우라는 선언)
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setContentLengthLong(file.length());

        // 스트림을 이용한 파일 복사 (스프링 유틸 도구 활용으로 자원 누수 완전 차단)
        try (InputStream in = new BufferedInputStream(new FileInputStream(file));
             OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            FileCopyUtils.copy(in, out);
            out.flush();
        }
    }

    /**
     * 🗜️ 다중 파일을 하나의 ZIP 파일로 압축하여 즉시 다운로드 시키는 기능
     *
     * @param originalNames 원본 파일명 배열 (사용자에게 보여줄 이름들 - 예: "보고서.pdf")
     * @param storedNames   서버 저장 파일명 배열 (실제 디스크에 저장된 이름들 - 예: "UUID_보고서.pdf")
     * @param zipFileName   최종 다운로드될 압축파일명 (예: "첨부파일_모음.zip")
     */
    public static void downloadZipFile(String[] originalNames, String[] storedNames, String zipFileName, HttpServletResponse response) throws IOException {

        // 1. 방어 코드: 데이터 규격이 안 맞으면 조기 종료
        if (originalNames == null || storedNames == null || originalNames.length != storedNames.length) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 2. 브라우저별 한글 깨짐 방지 인코딩
        String encodedZipName = java.net.URLEncoder.encode(zipFileName, java.nio.charset.StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        // 3. HTTP 다운로드 헤더 세팅
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedZipName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        // 4. 리소스를 안전하게 닫기 위해 try-with-resources 사용
        // response 출력 스트림을 ZipOutputStream이 그대로 물고 실시간 압축 전송합니다.
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()))) {

            byte[] buffer = new byte[4096]; // 4KB 버퍼 양동이

            for (int i = 0; i < storedNames.length; i++) {
                File file = new File(UPLOAD_DIR + storedNames[i]);

                // 파일이 진짜 존재할 때만 압축 주머니에 집어넣음
                if (file.exists() && file.isFile()) {

                    // ZIP 파일 내부 구조에 "원본 파일명"으로 방을 만듭니다.
                    ZipEntry zipEntry = new ZipEntry(originalNames[i]);
                    zos.putNextEntry(zipEntry);

                    // 물리 파일을 읽어서 ZIP 스트림으로 쏟아붓기
                    try (InputStream fis = new BufferedInputStream(new FileInputStream(file))) {
                        int readBytes;
                        while ((readBytes = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, readBytes);
                        }
                    }

                    zos.closeEntry(); // 하나의 파일 압축 완료 및 방 닫기
                }
            }
            zos.flush();
        }
    }

    @GetMapping("/check-size")
    @ResponseBody
    public long checkFileSize(@RequestParam(value = "saveNames", required = false) String[] storedNames) {
        if (storedNames == null || storedNames.length == 0) {
            return 0L;
        }

        // 파일이 1개일 때는 그냥 그 파일의 물리 크기를 리턴
        if (storedNames.length == 1) {
            String uploadDir = System.getProperty("user.home") + File.separator + "ktlms_uploads" + File.separator;
            File file = new File(uploadDir + storedNames[0]);
            return file.exists() ? file.length() : 0L;
        }

        // 🔥 파일이 여러 개(ZIP)일 때는 '가짜 압축 스트림'으로 크기 예측
        long estimatedZipSize = 0L;
        String uploadDir = System.getProperty("user.home") + File.separator + "ktlms_uploads" + File.separator;

        for (String storedName : storedNames) {
            File file = new File(uploadDir + storedName);
            if (file.exists() && file.isFile()) {

                // 1. ZIP 파일 내부에 들어갈 파일 기본 헤더 용량 (약 30~46 바이트)
                estimatedZipSize += 46;

                // 2. 파일명 길이만큼 늘어나는 용량 반영
                estimatedZipSize += file.getName().getBytes().length;

                // 3. [핵심] 데이터 용량 반영
                // 이미지나 영상, PDF 등은 압축을 해도 용량이 거의 안 줄어들기 때문에
                // 실무 보안상 안전하게 원본 크기를 그대로 더해주는 것이 정확합니다.
                estimatedZipSize += file.length();
            }
        }

        // 4. ZIP 파일 맨 뒤에 붙는 중앙 디렉토리 종단 레코드(EOD) 용량 추가 (약 22 바이트)
        estimatedZipSize += 22;

        return estimatedZipSize;
    }
}
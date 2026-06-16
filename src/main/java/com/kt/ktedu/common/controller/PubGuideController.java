package com.kt.ktedu.common.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class PubGuideController {

    @GetMapping("/pub-guide")
    public String getPublishList(HttpServletRequest request, Model model) {
        String rootPath = request.getServletContext().getRealPath("/resources/pub");

        List<Map<String, String>> htmlFiles = new ArrayList<>();
        File dir = new File(rootPath);

        if (dir.exists() && dir.isDirectory()) {
            searchHtmlFiles(dir, "", htmlFiles);
        }

        model.addAttribute("fileList", htmlFiles);
        return "common/pages/pubGuide";
    }

    private void searchHtmlFiles(File folder, String relativePath, List<Map<String, String>> htmlFiles) {
        File[] files = folder.listFiles();
        if (files == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (File file : files) {
            if (file.isDirectory()) {
                searchHtmlFiles(file, relativePath + file.getName() + "/", htmlFiles);
            } else if (file.getName().endsWith(".html")) {

                Map<String, String> fileInfo = new HashMap<>();
                fileInfo.put("path", relativePath + file.getName());

                // 1. 자동으로 파일 수정 시간 구하기
                String lastModified = sdf.format(file.lastModified());

                // 2. 자동으로 HTML 안의 <title> 내용 훔쳐오기
                String htmlTitle = extractHtmlTitle(file);

                // 3. 비고란에 넣을 텍스트 조립
                String remarks = htmlTitle + " (업데이트: " + lastModified + ")";
                fileInfo.put("remarks", remarks);

                htmlFiles.add(fileInfo);
            }
        }
    }

    // HTML 파일 열어서 <title> 태그 내용만 쏙 뽑아내는 자바 정규식
    private String extractHtmlTitle(File file) {
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            Pattern pattern = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (IOException e) {
            return "이름 없음";
        }
        return "이름 없음";
    }
}
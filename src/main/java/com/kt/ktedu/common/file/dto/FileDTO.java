package com.kt.ktedu.common.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class FileDTO {
    private final String originalFileName; // 사용자가 올린 원래 파일명 (예: 보고서.pdf)
    private final String storedFileName;   // 서버에 중복 방지용으로 저장된 파일명 (UUID_보고서.pdf)
    private final String filePath;         // 서버 물리 저장 경로
    private final long fileSize;           // 파일 크기 (Byte)
}
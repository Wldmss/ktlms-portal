package com.kt.ktedu.common.crypto.controller;

import com.kt.ktedu.common.common.dto.ResponseDTO;
import com.kt.ktedu.common.crypto.service.RsaKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/key")
public class RSAController {

    private final RsaKeyService rsaKeyService;

    /**
     * RSA 키 쌍 N개 생성 후 DB 저장
     */
    @GetMapping("/generate/{count}")
    public ResponseEntity<?> generateRsaKey(@PathVariable(name = "count") Integer count) {
        try {
            int totalCount = rsaKeyService.generateAndSave(count);
            return ResponseEntity.ok(ResponseDTO.success(totalCount + "건 생성 완료"));
        } catch (Exception e) {
            log.error("RSA 키 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.fail("키 생성 실패: " + e.getMessage()));
        }
    }

    /**
     * RSA Public Key 교환
     */
    @PostMapping("/public")
    public ResponseEntity<?> getPublicKey(@RequestBody Map<String, String> request) {
        try {
            String encryptedKeySeq = request.get("keySeq");
            String publicKey = rsaKeyService.getPublicKey(encryptedKeySeq);
            return ResponseEntity.ok(publicKey);
        } catch (Exception e) {
            log.error("공개키 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

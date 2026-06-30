package com.kt.ktedu.common.crypto.controller;

import com.kt.ktedu.common.common.dto.ResponseDTO;
import com.kt.ktedu.common.crypto.service.RsaKeyService;
import com.kt.ktedu.core.exception.ApiException;
import com.kt.ktedu.core.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 예외는 GlobalExceptionHandler 가 공통으로 처리 (ApiException → ResponseDTO.fail)
     */
    @GetMapping("/generate/{count}")
    public ResponseDTO generateRsaKey(@PathVariable Integer count) {
        try {
            int totalCount = rsaKeyService.generateAndSave(count);
            return ResponseDTO.success(totalCount + "건 생성 완료", null);
        } catch (ApiException e) {
            throw e; // 이미 ApiException 이면 그대로 전파
        } catch (Exception e) {
            log.error("RSA 키 생성 실패", e);
            throw new ApiException("키 생성 실패: " + e.getMessage(), ErrorMessage.API_ERROR);
        }
    }

    /**
     * RSA Public Key 교환
     */
    @PostMapping("/public")
    public ResponseDTO getPublicKey(@RequestBody Map<String, String> request) {
        try {
            String encryptedKeySeq = request.get("keySeq");
            String publicKey = rsaKeyService.getPublicKey(encryptedKeySeq);
            return ResponseDTO.success("조회 성공", publicKey);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("공개키 조회 실패", e);
            throw new ApiException("공개키 조회 실패: " + e.getMessage(), ErrorMessage.API_ERROR);
        }
    }
}

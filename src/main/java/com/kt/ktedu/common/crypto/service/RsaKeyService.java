package com.kt.ktedu.common.crypto.service;

import com.kt.ktedu.common.crypto.dto.RsaKeyDTO;
import com.kt.ktedu.common.crypto.mapper.RsaKeyMapper;
import com.kt.ktedu.common.crypto.util.GCMUtil;
import com.kt.ktedu.common.crypto.util.RSAUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * RSA 키 DB 관리 + 타임스탬프 검증 서비스
 * <p>
 * exam 의 RsaKeyService 와 역할 차이:
 * - 순수 암호화 연산은 RSAUtil / GCMUtil (static) 로 위임
 * - JPA Repository → MyBatis Mapper 로 교체
 * - 이 클래스는 "DB 접근 + 비즈니스 검증" 만 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RsaKeyService {

    // TODO: application.properties 에 값 설정 필요 (exam 의 kt-genius.jwt.secret-key 대응)
    @Value("${crypto.rsa.secret-key:KT_GCM_SECURE_KEY_HAVE_32_BYTES_}")
    private String secretKey;

    private final RsaKeyMapper rsaKeyMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** RSA 키 쌍 N개 생성 후 DB 저장 (100개 초과 시 100개 단위 배치 저장) */
    public int generateAndSave(int count) throws Exception {
        boolean over100 = count > 100;

        List<RsaKeyDTO> keyList = new ArrayList<>();
        int totalCount = 0;

        for (int i = 1; i <= count; i++) {
            KeyPair keyPair = RSAUtil.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String encryptedPrivateKeyString = RSAUtil.encryptPrivateKey(secretKey, privateKey);

            keyList.add(RsaKeyDTO.builder()
                    .publicKey(publicKeyString)
                    .privateKey(encryptedPrivateKeyString)
                    .build());

            totalCount++;

            if (over100 && ((i % 100 == 0 && i < count) || i == count)) {
                rsaKeyMapper.insertList(keyList);
                log.info("RSA 키 생성 진행: {}/{} 건 처리 완료", i, count);

                if (i < count) {
                    keyList.clear();
                    Thread.sleep(10000);
                }
            }
        }

        if (!over100) {
            rsaKeyMapper.insertList(keyList);
            log.info("RSA 키 생성 완료: {} 건", count);
        }

        return totalCount;
    }

    /** 공개키 조회 (keySeq 는 GCM Hex 로 암호화되어 전달됨) */
    public String getPublicKey(String encryptedKeySeq) throws Exception {
        Integer keySeq = Integer.valueOf(GCMUtil.decryptHex(secretKey, encryptedKeySeq));

        RsaKeyDTO rsaKey = rsaKeyMapper.findByKeySeq(keySeq);
        if (rsaKey == null) {
            throw new IllegalArgumentException("존재하지 않는 키입니다. keySeq=" + keySeq);
        }

        return GCMUtil.encryptHex(secretKey, rsaKey.getPublicKey());
    }

    /** 개인키 조회 (1분 이내 타임스탬프 검증 포함) */
    public PrivateKey getPrivateKey(String encryptedKeySeq) throws Exception {
        String timeStampSeq = GCMUtil.decryptHex(secretKey, encryptedKeySeq);

        MinuteResult result = isWithinOneMinute(timeStampSeq);
        if (!result.isInTime()) {
            throw new IllegalStateException("유효하지 않은 접근입니다. (시간 초과)");
        }

        RsaKeyDTO rsaKey = rsaKeyMapper.findByKeySeq(Integer.parseInt(result.getValue()));
        if (rsaKey == null) {
            return null;
        }

        return RSAUtil.decryptPrivateKey(secretKey, rsaKey.getPrivateKey());
    }

    /** RSA 로 암호화된 값을 복호화 + 타임스탬프 검증 */
    public String getDecryptValue(String encryptedKeySeq, String encryptedValue) throws Exception {
        PrivateKey privateKey = getPrivateKey(encryptedKeySeq);
        if (privateKey == null) {
            throw new IllegalStateException("개인키를 찾을 수 없습니다.");
        }

        String decryptedValue = RSAUtil.decrypt(encryptedValue, privateKey);

        MinuteResult result = isWithinOneMinute(decryptedValue);
        if (!result.isInTime()) {
            throw new IllegalStateException("유효하지 않은 접근입니다. (시간 초과)");
        }

        return result.getValue();
    }

    /** 앞 14자리(yyyyMMddHHmmss) 가 현재 시각 기준 1분 이내인지 검증 */
    private MinuteResult isWithinOneMinute(String value) {
        String timeStampString = value.substring(0, 14);
        LocalDateTime targetTime = LocalDateTime.parse(timeStampString, FORMATTER);
        LocalDateTime now = LocalDateTime.now();

        long secondsDifference = Duration.between(targetTime, now).getSeconds();

        return MinuteResult.builder()
                .inTime(secondsDifference >= 0 && secondsDifference < 60)
                .value(value.substring(14))
                .build();
    }

    @Builder
    @Getter
    @Setter
    private static class MinuteResult {
        private boolean inTime;
        private String  value;
    }
}

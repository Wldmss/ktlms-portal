package com.kt.ktedu.common.crypto.util;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * RSA 암호화 유틸 (정적 메서드 제공)
 * <p>
 * 순수 암호화 연산만 담당 — DB 접근, 타임스탬프 검증은 Service 레이어에서 처리
 * → RSAUtil.encryptPrivateKey(secretKey, privateKey) 형태로 바로 사용 가능
 */
@Component
public class RSAUtil {

    private static final RSACore rsaCore = new RSACore();
    private static final GCMCore gcmCore = new GCMCore();

    // ── RSA 암복호화 ─────────────────────────────────────────

    /**
     * 공개키로 RSA 암호화 → Base64
     */
    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        return rsaCore.encrypt(plainText, publicKey);
    }

    /**
     * 개인키로 RSA 복호화 ← Base64
     */
    public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        return rsaCore.decrypt(cipherText, privateKey);
    }

    // ── RSA 키 쌍 생성 ───────────────────────────────────────

    public static KeyPair generateKeyPair() throws Exception {
        return rsaCore.generateKeyPair();
    }

    // ── 개인키 AES-GCM 래핑 / 언래핑 ────────────────────────

    /**
     * 개인키 → AES-GCM 암호화 → Base64 (DB 저장용)
     */
    public static String encryptPrivateKey(String secretKey, PrivateKey privateKey) throws Exception {
        return gcmCore.encryptBytes(privateKey.getEncoded(), secretKey);
    }

    /**
     * Base64 → AES-GCM 복호화 → PrivateKey 복원
     */
    public static PrivateKey decryptPrivateKey(String secretKey, String encryptedPrivateKey) throws Exception {
        byte[] keyBytes = gcmCore.decryptBytes(encryptedPrivateKey, secretKey);
        return rsaCore.toPrivateKey(keyBytes);
    }

    // ── 키 직렬화 / 역직렬화 ─────────────────────────────────

    public static PublicKey toPublicKey(String base64) throws Exception {
        return rsaCore.toPublicKey(base64);
    }

    /**
     * Base64(PKCS8 DER) 문자열 → PrivateKey 복원 (GCM 래핑 없이 평문 저장된 키용)
     */
    public static PrivateKey toPrivateKey(String base64) throws Exception {
        return rsaCore.toPrivateKey(Base64.getDecoder().decode(base64));
    }

    public static String privateKeyToBase64(PrivateKey privateKey) {
        return rsaCore.privateKeyToBase64(privateKey);
    }
}

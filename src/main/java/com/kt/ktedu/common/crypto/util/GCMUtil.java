package com.kt.ktedu.common.crypto.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AES-256-GCM 암호화 유틸 (정적 메서드 제공)
 * <p>
 * Spring Bean 으로 등록되어 @Value 주입 후 static 필드에 복사
 * → GCMUtil.encryptHex(key, data) 형태로 바로 사용 가능
 * <p>
 * 인코딩 방식:
 * - Base64 : encrypt / decrypt
 * - Hex    : encryptHex / decryptHex
 * - Bytes  : encryptBytes / decryptBytes (개인키 등 바이너리)
 */
@Component
public class GCMUtil implements InitializingBean {

    private static final GCMCore core = new GCMCore();

    @Value("${app.enc-key}")
    private String defaultKeyValue;  // 32자

    private static String DEFAULT_KEY;

    @Override
    public void afterPropertiesSet() {
        DEFAULT_KEY = defaultKeyValue;
    }

    // ── Base64 ──────────────────────────────────────────────

    public static String encrypt(String plainText) throws Exception {
        return core.encrypt(plainText, DEFAULT_KEY);
    }

    public static String decrypt(String cipherText) throws Exception {
        return core.decrypt(cipherText, DEFAULT_KEY);
    }

    public static String encrypt(String key, String plainText) throws Exception {
        return core.encrypt(plainText, key);
    }

    public static String decrypt(String key, String cipherText) throws Exception {
        return core.decrypt(cipherText, key);
    }

    // ── Hex ─────────────────────────────────────────────────

    public static String encryptHex(String plainText) throws Exception {
        return core.encryptHex(plainText, DEFAULT_KEY);
    }

    public static String decryptHex(String cipherText) throws Exception {
        return core.decryptHex(cipherText, DEFAULT_KEY);
    }

    public static String encryptHex(String key, String plainText) throws Exception {
        return core.encryptHex(plainText, key);
    }

    public static String decryptHex(String key, String cipherText) throws Exception {
        return core.decryptHex(cipherText, key);
    }

    // ── Bytes ────────────────────────────────────────────────

    public static String encryptBytes(byte[] data) throws Exception {
        return core.encryptBytes(data, DEFAULT_KEY);
    }

    public static byte[] decryptBytes(String cipherText) throws Exception {
        return core.decryptBytes(cipherText, DEFAULT_KEY);
    }

    public static String encryptBytes(String key, byte[] data) throws Exception {
        return core.encryptBytes(data, key);
    }

    public static byte[] decryptBytes(String key, String cipherText) throws Exception {
        return core.decryptBytes(cipherText, key);
    }
}

package com.kt.ktedu.common.crypto.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AES-256-CBC 암호화 유틸 (정적 메서드 제공)
 * <p>
 * Spring Bean 으로 등록되어 @Value 주입 후 static 필드에 복사
 * → AES256Util.encryptLdap(key, data) 형태로 바로 사용 가능
 * <p>
 * IV 전략:
 * - 기본       : 명시적 key + IV
 * - Genius     : IV = key 앞 16자
 * - LDAP       : IV = zero bytes
 * - PrivateKey : 바이너리 → Base64
 */
@Component
public class AES256Util implements InitializingBean {

    private static final AES256Core core = new AES256Core();

    @Value("${app.enc-key}")
    private String defaultKeyValue;  // 32자

    // TODO
    @Value("${crypto.aes256.default-iv:KT_LMS_INIT_IV_16_}")
    private String defaultIvValue;   // 16자

    private static String DEFAULT_KEY;
    private static String DEFAULT_IV;

    @Override
    public void afterPropertiesSet() {
        DEFAULT_KEY = defaultKeyValue;
        DEFAULT_IV = defaultIvValue;
    }

    // ── 기본 (대량평가) : 명시적 key + IV ─────────────────

    /**
     * Base64 — key, IV 직접 지정
     */
    public static String encrypt(String key, String iv, String plainText) throws Exception {
        return core.encryptBase64(plainText, key, iv);
    }

    public static String decrypt(String key, String iv, String cipherText) throws Exception {
        return core.decryptBase64(cipherText, key, iv);
    }

    /**
     * Base64 — 기본 key/IV 사용
     */
    public static String encrypt(String plainText) throws Exception {
        return core.encryptBase64(plainText, DEFAULT_KEY, DEFAULT_IV);
    }

    public static String decrypt(String cipherText) throws Exception {
        return core.decryptBase64(cipherText, DEFAULT_KEY, DEFAULT_IV);
    }

    // ── Genius : IV = key 앞 16자 (삭제 예정) ─────────────────────────

    public static String encryptGenius(String key, String plainText) throws Exception {
        return core.encryptBase64(plainText, key, key.substring(0, 16));
    }

    public static String decryptGenius(String key, String cipherText) throws Exception {
        return core.decryptBase64(cipherText, key, key.substring(0, 16));
    }

    public static String encryptGeniusHex(String key, String plainText) throws Exception {
        return core.encryptHex(plainText, key, key.substring(0, 16));
    }

    public static String decryptGeniusHex(String key, String cipherText) throws Exception {
        return core.decryptHex(cipherText, key, key.substring(0, 16));
    }

    // ── LDAP : IV = zero bytes ────────────────────────────

    public static String encryptLdap(String key, String plainText) throws Exception {
        return core.encryptBase64ZeroIv(plainText, key);
    }

    public static String decryptLdap(String key, String cipherText) throws Exception {
        return core.decryptBase64ZeroIv(cipherText, key);
    }

    // (삭제 예정)
    public static String encryptLdapHex(String key, String plainText) throws Exception {
        return core.encryptHexZeroIv(plainText, key);
    }

    // (삭제 예정)
    public static String decryptLdapHex(String key, String cipherText) throws Exception {
        return core.decryptHexZeroIv(cipherText, key);
    }

    // ── PrivateKey : 바이너리 → Base64 (Genius IV) ────────

    public static String encryptPrivateKey(String key, byte[] keyBytes) throws Exception {
        return core.encryptBytes(keyBytes, key, key.substring(0, 16));
    }

    public static byte[] decryptPrivateKey(String key, String cipherText) throws Exception {
        return core.decryptBytes(cipherText, key, key.substring(0, 16));
    }
}

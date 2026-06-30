package com.kt.ktedu.common.crypto.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES-256-GCM 순수 암호화 코어
 * IV 는 암호화 시마다 랜덤 생성 후 암호문 앞에 결합 (12 bytes)
 * Spring 의존성 없음
 */
class GCMCore {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_BIT = 128;

    // ── Base64 ──────────────────────────────────────────────

    String encrypt(String plainText, String key) throws Exception {
        byte[] combined = encryptInternal(plainText.getBytes(StandardCharsets.UTF_8), key);
        return Base64.getEncoder().encodeToString(combined);
    }

    String decrypt(String cipherText, String key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = decryptInternal(combined, key);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ── Hex (삭제 예정)─────────────────────────────────────────────────

    String encryptHex(String plainText, String key) throws Exception {
        byte[] combined = encryptInternal(plainText.getBytes(StandardCharsets.UTF_8), key);
        return Hex.encodeHexString(combined);
    }

    String decryptHex(String cipherText, String key) throws Exception {
        byte[] combined = Hex.decodeHex(cipherText.toCharArray());
        byte[] decrypted = decryptInternal(combined, key);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ── Bytes (개인키 등 바이너리) ───────────────────────────

    String encryptBytes(byte[] data, String key) throws Exception {
        byte[] combined = encryptInternal(data, key);
        return Base64.getEncoder().encodeToString(combined);
    }

    byte[] decryptBytes(String cipherText, String key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherText);
        return decryptInternal(combined, key);
    }

    // ── 내부 공통 ────────────────────────────────────────────

    private byte[] encryptInternal(byte[] data, String key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),
                new GCMParameterSpec(GCM_TAG_BIT, iv));

        byte[] encrypted = cipher.doFinal(data);

        // IV(12) + 암호문 결합
        byte[] combined = new byte[GCM_IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, GCM_IV_LENGTH);
        System.arraycopy(encrypted, 0, combined, GCM_IV_LENGTH, encrypted.length);
        return combined;
    }

    private byte[] decryptInternal(byte[] combined, String key) throws Exception {
        byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
        byte[] cipherText = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),
                new GCMParameterSpec(GCM_TAG_BIT, iv));

        return cipher.doFinal(cipherText);
    }
}

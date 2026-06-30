package com.kt.ktedu.common.crypto.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES-256-CBC 순수 암호화 코어
 * key, IV 를 직접 받아 처리 — Spring 의존성 없음
 * IV 전략(Genius/LDAP/기본)은 AES256Util 에서 결정
 */
class AES256Core {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // ── Base64 ──────────────────────────────────────────────

    String encryptBase64(String plainText, String key, String iv) throws Exception {
        byte[] encrypted = doEncrypt(plainText.getBytes(StandardCharsets.UTF_8), key, iv.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    String decryptBase64(String cipherText, String key, String iv) throws Exception {
        byte[] decrypted = doDecrypt(Base64.getDecoder().decode(cipherText), key, iv.getBytes(StandardCharsets.UTF_8));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ── Hex ─────────────────────────────────────────────────

    String encryptHex(String plainText, String key, String iv) throws Exception {
        byte[] encrypted = doEncrypt(plainText.getBytes(StandardCharsets.UTF_8), key, iv.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(encrypted);
    }

    String decryptHex(String cipherText, String key, String iv) throws Exception {
        byte[] decrypted = doDecrypt(Hex.decodeHex(cipherText.toCharArray()), key, iv.getBytes(StandardCharsets.UTF_8));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ── Hex (zero-byte IV 전용 — LDAP) (삭제 예정) ──────────────────────

    String encryptHexZeroIv(String plainText, String key) throws Exception {
        byte[] encrypted = doEncrypt(plainText.getBytes(StandardCharsets.UTF_8), key, new byte[16]);
        return Hex.encodeHexString(encrypted);
    }

    String decryptHexZeroIv(String cipherText, String key) throws Exception {
        byte[] decrypted = doDecrypt(Hex.decodeHex(cipherText.toCharArray()), key, new byte[16]);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ── Base64 (zero-byte IV — LDAP) ────────────────────────

    String encryptBase64ZeroIv(String plainText, String key) throws Exception {
        byte[] encrypted = doEncrypt(plainText.getBytes(StandardCharsets.UTF_8), key, new byte[16]);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    String decryptBase64ZeroIv(String cipherText, String key) throws Exception {
        byte[] decrypted = doDecrypt(Base64.getDecoder().decode(cipherText), key, new byte[16]);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ── Bytes (개인키 등 바이너리 데이터) ───────────────────

    String encryptBytes(byte[] data, String key, String iv) throws Exception {
        byte[] encrypted = doEncrypt(data, key, iv.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    byte[] decryptBytes(String cipherText, String key, String iv) throws Exception {
        return doDecrypt(Base64.getDecoder().decode(cipherText), key, iv.getBytes(StandardCharsets.UTF_8));
    }

    // ── 내부 공통 ────────────────────────────────────────────

    private byte[] doEncrypt(byte[] data, String key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),
                new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }

    private byte[] doDecrypt(byte[] data, String key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),
                new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }
}

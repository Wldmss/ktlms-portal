package com.kt.ktedu.common.crypto.util;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 순수 암호화 코어
 * Spring 의존성 없음
 */
class RSACore {

    private static final String ALGORITHM = "RSA";

    /**
     * RSA 키 쌍 생성 (2048 bit)
     */
    KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    /**
     * 공개키(PublicKey)로 암호화 → Base64
     */
    String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    /**
     * 개인키(PrivateKey)로 복호화 ← Base64
     */
    String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    /**
     * Base64 문자열 → PublicKey 객체
     */
    PublicKey toPublicKey(String base64) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(base64);
        return KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
    }

    /**
     * PrivateKey → Base64 문자열
     */
    String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * byte[] → PrivateKey 객체 (DB 에서 복원할 때)
     */
    PrivateKey toPrivateKey(byte[] keyBytes) throws Exception {
        return KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }
}

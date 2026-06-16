package com.kt.ktedu.util.crypto;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

public class RSACore {
    private static final String ALGORITHM = "RSA";

    // 💡 최초 1회 혹은 주기적으로 KeyPair를 생성할 때 쓰는 메서드 (2048비트 권장)
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(2048);
        return keyPairGen.generateKeyPair();
    }

    // 공개키(PublicKey)로 암호화
    public String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // 개인키(PrivateKey)로 복호화
    public String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = Base64.getDecoder().decode(cipherText);
        return new String(cipher.doFinal(decrypted));
    }
}
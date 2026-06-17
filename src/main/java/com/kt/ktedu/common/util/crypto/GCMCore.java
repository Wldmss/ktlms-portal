package com.kt.ktedu.common.util.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class GCMCore {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    public String encrypt(String plainText, String secretKey) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedBytes);

        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    public String decrypt(String cipherText, String secretKey) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decodedBytes, 0, iv, 0, iv.length);

        int encryptedSize = decodedBytes.length - IV_LENGTH_BYTE;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(decodedBytes, IV_LENGTH_BYTE, encryptedBytes, 0, encryptedSize);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        return new String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8);
    }
}
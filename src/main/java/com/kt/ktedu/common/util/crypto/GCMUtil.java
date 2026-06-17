package com.kt.ktedu.common.util.crypto;

public class GCMUtil {
    private static final GCMCore core = new GCMCore();
    private static final String DEFAULT_KEY = "KT_GCM_SECURE_KEY_HAVE_32_BYTES_"; // 32자

    public static String encrypt(String plainText) throws Exception {
        return core.encrypt(plainText, DEFAULT_KEY);
    }

    public static String decrypt(String cipherText) throws Exception {
        return core.decrypt(cipherText, DEFAULT_KEY);
    }
}
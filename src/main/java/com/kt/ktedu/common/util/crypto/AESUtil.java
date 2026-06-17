package com.kt.ktedu.common.util.crypto;

public class AESUtil {
    private static final AES256Core core = new AES256Core();

    // 💡 KT 프로젝트 공통 고정키 예시 (실무선 properties에서 땡겨오는 것을 추천)
    private static final String DEFAULT_KEY = "KT_LMS_PROJECT_SECRET_KEY_256_BIT"; // 32자
    private static final String DEFAULT_IV = "KT_LMS_INIT_IV_16"; // 16자

    public static String encrypt(String plainText) throws Exception {
        return core.encrypt(plainText, DEFAULT_KEY, DEFAULT_IV);
    }

    public static String decrypt(String cipherText) throws Exception {
        return core.decrypt(cipherText, DEFAULT_KEY, DEFAULT_IV);
    }
}
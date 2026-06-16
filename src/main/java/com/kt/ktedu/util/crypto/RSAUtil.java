package com.kt.ktedu.util.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAUtil {
    private static final RSACore core = new RSACore();

    // 서버 메모리에 상주할 키 쌍 (실무에서는 키를 DB나 파일 시스템에 보관하는 것을 권장)
    private static final KeyPair keyPair;

    static {
        try {
            keyPair = core.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("RSA 키 쌍 초기화 실패", e);
        }
    }

    public static String encrypt(String plainText) throws Exception {
        return core.encrypt(plainText, keyPair.getPublic());
    }

    public static String decrypt(String cipherText) throws Exception {
        return core.decrypt(cipherText, keyPair.getPrivate());
    }

    // 외부에서 특정 공개키가 필요할 때 껍데기 분출용
    public static PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
}
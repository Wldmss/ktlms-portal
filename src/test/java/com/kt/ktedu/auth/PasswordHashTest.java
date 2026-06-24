package com.kt.ktedu.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "admin1234";
        String encoded = encoder.encode(raw);
        System.out.println("=== BCrypt Hash ===");
        System.out.println("raw     : " + raw);
        System.out.println("encoded : " + encoded);
        System.out.println("matches : " + encoder.matches(raw, encoded));

        // 기존 코드에 있던 해시 검증
        String existingHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        System.out.println("기존 해시 매칭 여부 : " + encoder.matches(raw, existingHash));
    }
}

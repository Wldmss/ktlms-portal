package com.kt.ktedu.core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    // firebase 비공개 키 파일
    @Value("classpath:firebase/ktgenius-firebase-adminsdk.json")
    private Resource resource;

    private FirebaseApp firebaseApp;

    /**
     * firebase app 초기화
     */
    @PostConstruct
    public FirebaseApp initFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream(resource.getFile());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options);

            return firebaseApp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * firebase messaging 객체 초기화
     */
    @Bean
    public FirebaseMessaging initFirebaseMessaging() {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
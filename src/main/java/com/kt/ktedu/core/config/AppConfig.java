package com.kt.ktedu.core.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 애플리케이션 시작 전에 로컬 환경변수 파일을 JVM system property로 주입한다.
 *
 * <p>운영/개발 서버는 JBoss/Azure가 제공하는 OS 환경변수를 우선 사용하고,
 * IntelliJ Community + Tomcat 로컬 실행에서
 * {@code -Dspring.profiles.active=local}인 경우에만 프로젝트의 env/common.env와
 * env/local.env를 사용한다.</p>
 */
public final class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
    private static final String ENV_DIRECTORY_PROPERTY = "app.env.directory";
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    private AppConfig() {
    }

    /** 중복 호출되어도 한 번만 초기화한다. */
    public static synchronized void initialize() {
        if (Boolean.getBoolean(AppConfig.class.getName() + ".initialized")) {
            return;
        }

        String activeProfiles = firstValue(
                System.getProperty(SPRING_PROFILES_ACTIVE),
                System.getenv("SPRING_PROFILES_ACTIVE")
        );
        if (activeProfiles != null && !containsProfile(activeProfiles, "local")) {
            // 개발/운영: Azure/JBoss가 주입한 환경변수만 사용한다.
            markInitialized();
            log.info("Local env files are disabled for spring.profiles.active={}", activeProfiles);
            return;
        }

        Path envDirectory = resolveEnvDirectory();
        Map<String, String> fileValues = new LinkedHashMap<>();
        load(envDirectory, "common.env", fileValues);
        load(envDirectory, "local.env", fileValues); // local.env가 common.env보다 우선
        fileValues.forEach((key, value) -> {
            // Azure/JBoss OS 환경변수와 명시적 JVM 옵션(-D)을 절대 덮어쓰지 않는다.
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        });
        markInitialized();
        log.info("Local env files loaded from {}", envDirectory.toAbsolutePath().normalize());
    }

    private static void load(Path envDirectory, String filename, Map<String, String> fileValues) {
        if (!Files.isDirectory(envDirectory)) {
            log.warn("Env directory does not exist: {}", envDirectory.toAbsolutePath().normalize());
            return;
        }

        Dotenv dotenv = Dotenv.configure()
                .directory(envDirectory.toString())
                .filename(filename)
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> fileValues.put(entry.getKey(), entry.getValue()));
    }

    private static Path resolveEnvDirectory() {
        String configured = firstValue(
                System.getProperty(ENV_DIRECTORY_PROPERTY),
                System.getenv("APP_ENV_DIRECTORY")
        );
        if (configured != null) {
            return Paths.get(configured);
        }

        // IntelliJ Tomcat의 기본 working directory가 프로젝트 루트인 경우.
        return Paths.get(System.getProperty("user.dir"), "env");
    }

    private static String firstValue(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private static boolean containsProfile(String profiles, String expected) {
        for (String profile : profiles.split(",")) {
            if (expected.equalsIgnoreCase(profile.trim())) {
                return true;
            }
        }
        return false;
    }

    private static void markInitialized() {
        System.setProperty(AppConfig.class.getName() + ".initialized", "true");
    }
}

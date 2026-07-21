package com.kt.ktedu.core.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/** Spring ContextLoaderListener보다 먼저 로컬 env 파일을 초기화한다. */
public class EnvFilePropertyInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        AppConfig.initialize();
    }
}

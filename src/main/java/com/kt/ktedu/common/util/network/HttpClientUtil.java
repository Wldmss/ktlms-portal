package com.kt.ktedu.common.util.network;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpClientUtil {

    private static final Timeout TIMEOUT_LIMIT = Timeout.ofSeconds(5);
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER;

    static {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(TIMEOUT_LIMIT)
                .setSocketTimeout(TIMEOUT_LIMIT)
                .build();

        CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
        CONNECTION_MANAGER.setDefaultConnectionConfig(connectionConfig);
    }

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setResponseTimeout(TIMEOUT_LIMIT)
            .build();

    private HttpClientUtil() {
    }

    /**
     * [GET 요청]
     */
    public static String sendGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(REQUEST_CONFIG);

        try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(CONNECTION_MANAGER).build()) {
            return httpClient.execute(httpGet, response -> {
                try {
                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                } catch (ParseException e) {
                    throw new IOException("HTTP 응답 바디 파싱 실패", e);
                }
            });
        }
    }

    /**
     * [POST 요청 - JSON]
     */
    public static String sendPostJson(String url, String jsonBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(REQUEST_CONFIG);

        StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(CONNECTION_MANAGER).build()) {
            return httpClient.execute(httpPost, response -> {
                try {
                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                } catch (ParseException e) {
                    throw new IOException("HTTP 응답 바디 파싱 실패", e);
                }
            });
        }
    }
}
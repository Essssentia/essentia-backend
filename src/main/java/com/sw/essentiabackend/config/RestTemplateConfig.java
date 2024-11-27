package com.sw.essentiabackend.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 API 통신을 위한 RestTemplate 설정 파일로, 타임아웃 설정을 포함합니다.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        // HTTP 클라이언트 생성
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(new PoolingHttpClientConnectionManager())
            .build();

        // 타임아웃 설정
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
            httpClient);
        requestFactory.setConnectTimeout(5000); // 연결 시간 초과 (ms)
        requestFactory.setReadTimeout(5000);   // 읽기 시간 초과 (ms)

        // RestTemplate 빌드
        return restTemplateBuilder
            .requestFactory(() -> requestFactory)
            .build();
    }
}
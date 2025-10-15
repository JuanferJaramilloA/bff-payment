package com.co.flypass.payments.bff.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Component
public class RestClientFactory {

    public RestClient buildRestClientFor(PaymentsProperties.ServiceConfig serviceConfig) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(serviceConfig.getConnectTimeout())
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(serviceConfig.getReadTimeout());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(serviceConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

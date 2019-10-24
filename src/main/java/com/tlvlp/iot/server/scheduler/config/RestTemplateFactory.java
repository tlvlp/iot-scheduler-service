package com.tlvlp.iot.server.scheduler.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateFactory {

    private Properties properties;
    private RestTemplateBuilder builder;

    public RestTemplateFactory(Properties properties, RestTemplateBuilder builder) {
        this.properties = properties;
        this.builder = builder;
    }

    public RestTemplate getRestTemplateWithCredentials() {
        return builder
                .basicAuthentication(
                        properties.getAPI_GATEWAY_SECURITY_USER_BACKEND(),
                        properties.getAPI_GATEWAY_SECURITY_PASS_BACKEND())
                .build();
    }
}

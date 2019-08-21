package com.tlvlp.iot.server.scheduler.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiCallerService {

    private static final Logger log = LoggerFactory.getLogger(ApiCallerService.class);
    private RestTemplate restTemplate;

    public ApiCallerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void postPayloadToTarget(String targetUri, String payload) {
        try {
            restTemplate.postForEntity(
                    targetUri,
                    payload,
                    String.class
            );
            log.info("Post sent: {target={}, payload={}}", targetUri, payload);
        } catch (ResourceAccessException e) {
            log.warn("Warning! API endpoint is not reachable: {}", targetUri);
        }
    }
}

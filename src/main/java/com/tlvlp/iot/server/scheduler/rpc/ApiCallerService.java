package com.tlvlp.iot.server.scheduler.rpc;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiCallerService {

    private static final Logger log = LoggerFactory.getLogger(ApiCallerService.class);
    private RestTemplate restTemplate;

    public ApiCallerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void postPayloadToTarget(ScheduledEvent event) {
        try {

            restTemplate.postForEntity(
                    event.getTargetURL(),
                    event.getPayload(),
                    String.class
            );
            log.info("Event executed: {}", event);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Error! Cannot execute event: {} Cause: {}", event, e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            log.error("Error! Cannot execute event: {} Cause: {}", event, e.getMessage());
        }
    }
}

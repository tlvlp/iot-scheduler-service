package com.tlvlp.iot.server.scheduler.services;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class EventExecutor extends Task {

    private static final Logger log = LoggerFactory.getLogger(EventExecutor.class);
    private ScheduledEvent event;
    private RestTemplate restTemplate;

    EventExecutor(ScheduledEvent event, RestTemplate restTemplate) {
        this.event = event;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
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

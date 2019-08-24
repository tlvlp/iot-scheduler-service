package com.tlvlp.iot.server.scheduler.services;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.rpc.ApiCallerService;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EventExecutor extends Task {

    private ApiCallerService apiCallerService;
    private ScheduledEvent event;

    public EventExecutor(ApiCallerService apiCallerService) {
        this.apiCallerService = apiCallerService;
    }

    void setEvent(ScheduledEvent event) {
        this.event = event;
    }

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
        apiCallerService.postPayloadToTarget(event);

    }
}

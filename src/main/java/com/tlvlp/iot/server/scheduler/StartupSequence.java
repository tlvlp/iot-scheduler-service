package com.tlvlp.iot.server.scheduler;

import com.tlvlp.iot.server.scheduler.services.ScheduledEventService;
import it.sauronsoftware.cron4j.Scheduler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupSequence {

    private Scheduler scheduler;
    private ScheduledEventService eventService;

    public StartupSequence(Scheduler scheduler, ScheduledEventService eventService) {
        this.scheduler = scheduler;
        this.eventService = eventService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        scheduler.start();
        eventService.loadEventsFromDB();
    }

}

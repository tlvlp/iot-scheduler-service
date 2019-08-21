package com.tlvlp.iot.server.scheduler.services;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);
    private Scheduler scheduler;
    private EventExecutor eventExecutor;

    public EventScheduler(Scheduler scheduler, EventExecutor eventExecutor) {
        this.scheduler = scheduler;
        this.eventExecutor = eventExecutor;
    }

    ScheduledEvent addSchedule(ScheduledEvent event) {
        eventExecutor.setEvent(event);
        String schedulerID = scheduler.schedule(new SchedulingPattern(event.getCronSchedule()), eventExecutor);
        event.setSchedulerID(schedulerID);
        log.info("Added event to scheduler: {}", event);
        return event;
    }

    void removeSchedule(ScheduledEvent event) {
        scheduler.deschedule(event.getSchedulerID());
        event.setSchedulerID("");
        log.info("Removed event from scheduler: {}", event);
    }
}

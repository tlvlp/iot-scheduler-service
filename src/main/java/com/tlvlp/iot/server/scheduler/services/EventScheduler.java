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

    String addSchedule(ScheduledEvent event) {
        eventExecutor.setEvent(event);
        String schedulerID = scheduler.schedule(new SchedulingPattern(event.getCronSchedule()), eventExecutor);
        log.info("Added event to scheduler. schedulerID: {}", schedulerID);
        return schedulerID;
    }

    void removeSchedule(ScheduledEvent event) {
        String schedulerID = event.getSchedulerID();
        scheduler.deschedule(schedulerID);
        log.info("Removed event from scheduler. schedulerID: {}", schedulerID);
    }
}

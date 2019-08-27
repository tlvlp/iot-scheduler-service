package com.tlvlp.iot.server.scheduler.services;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.persistence.ScheduledEventRepository;
import it.sauronsoftware.cron4j.SchedulingPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduledEventService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledEventService.class);
    private ScheduledEventRepository repository;
    private EventScheduler eventScheduler;

    public ScheduledEventService(ScheduledEventRepository repository, EventScheduler eventScheduler) {
        this.repository = repository;
        this.eventScheduler = eventScheduler;
    }

    public List<ScheduledEvent> getAllEvents() {
        return repository.findAll();
    }

    public List<ScheduledEvent> getEventsByExample(ScheduledEvent exampleEvent) {
        return repository.findAll(Example.of(exampleEvent, ExampleMatcher.matching().withIgnoreNullValues()));
    }

    public ScheduledEvent createEvent(ScheduledEvent event) throws EventException, IllegalArgumentException {
        checkEventValidity(event);
        String eventID = event.getEventID();
        if (isValidString(eventID)) {
            event.setEventID(getNewEventID());
        } else {
            Optional<ScheduledEvent> eventDB = repository.findById(eventID);
            eventDB.ifPresent(e -> eventScheduler.removeSchedule(e));
        }
        event.setSchedulerID(eventScheduler.addSchedule(event));
        event.setLastUpdated(LocalDateTime.now());
        repository.save(event);
        log.info("Event saved: {}", event);
        return event;
    }

    private void checkEventValidity(ScheduledEvent event) throws EventException {
        if (!isValidString(event.getCronSchedule()) || !isValidCronPattern(event.getCronSchedule())) {
            throw new EventException("Event cronSchedule must be a valid String and CRON pattern!");
        } else if (!isValidURL(event.getTargetURL())) {
            throw new EventException("Event targetURI must be a valid URL!");
        } else if (!isValidString(event.getInfo())) {
            throw new EventException("Event info must be a valid String!");
        } else if (event.getPayload() == null) {
            throw new EventException("Event payload must be a valid String!");
        }
    }

    private Boolean isValidString(String str) {
        return str != null && !str.isEmpty();
    }

    private Boolean isValidCronPattern(String str) {
        return SchedulingPattern.validate(str);
    }

    private Boolean isValidURL(String str) {
        try {
            new URL(str).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    private String getNewEventID() {
        return String.format("%s-%S", LocalDate.now().toString(), UUID.randomUUID().toString());
    }

    public void deleteEventById(ScheduledEvent event) {
        String id = event.getEventID();
        Optional<ScheduledEvent> eventDB = repository.findById(id);
        if (eventDB.isPresent()) {
            eventScheduler.removeSchedule(eventDB.get());
            repository.deleteById(id);
            log.info("Event deleted with ID: {}", id);
        } else {
            log.info("Event cannot be deleted, no such ID: {}", id);
        }
    }

    public void scheduleAllEventsFromDB() {
        getAllEvents().forEach(event -> {
            event.setSchedulerID(eventScheduler.addSchedule(event));
            repository.save(event);
        });
    }
}

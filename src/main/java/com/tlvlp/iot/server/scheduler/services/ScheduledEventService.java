package com.tlvlp.iot.server.scheduler.services;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.persistence.ScheduledEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

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

    public ScheduledEvent postEvent(ScheduledEvent event) throws EventException {
        try {
            if (null == event.getId()) {
                return saveNewEvent(event);
            } else {
                return updateEvent(event);
            }
        } catch (EventException e) {
            String err = String.format("Error! Cannot save event: %s", e.getMessage());
            log.error(err);
            throw new EventException(err);
        }
    }

    private ScheduledEvent updateEvent(ScheduledEvent event) throws EventException {
        checkEventValidity(event);
        Optional<ScheduledEvent> eventDB = repository.findById(event.getId());
        if (!eventDB.isPresent()) {
            throw new EventException("Error! Update has failed as Event ID was provided " +
                    "but not present in the database. For new event generation the ID must not be present.");
        }
        eventScheduler.removeSchedule(eventDB.get());
        event.setSchedulerID(eventScheduler.addSchedule(event));
        event.setLastUpdated(LocalDateTime.now());
        repository.save(event);
        log.info("Event updated: {}", event);
        return event;
    }

    private ScheduledEvent saveNewEvent(ScheduledEvent event) throws EventException {
        event.setId(getNewEventID());
        checkEventValidity(event);
        event.setLastUpdated(LocalDateTime.now());
        event.setSchedulerID(eventScheduler.addSchedule(event));
        repository.save(event);
        log.info("Event saved: {}", event);
        return event;
    }

    private void checkEventValidity(ScheduledEvent event) throws EventException {
        if (isInvalid(event.getId())) {
            throw new EventException("Invalid event id!");
        } else if (isInvalid(event.getCronSchedule())) {
            throw new EventException("Invalid event cronSchedule!");
        } else if (isInvalid(event.getTargetUri())) {
            throw new EventException("Invalid event targetUri!");
        } else if (isInvalid(event.getInfo())) {
            throw new EventException("Invalid event info!");
        } else if (event.getPayload() == null) {
            throw new EventException("Invalid event payload!");
        }
    }

    private Boolean isInvalid(String str) {
        return str != null && !str.isEmpty();
    }

    private String getNewEventID() {
        return String.format("%s-%S", LocalDate.now().toString(), UUID.randomUUID().toString());
    }

    public void deleteEventById(String id) {
        Optional<ScheduledEvent> eventDB = repository.findById(id);
        if (eventDB.isPresent()) {
            eventScheduler.removeSchedule(eventDB.get());
            repository.deleteById(id);
            log.info("Event deleted with ID: {}", id);
        }
        log.info("Event cannot be deleted, no such ID: {}", id);
    }

    public void scheduleAllEventsFromDB() {
        getAllEvents().forEach(event -> {
            event.setSchedulerID(eventScheduler.addSchedule(event));
            repository.save(event);
        });
    }
}

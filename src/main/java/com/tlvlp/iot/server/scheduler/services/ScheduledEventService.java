package com.tlvlp.iot.server.scheduler.services;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.persistence.ScheduledEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        return repository.findAll(Example.of(exampleEvent));
    }

    public void postEvent(ScheduledEvent event) throws EventSchedulingException {
        try {
            checkEventValidity(event);
            Optional<ScheduledEvent> eventDB = repository.findById(event.getId());
            if (eventDB.isPresent()) {
                updateEvent(event);
            } else {
                saveNewEvent(event);
            }
        } catch (EventSchedulingException e) {
            String err = String.format("Error! Cannot save event: %s", e.getMessage());
            log.error(err);
            throw new EventSchedulingException(err);
        }
    }

    private void updateEvent(ScheduledEvent event) {
        eventScheduler.removeSchedule(event);
        ScheduledEvent updatedSchedule = eventScheduler.addSchedule(event);
        updatedSchedule.setLastUpdated(LocalDateTime.now());
        repository.save(updatedSchedule);
        log.info("Event updated: {}", updatedSchedule);
    }

    private void saveNewEvent(ScheduledEvent event) {
        ScheduledEvent newSchedule = eventScheduler.addSchedule(event);
        newSchedule.setLastUpdated(LocalDateTime.now());
        repository.save(newSchedule);
        log.info("Event saved: {}", newSchedule);
    }

    private void checkEventValidity(ScheduledEvent event) throws EventSchedulingException {
        if (isInvalid(event.getId())) {
            throw new EventSchedulingException("Invalid event id!");
        } else if (isInvalid(event.getCronSchedule())) {
            throw new EventSchedulingException("Invalid event cronSchedule!");
        } else if (isInvalid(event.getTargetUri())) {
            throw new EventSchedulingException("Invalid event targetUri!");
        } else if (isInvalid(event.getInfo())) {
            throw new EventSchedulingException("Invalid event info!");
        } else if (event.getPayload() == null) {
            throw new EventSchedulingException("Invalid event payload!");
        }
    }

    private Boolean isInvalid(String str) {
        return str != null && !str.isEmpty();
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

    public void loadEventsFromDB() {
        getAllEvents().forEach(event -> {
            ScheduledEvent scheduledEvent = eventScheduler.addSchedule(event);
            updateEvent(scheduledEvent);
        });
    }
}

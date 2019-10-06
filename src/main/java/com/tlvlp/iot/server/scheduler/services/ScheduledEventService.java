package com.tlvlp.iot.server.scheduler.services;

import com.tlvlp.iot.server.scheduler.config.Properties;
import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.persistence.ScheduledEventRepository;
import it.sauronsoftware.cron4j.SchedulingPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.Validation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduledEventService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledEventService.class);
    private ScheduledEventRepository repository;
    private EventScheduler eventScheduler;
    private Properties properties;


    public ScheduledEventService(ScheduledEventRepository repository, EventScheduler eventScheduler, Properties properties) {
        this.repository = repository;
        this.eventScheduler = eventScheduler;
        this.properties = properties;
    }

    public List<ScheduledEvent> getAllEventsFromList(List<String> eventIDList) {
        return eventIDList.stream()
                .map(eventID -> repository.findById(eventID))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public String createOrUpdateMqttSendEvent(ScheduledEvent event) throws EventException {
        event.setTargetURL(getPostMqttMessageUrl());
        return createOrUpdateEvent(event);
    }

    public String createOrUpdateEvent(ScheduledEvent event) throws EventException {
        var eventID = event.getEventID();
        if (eventID == null) {
            event.setEventID(getNewEventID());
        } else {
            Optional<ScheduledEvent> eventDB = repository.findById(eventID);
            eventDB.ifPresent(e -> eventScheduler.removeSchedule(e));
        }
        checkEventValidity(event);
        event.setSchedulerID(eventScheduler.addSchedule(event));
        event.setLastUpdated(LocalDateTime.now());
        repository.save(event);
        log.info("Event saved: {}", event);
        return event.getEventID();
    }

    private String getPostMqttMessageUrl() {
        return String.format("http://%s:%s%s",
                properties.getAPI_GATEWAY_NAME(),
                properties.getAPI_GATEWAY_PORT(),
                properties.getAPI_GATEWAY_API_OUTGOING_MQTT_MESSAGE());
    }

    private void checkEventValidity(ScheduledEvent event) throws EventException {
        var validationProblems =
                Validation.buildDefaultValidatorFactory().getValidator().validate(event);
        if (!validationProblems.isEmpty()) {
            throw new EventException(validationProblems.toString());
        }
        // TODO: Validate these through the validation api as well
        if (!SchedulingPattern.validate(event.getCronSchedule())) {
            throw new EventException("cronSchedule must be a valid cron pattern");
        }
        if (!isValidURL(event.getTargetURL())) {
            throw new EventException("Target URL must be a valid url pattern");
        }
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
        return String.format("%s-EVENT-%S", LocalDate.now().toString(), UUID.randomUUID().toString());
    }

    public String deleteEvent(ScheduledEvent event) throws EventException {
        var eventID = event.getEventID();
        Optional<ScheduledEvent> eventDB = repository.findById(eventID);
        if (eventDB.isPresent()) {
            eventScheduler.removeSchedule(eventDB.get());
            repository.deleteById(eventID);
            log.info("Event deleted with ID: {}", eventID);
        }
        return eventID;
    }

    public void scheduleAllEventsFromDB() {
        List<ScheduledEvent> allEvents = repository.findAll();
        int eventNum = allEvents.size();
        log.info("Scheduling events from the database: {}", eventNum);
        allEvents.forEach(event -> {
            event.setSchedulerID(eventScheduler.addSchedule(event));
            repository.save(event);
        });
    }
}

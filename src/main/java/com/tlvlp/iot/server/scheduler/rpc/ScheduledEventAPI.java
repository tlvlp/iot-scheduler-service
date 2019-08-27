package com.tlvlp.iot.server.scheduler.rpc;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.services.EventException;
import com.tlvlp.iot.server.scheduler.services.ScheduledEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ScheduledEventAPI {

    private ScheduledEventService eventService;

    public ScheduledEventAPI(ScheduledEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("${SCHEDULER_SERVICE_API_LIST_ALL_EVENT}")
    public ResponseEntity getAllEvents() {
        return new ResponseEntity<>(eventService.getAllEvents(), HttpStatus.OK);
    }

    @GetMapping("${SCHEDULER_SERVICE_API_LIST_EVENTS_BY_EXAMPLE}")
    public ResponseEntity getEventsByExample(@RequestBody ScheduledEvent exampleEvent) {
        return new ResponseEntity<>(eventService.getEventsByExample(exampleEvent), HttpStatus.OK);
    }

    @PostMapping("${SCHEDULER_SERVICE_API_POST_EVENT}")
    public ResponseEntity createEvent(@RequestBody ScheduledEvent event) {
        try {
            ScheduledEvent processedEvent = eventService.createEvent(event);
            return new ResponseEntity<>(processedEvent, HttpStatus.OK);
        } catch (EventException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("${SCHEDULER_SERVICE_API_DELETE_EVENT_BY_ID}")
    public ResponseEntity deleteEventById(@RequestBody ScheduledEvent event) {
        eventService.deleteEventById(event);
        return new ResponseEntity(HttpStatus.OK);

    }

}

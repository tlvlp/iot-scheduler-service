package com.tlvlp.iot.server.scheduler.rpc;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.services.EventSchedulingException;
import com.tlvlp.iot.server.scheduler.services.ScheduledEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ScheduledEventControl {

    private ScheduledEventService eventService;

    public ScheduledEventControl(ScheduledEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("${SCHEDULER_SERVICE_EVENT_LIST_ALL_CONTROL}")
    public ResponseEntity getAllEvents() {
        return new ResponseEntity<>(eventService.getAllEvents(), HttpStatus.OK);
    }

    @GetMapping("${SCHEDULER_SERVICE_EVENT_LIST_BY_EXAMPLE_CONTROL}")
    public ResponseEntity getEventsByExample(@RequestBody ScheduledEvent exampleEvent) {
        return new ResponseEntity<>(eventService.getEventsByExample(exampleEvent), HttpStatus.OK);
    }

    @PostMapping("${SCHEDULER_SERVICE_POST_EVENT_CONTROL}")
    public ResponseEntity postEvent(@RequestBody ScheduledEvent event) {
        try {
            eventService.postEvent(event);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (EventSchedulingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("${SCHEDULER_SERVICE_DELETE_EVENT_BY_ID_CONTROL}")
    public ResponseEntity deleteEventById(@RequestParam String eventID) {
        eventService.deleteEventById(eventID);
        return new ResponseEntity(HttpStatus.OK);

    }

}

package com.tlvlp.iot.server.scheduler.rpc;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.services.EventException;
import com.tlvlp.iot.server.scheduler.services.ScheduledEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ScheduledEventAPI {

    private ScheduledEventService eventService;

    public ScheduledEventAPI(ScheduledEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("${SCHEDULER_SERVICE_API_POST_EVENT}")
    public ResponseEntity<String> createOrUpdateEvent(@RequestBody ScheduledEvent event) {
        try {
            return new ResponseEntity<>(eventService.createOrUpdateEvent(event), HttpStatus.OK);
        } catch (EventException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("${SCHEDULER_SERVICE_API_DELETE_EVENT_BY_ID}")
    public ResponseEntity deleteEventById(@RequestParam String eventID) {
        eventService.deleteEventById(eventID);
        return new ResponseEntity(HttpStatus.OK);

    }

    @PostMapping("${SCHEDULER_SERVICE_API_GET_EVENTS_FROM_LIST}")
    public ResponseEntity<List<ScheduledEvent>> getAllEventsFromList(@RequestBody List<String> eventIDList) {
        return new ResponseEntity<>(eventService.getAllEventsFromList(eventIDList), HttpStatus.OK);
    }

}

package com.tlvlp.iot.server.scheduler.services;

import com.tlvlp.iot.server.scheduler.persistence.ScheduledEvent;
import com.tlvlp.iot.server.scheduler.persistence.ScheduledEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Scheduled event service")
class ScheduledEventServiceTest {

    @Mock
    private ScheduledEventRepository repository;
    @Mock
    private EventScheduler eventScheduler;

    @InjectMocks
    private ScheduledEventService scheduledEventService;


    @Captor
    private ArgumentCaptor<ScheduledEvent> captor;
    private ScheduledEvent event;

    @BeforeEach
    void beforeEach() {
        event = new ScheduledEvent()
                .setCronSchedule("* * * * *")
                .setInfo("info")
                .setPayload(Collections.singletonMap("test", "value"))
                .setTargetURL("http://test");
    }


    @Test
    @DisplayName("Add new schedule")
    void createEvent_NewEvent() throws EventException {
        // given
        String newSchedulerID = "id";
        given(eventScheduler.addSchedule(any(ScheduledEvent.class))).willReturn(newSchedulerID);
        given(repository.save(any(ScheduledEvent.class))).willReturn(new ScheduledEvent());

        // when
        ScheduledEvent newEvent = scheduledEventService.createEvent(event);

        // then
        then(repository).should().save(captor.capture());
        then(repository).shouldHaveNoMoreInteractions();
        then(eventScheduler).shouldHaveZeroInteractions();

        assertNotNull(newEvent, "Event is not null");
        assertEquals(captor.getValue().getSchedulerID(), newSchedulerID,
                "Scheduler ID is added and matches the one provided by the Event Scheduler");
        assertNotNull(captor.getValue().getEventID(), "EventID is generated");
        assertNotNull(captor.getValue().getLastUpdated(), "Last update timestamp is generated");
    }

    @Test
    @DisplayName("Add new schedule and remove old schedule")
    void createEvent_OldEvent() throws EventException {
        // given
        String oldEventID = "oldEventID";
        String oldSchedulerID = "oldSchedulerID";
        String newSchedulerID = "newSchedulerID";
        LocalDateTime oldUpdateTime = LocalDateTime.now().minusDays(1);
        event
                .setEventID(oldEventID)
                .setSchedulerID(oldSchedulerID)
                .setLastUpdated(oldUpdateTime);

        given(eventScheduler.addSchedule(any(ScheduledEvent.class))).willReturn(newSchedulerID);
        given(repository.save(any(ScheduledEvent.class))).willReturn(new ScheduledEvent());
        given(repository.findById(oldEventID)).willReturn(Optional.of(event));

        // when
        ScheduledEvent newEvent = scheduledEventService.createEvent(event);

        // then
        then(repository).should().findById(oldEventID);
        then(eventScheduler).should().removeSchedule(event);
        then(repository).should().save(captor.capture());

        assertNotNull(newEvent, "Event is not null");
        assertEquals(captor.getValue().getEventID(), oldEventID, "EventID remains unchanged");
        assertNotEquals(captor.getValue().getLastUpdated(), oldUpdateTime,
                "Last update timestamp is updated");
        assertEquals(captor.getValue().getSchedulerID(), newSchedulerID,
                "Scheduler ID is updated to the one provided by the Event Scheduler");
    }

    @Test
    @DisplayName("Add new schedule with invalid Cron schedule")
    void createEventInvalidCronschedule() {
        event.setCronSchedule(null);
        assertThrows(EventException.class, () -> scheduledEventService.createEvent(event));
    }

    @Test
    @DisplayName("Add new schedule with invalid Info")
    void createEventInvalidInfo() {
        event.setInfo(null);
        assertThrows(EventException.class, () -> scheduledEventService.createEvent(event));
    }

    @Test
    @DisplayName("Add new schedule with invalid Payload")
    void createEventInvalidPayload() {
        event.setPayload(null);
        assertThrows(EventException.class, () -> scheduledEventService.createEvent(event));
    }

    @Test
    @DisplayName("Add new schedule with invalid TargetURL")
    void createEventInvalidTargetURL() {
        event.setTargetURL(null);
        assertThrows(EventException.class, () -> scheduledEventService.createEvent(event));
    }


    @Disabled //TODO: repository.findall() double call error
    @Test
    @DisplayName("Schedule persisted events on startup")
    void scheduleAllEventsFromDB() {
        // given
        String newSchedulerID = "id";
        List<ScheduledEvent> allEvents = Arrays.asList(new ScheduledEvent(), new ScheduledEvent(), new ScheduledEvent());
        given(repository.findAll()).willReturn(allEvents);
        given(eventScheduler.addSchedule(any(ScheduledEvent.class))).willReturn(newSchedulerID);
        given(repository.save(any(ScheduledEvent.class))).willReturn(new ScheduledEvent());

        // when
        scheduledEventService.scheduleAllEventsFromDB();

        // then
//        then(repository).should().findAll();
        then(eventScheduler).should().addSchedule(new ScheduledEvent());
        then(repository).should(times(3)).save(captor.capture());
        for (ScheduledEvent savedEvent : captor.getAllValues()) {
            assertNotNull(savedEvent, "Event is not null");
            assertEquals(savedEvent.getSchedulerID(), newSchedulerID,
                    "Scheduler ID is updated to the one provided by the Event Scheduler");
        }

    }
}
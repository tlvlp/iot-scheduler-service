package com.tlvlp.iot.server.scheduler.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Document(collection = "events")
public class ScheduledEvent {

    @Id
    private String eventID;
    private String schedulerID;
    private String cronSchedule;
    private String targetURL;
    private String info;
    private LocalDateTime lastUpdated;
    private Map payload;

    @Override
    public String toString() {
        return "ScheduledEvent{" +
                "eventID='" + eventID + '\'' +
                ", schedulerID='" + schedulerID + '\'' +
                ", cronSchedule='" + cronSchedule + '\'' +
                ", targetURL='" + targetURL + '\'' +
                ", info='" + info + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", payload=" + payload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledEvent event = (ScheduledEvent) o;
        return eventID.equals(event.eventID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventID);
    }

    public String getEventID() {
        return eventID;
    }

    public ScheduledEvent setEventID(String eventID) {
        this.eventID = eventID;
        return this;
    }

    public String getSchedulerID() {
        return schedulerID;
    }

    public ScheduledEvent setSchedulerID(String schedulerID) {
        this.schedulerID = schedulerID;
        return this;
    }

    public String getCronSchedule() {
        return cronSchedule;
    }

    public ScheduledEvent setCronSchedule(String cronSchedule) {
        this.cronSchedule = cronSchedule;
        return this;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public ScheduledEvent setTargetURL(String targetURL) {
        this.targetURL = targetURL;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public ScheduledEvent setInfo(String info) {
        this.info = info;
        return this;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public ScheduledEvent setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public Map getPayload() {
        return payload;
    }

    public ScheduledEvent setPayload(Map payload) {
        this.payload = payload;
        return this;
    }
}

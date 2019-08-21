package com.tlvlp.iot.server.scheduler.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "events")
public class ScheduledEvent {

    @Id
    private String id;
    private String schedulerID;
    private String cronSchedule;
    private String targetUri;
    private String info;
    private LocalDateTime lastUpdated;
    private String payload;


    @Override
    public String toString() {
        return "ScheduledEvent{" +
                "id='" + id + '\'' +
                ", schedulerID='" + schedulerID + '\'' +
                ", cronSchedule='" + cronSchedule + '\'' +
                ", targetUri='" + targetUri + '\'' +
                ", info='" + info + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", payload=" + payload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledEvent)) return false;
        ScheduledEvent that = (ScheduledEvent) o;
        return id.equals(that.id) &&
                schedulerID.equals(that.schedulerID) &&
                cronSchedule.equals(that.cronSchedule) &&
                targetUri.equals(that.targetUri) &&
                info.equals(that.info) &&
                payload.equals(that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, schedulerID, cronSchedule, targetUri, info, payload);
    }

    public String getId() {
        return id;
    }

    public ScheduledEvent setId(String id) {
        this.id = id;
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

    public String getTargetUri() {
        return targetUri;
    }

    public ScheduledEvent setTargetUri(String targetUri) {
        this.targetUri = targetUri;
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

    public String getPayload() {
        return payload;
    }

    public ScheduledEvent setPayload(String payload) {
        this.payload = payload;
        return this;
    }
}

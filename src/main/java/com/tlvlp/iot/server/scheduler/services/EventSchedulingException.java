package com.tlvlp.iot.server.scheduler.services;

public class EventSchedulingException extends Exception {
    EventSchedulingException(String errorMessage) {
        super(errorMessage);
    }
}

package com.tlvlp.iot.server.scheduler.services;

public class EventException extends Exception {
    EventException(String errorMessage) {
        super(errorMessage);
    }
}

package com.tlvlp.iot.server.scheduler.services;

import it.sauronsoftware.cron4j.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class SchedulerBean {

    @Bean
    public Scheduler getScheduler() {
        return new Scheduler();
    }
}

package com.tlvlp.iot.server.scheduler.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface ScheduledEventRepository
        extends MongoRepository<ScheduledEvent, String>, QueryByExampleExecutor<ScheduledEvent> {

}

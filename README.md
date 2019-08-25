# IoT Server Scheduler Service

## Service
Part of the tlvlp IoT project's server side microservices.

This Dockerized SpringBoot-based service is responsible for executing pre-composed API calls timed with CRON schedules.
- Crates, modifies, lists and deletes scheduled events
- Persists events to the database and re-schedules them on startup
- Executes API calls to given endpoints with given payloads

## Deployment
- This service is currently designed as **stateful** and should only have one instance running per Docker Swarm Stack.
- For settings and deployment details see the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)

## Server-side API
Actual API endpoints are inherited from the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.

### GET Events by example:

Returns a list of Events that match all values in the example


#### Related environment variables:
- ${SCHEDULER_SERVICE_EVENT_LIST_BY_EXAMPLE_CONTROL}
- ${SCHEDULER_SERVICE_EVENT_LIST_BY_EXAMPLE_CONTROL_URI}

#### Fields:
Takes a ScheduledEvent object in the RequestBody where all the empty fields are ignored
- **id**: String - event ID
- **schedulerID**: String - the ID assigned by the scheduler
- **cronSchedule**: String - must be a valid CRON expression (cron4j)
- **targetUri**: String - targeted API endpoint
- **info**: String - a human readable information about the scheduled event
- **lastUpdated**: LocalDateTime of the last update
- **payload**: Map<String, String> containing the payload to be delivered to the **targetUri** 
- **schedulerID** and **lastUpdated** fields are ignored and automatically repopulated on a successful request.

Get one event by sending a ScheduledEvent object:
```
{
    "id": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC",
    "cronSchedule": "* * * * *",
    "targetUri": "http://mqtt-client:8100/messages",
    "info": "Posts an mqtt message every minute",
    "payload": {
        "topic": "/global/test",
        "payload": {
            "much": "payload",
            "even": "better"
        }
    }
}


```

Get one event with a specific ID:
```
{
    "id": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC"
}
```

Get all the events that are scheduled for 9am every day and target the below URI:
```
{
    "cronSchedule": "* 9 * * *",
    "targetUri": "http://mqtt-client:8100/messages"
}

```

### GET All events:

Returns all the Events

#### Related global variables:
- ${SCHEDULER_SERVICE_EVENT_LIST_ALL_CONTROL}
- ${SCHEDULER_SERVICE_EVENT_LIST_ALL_CONTROL_URI}

#### Fields:
Takes no arguments.


### POST Create or modify events:

Creates a new ScheduledEvent or updates and existing one and returns it for later reference.

#### Related global variables:
- ${SCHEDULER_SERVICE_POST_EVENT_CONTROL}
- ${SCHEDULER_SERVICE_POST_EVENT_CONTROL_URI}

#### Fields:
- Takes a ScheduledEvent object in the RequestBody. See the "GET Events by example" section for field details.
- If the **id** is included then the event with the matching ID will be updated.
- **schedulerID** and **lastUpdated** fields are ignored and automatically repopulated on a successful request.
- Note that in the example there is a payload object inside the **payload** field as this is a message to be posted 
to the MQTT Client service and it contains another message to be forwarded to the units.

#### Create a new schedule
- **cronSchedule**
- **targetUri**
- **info**
- **payload**
```
{
    "cronSchedule": "* * * * *",
    "targetUri": "http://mqtt-client:8100/messages",
    "info": "Posts an mqtt message every minute",
    "payload": {
        "topic": "/global/test",
        "payload": {
            "much": "payload",
            "even": "better"
        }
    }
}
```

#### Update an existing schedule
- **id**
- **cronSchedule**
- **targetUri**
- **info**
- **payload**
```
{
    "id": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC",
    "cronSchedule": "* * * * *",
    "targetUri": "http://mqtt-client:8100/messages",
    "info": "Posts an mqtt message every minute",
    "payload": {
        "topic": "/global/test",
        "payload": {
            "much": "payload-UPDATED",
            "even": "better-UPDATED"
        }
    }
}
```

### DELETE event by ID:

Deletes an Event 

#### Related global variables:
- ${SCHEDULER_SERVICE_DELETE_EVENT_BY_ID_CONTROL}
- ${SCHEDULER_SERVICE_DELETE_EVENT_BY_ID_CONTROL_URI}

#### Fields:
Takes a ScheduledEvent object but only requires the **id** field and the rest is ignored.
```
{
    "id": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC"
}
```
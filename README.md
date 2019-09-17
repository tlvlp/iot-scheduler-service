# IoT Server Scheduler Service

## Service
Part of the tlvlp IoT project's server side microservices.

This Dockerized SpringBoot-based service is responsible for executing pre-composed API calls timed with CRON schedules.
- Crates, modifies, lists and deletes scheduled events
- Persists events to the database and re-schedules them on startup
- Executes API calls to given endpoints with given payloads

## Building and publishing JAR + Docker image
This project is using the using the [Palantir Docker Gradle plugin](https://github.com/palantir/gradle-docker).
All configuration can be found in the [Gradle build file](build.gradle) file 
and is recommended to be run with the docker/dockerTagsPush task.

## Deployment
- This service is currently designed as **stateful** and should only have one instance running per Docker Swarm Stack.
- For settings and deployment details see the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)

## Server-side API
Actual API endpoints are inherited from the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.

### GET Events by example:

Returns a list of Events that match all values in the example


#### Related environment variables:
- ${SCHEDULER_SERVICE_API_LIST_EVENTS_BY_EXAMPLE}
- ${SCHEDULER_SERVICE_API_LIST_EVENTS_BY_EXAMPLE_URL}

#### Fields:
Takes a ScheduledEvent object in the RequestBody where all the empty fields are ignored
- **eventID**: String - event ID
- **schedulerID**: String - the ID assigned by the scheduler
- **cronSchedule**: String - must be a valid CRON expression (cron4j)
- **targetURL**: String - targeted API endpoint
- **info**: String - a human readable information about the scheduled event
- **lastUpdated**: LocalDateTime of the last update
- **payload**: Map<String, String> containing the payload to be delivered to the **targetUri** 
- **schedulerID** and **lastUpdated** fields are ignored and automatically repopulated on a successful request.

Get one event by sending a ScheduledEvent object:
```
{
    "eventID": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC",
    "cronSchedule": "* * * * *",
    "targetURL": "http://mqtt-client:8100/messages",
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
    "eventID": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC"
}
```

Get all the events that are scheduled for 9am every day and target the below URI:
```
{
    "cronSchedule": "* 9 * * *",
    "targetURL": "http://mqtt-client:8100/messages"
}

```

### GET All events:

Returns all the Events

#### Related global variables:
- ${SCHEDULER_SERVICE_API_LIST_ALL_EVENT}
- ${SCHEDULER_SERVICE_API_LIST_ALL_EVENT_URL}

#### Fields:
Takes no arguments.


### POST Create or modify events:

Creates a new ScheduledEvent or updates and existing one and returns it for later reference.

#### Related global variables:
- ${SCHEDULER_SERVICE_API_POST_EVENT}
- ${SCHEDULER_SERVICE_API_POST_EVENT_URL}

#### Fields:
- Takes a ScheduledEvent object in the RequestBody. See the "GET Events by example" section for field details.
- If the **eventID** is included then the event with the matching ID will be updated.
- **schedulerID** and **lastUpdated** fields are ignored and automatically repopulated on a successful request.
- Note that in the example there is a payload object inside the **payload** field as this is a message to be posted 
to the MQTT Client service and it contains another message to be forwarded to the units.

#### Create a new schedule
- **cronSchedule**
- **targetURL**
- **info**
- **payload**
```
{
    "cronSchedule": "* * * * *",
    "targetURL": "http://mqtt-client:8100/messages",
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
- **eventID**
- **cronSchedule**
- **targetURL**
- **info**
- **payload**
```
{
    "eventID": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC",
    "cronSchedule": "* * * * *",
    "targetURL": "http://mqtt-client:8100/messages",
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

### DELETE event by eventID:

Deletes an Event 

#### Related global variables:
- ${SCHEDULER_SERVICE_API_DELETE_EVENT_BY_ID}
- ${SCHEDULER_SERVICE_API_DELETE_EVENT_BY_ID_URL}

#### Fields:
Takes aa String with the **eventID**.
```
{
    "eventID": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC"
}
```
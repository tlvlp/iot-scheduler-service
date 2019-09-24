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

### POST Get All Events from eventID List:

Takes a list of eventIds and returns the corresponding events

#### Related environment variables:
- ${SCHEDULER_SERVICE_API_GET_EVENTS_FROM_LIST}

#### Input:
RequestBody:
- List of **eventID**

```
{
    ["eventID_1", "eventID_2", "eventID_3"]  
}

```

#### Output:

The list of events matching the eventIDs

```
{
    [ {..}, {..}, {..} ]  
}

```


### POST Create or modify events:

Creates a new ScheduledEvent or updates and existing one and returns the eventID.

#### Related global variables:
- ${SCHEDULER_SERVICE_API_POST_EVENT}

#### Input:
RequestBody:
- **eventID**: String - event ID. _NOTE_: if it's included then the event with the matching ID will be updated.
- **schedulerID**: String - the ID assigned by the scheduler
- **cronSchedule**: String - must be a valid CRON expression (cron4j)
- **targetURL**: String - targeted API endpoint
- **info**: String - a human readable information about the scheduled event
- **lastUpdated**: LocalDateTime of the last update
- **payload**: Map<String, String> containing the payload to be delivered to the **targetUri** 
- **schedulerID** and **lastUpdated** fields are ignored and automatically repopulated on a successful request.


#### Create a new schedule (note the lack of eventID!)
RequestBody:
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
RequestBody:
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

#### Output:
The eventID of the created / updated event


### DELETE event by eventID:

Deletes an Event 

#### Related global variables:
- ${SCHEDULER_SERVICE_API_DELETE_EVENT_BY_ID}

#### Input:
RequestParam: Takes a String with the **eventID**.


#### Output:
Acknowledgement with a Http response (200)
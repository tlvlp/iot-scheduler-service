# IoT Server Scheduler Service

## Service
Part of the tlvlp IoT project's server side microservices.

This Dockerized SpringBoot-based service is responsible for scheduling tasks 
by executing pre-composed API calls according CRON schedules.
* Crates, modifies, lists and deletes scheduled events
* Persists events to the database and re-schedules them on startup
* Executes API calls to given endpoints with given payloads

## Deployment
For settings and deployemnt details see the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)

## Server-side API
Actual API endpoints are inherited from the he project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.

### GET Events by example:

#### Related global variables:
- ${SCHEDULER_SERVICE_EVENT_LIST_BY_EXAMPLE_CONTROL}
- ${SCHEDULER_SERVICE_EVENT_LIST_BY_EXAMPLE_CONTROL_URI}

#### Fields:
Takes a ScheduledEvent object where all the empty fields are ignored
- "id": String - event ID
- "schedulerID": String - the ID assigned by the scheduler
- "cronSchedule": String - must be a valid CRON expression (cron4j)
- "targetUri": String - targeted API endpoint
- "info": String - a human readable information about the scheduled event
- "lastUpdated": LocalDateTime of the last update
- "payload": Map<String, String> containing the payload to be delivered to the "targetUri" 
- "schedulerID" and "lastUpdated" fields are ignored and automatically repopulated on a successful request.


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
    "targetUri": "http://mqtt-client:8100/messages",
}

```

### GET All events:

#### Related global variables:
- ${SCHEDULER_SERVICE_EVENT_LIST_ALL_CONTROL}
- ${SCHEDULER_SERVICE_EVENT_LIST_ALL_CONTROL_URI}

#### Fields:
Takes no arguments.


### POST Create or modify events:

#### Related global variables:
- ${SCHEDULER_SERVICE_POST_EVENT_CONTROL}
- ${SCHEDULER_SERVICE_POST_EVENT_CONTROL_URI}

#### Fields:
- Takes a ScheduledEvent object. See the "GET Events by example" section for details.
- If the ID is excluded then a new event will be created.
- "schedulerID" and "lastUpdated" fields are ignored and automatically repopulated on a successful request.

#### Creating a new schedule
- "cronSchedule"
- "targetUri"
- "info"
- "payload"


#### Updating an existing schedule
- "id": 
- "cronSchedule"
- "targetUri"
- "info"
- "payload"


### DELETE event by ID:

#### Related global variables:
- ${SCHEDULER_SERVICE_DELETE_EVENT_BY_ID_CONTROL}
- ${SCHEDULER_SERVICE_DELETE_EVENT_BY_ID_CONTROL_URI}

#### Fields:
Takes a ScheduledEvent object but only the "id" field is mandatory
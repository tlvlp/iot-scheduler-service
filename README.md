# IoT Server Scheduler Service

## LEGACY REPOSITORY!
This repository is no longer maintained. See the [project summary page](https://github.com/tlvlp/iot-project-summary) for details and updates.

## Service
Part of the [tlvlp IoT project](https://github.com/tlvlp/iot-project-summary)'s server side microservices.

This Dockerized SpringBoot-based service is responsible for executing pre-composed API calls timed with CRON schedules.
- Crates, modifies, lists and deletes scheduled events
- Persists events to the database and re-schedules them on startup
- Executes API calls to given endpoints with given payloads

## Building and publishing JAR + Docker image
This project is using the [Palantir Docker Gradle plugin](https://github.com/palantir/gradle-docker).
All configuration can be found in the [Gradle build file](build.gradle) file 
and is recommended to be run with the docker/dockerTagsPush task.

## Dockerhub
Repository: [tlvlp/iot-scheduler-service](https://cloud.docker.com/repository/docker/tlvlp/iot-scheduler-service)

## Deployment
- This service is currently designed as **stateful** and should only have one instance running per Docker Swarm Stack.
- For settings and deployment details see the project's [deployment repository](https://github.com/tlvlp/iot-server-deployment)

## Server-side API
Actual API endpoints are inherited from the project's [deployment repository](https://github.com/tlvlp/iot-server-deployment) via environment variables.

> API documentation has been temporarily removed!

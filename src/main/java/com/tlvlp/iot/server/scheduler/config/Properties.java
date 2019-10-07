package com.tlvlp.iot.server.scheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${API_GATEWAY_SECURITY_USER_BACKEND}")
    private String API_GATEWAY_SECURITY_USER_BACKEND;

    @Value("${API_GATEWAY_SECURITY_PASS_BACKEND_SECRET_FILE_PARSED}")
    private String API_GATEWAY_SECURITY_PASS_BACKEND;

    @Value("${API_GATEWAY_NAME}")
    private String API_GATEWAY_NAME;

    @Value("${API_GATEWAY_PORT}")
    private String API_GATEWAY_PORT;

    @Value("${API_GATEWAY_API_OUTGOING_MQTT_MESSAGE}")
    private String API_GATEWAY_API_OUTGOING_MQTT_MESSAGE;


    public String getAPI_GATEWAY_SECURITY_PASS_BACKEND() {
        return API_GATEWAY_SECURITY_PASS_BACKEND;
    }

    public String getAPI_GATEWAY_SECURITY_USER_BACKEND() {
        return API_GATEWAY_SECURITY_USER_BACKEND;
    }

    public String getAPI_GATEWAY_NAME() {
        return API_GATEWAY_NAME;
    }

    public String getAPI_GATEWAY_PORT() {
        return API_GATEWAY_PORT;
    }

    public String getAPI_GATEWAY_API_OUTGOING_MQTT_MESSAGE() {
        return API_GATEWAY_API_OUTGOING_MQTT_MESSAGE;
    }
}

package com.abshekh.kafkastreampoc.rest.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PocRestClient {
    private static final String SERVICE_URL = "http://localhost:6060/?message=";

    public void restClient(String message) {
        log.debug("rest call...");
        log.debug(" Making a request to {} at :{}", SERVICE_URL + message, LocalDateTime.now());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(SERVICE_URL, String.class);
    }
}

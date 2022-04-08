package com.abshekh.kafkastreampoc.rest.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
public class PocRestClient {

    private static final String SERVICE_URL = "http://localhost:6060/?message=";
    private static final String INSTANCE = "instance-topic2";
    private final Random random;

    public PocRestClient() {
        this.random = new Random();
    }

    @CircuitBreaker(name = INSTANCE, fallbackMethod = "fallback")
    @Retry(name = INSTANCE)
    public void restClient(String message) {
        log.debug("rest call...");
        log.debug(" Making a request to {} at :{}", SERVICE_URL + message, LocalDateTime.now());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(SERVICE_URL, String.class);
    }

    @SneakyThrows
    public void fallback(Exception e) {
        log.debug("rest call fallback...");
//        if(random.nextBoolean()) {
//            throw new RequeueCurrentMessageException(e);
//        }
//        throw new RequeueCurrentMessageException(e);
    }
}

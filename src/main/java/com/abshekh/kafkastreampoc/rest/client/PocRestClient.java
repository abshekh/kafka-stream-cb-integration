package com.abshekh.kafkastreampoc.rest.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static com.abshekh.kafkastreampoc.resilience.config.ResilienceCircuitBreaker.*;
import static com.abshekh.kafkastreampoc.resilience.config.ResilienceRateLimiter.RATE_LIMITER_INSTANCE_TOPIC_4;
import static com.abshekh.kafkastreampoc.resilience.config.ResilienceRetry.*;

@Service
@AllArgsConstructor
@Slf4j
public class PocRestClient {
    private static final String SERVICE_URL = "http://localhost:6060/?message=";

    public void restClient1(String message) {
        internalRestClient(message);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_INSTANCE_TOPIC_2)
    @Retry(name = RETRY_INSTANCE_TOPIC_2)
    public void restClient2(String message) {
        internalRestClient(message);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_INSTANCE_TOPIC_3)
    @Retry(name = RETRY_INSTANCE_TOPIC_3)
    public void restClient3(String message) {
        internalRestClient(message);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_INSTANCE_TOPIC_4)
    @Retry(name = RETRY_INSTANCE_TOPIC_4)
    @RateLimiter(name = RATE_LIMITER_INSTANCE_TOPIC_4)
    public void restClient4(String message) {
        internalRestClient(message);
    }

    private void internalRestClient(String message) {
        log.debug("rest call...");
        log.debug(" Making a request to {} at :{}", SERVICE_URL + message, LocalDateTime.now());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(SERVICE_URL, String.class);
    }
}

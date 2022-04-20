package com.abshekh.kafkastreampoc.rest.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class PocRestClient {
    private static final String SERVICE_URL = "http://localhost:6060/?message=";
    private final CircuitBreaker circuitBreakerInstanceTopic2;
    private final CircuitBreaker circuitBreakerInstanceTopic3;
    private final CircuitBreaker circuitBreakerInstanceTopic4;
    private final Retry retryInstanceTopic2;
    private final Retry retryInstanceTopic3;
    private final Retry retryInstanceTopic4;
    private final RateLimiter rateLimiterInstanceTopic4;

    public void restClient1(String message) {
        internalRestClient(message);
    }

    public void restClient2(String message) {
        var decoratedRestClient = Decorators.ofSupplier(() -> {
                    internalRestClient(message);
                    return null;
                })
                .withCircuitBreaker(circuitBreakerInstanceTopic2)
                .withRetry(retryInstanceTopic2)
                .decorate();

        Try.ofSupplier(decoratedRestClient);
    }

    public void restClient3(String message) {
        var decoratedRestClient = Decorators.ofSupplier(() -> {
                    internalRestClient(message);
                    return null;
                })
                .withCircuitBreaker(circuitBreakerInstanceTopic3)
                .withRetry(retryInstanceTopic3)
                .decorate();

        Try.ofSupplier(decoratedRestClient);
    }

    public void restClient4(String message) {
        var decoratedRestClient = Decorators.ofSupplier(() -> {
                    internalRestClient(message);
                    return null;
                })
                .withCircuitBreaker(circuitBreakerInstanceTopic4)
                .withRateLimiter(rateLimiterInstanceTopic4)
                .withRetry(retryInstanceTopic4)
                .decorate();

        Try.ofSupplier(decoratedRestClient);
    }

    private void internalRestClient(String message) {
        log.debug("rest call...");
        log.debug(" Making a request to {} at :{}", SERVICE_URL + message, LocalDateTime.now());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(SERVICE_URL, String.class);
    }
}

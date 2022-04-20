package com.abshekh.kafkastreampoc.rest.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Call;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PocRateLimiterRestClient {
    private static final String SERVICE_URL = "http://localhost:6060/?message=";
    private final RateLimiterRegistry rateLimiterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    public PocRateLimiterRestClient(RateLimiterRegistry rateLimiterRegistry,
                                    CircuitBreakerRegistry circuitBreakerRegistry,
                                    RetryRegistry retryRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    @Scheduled(cron = "0 * * * * *")
    public void scheduler() {
        log.debug("cron request...");
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .limitForPeriod(2)
                .timeoutDuration(Duration.ofSeconds(25))
                .build();

        RateLimiter rateLimiter = rateLimiterRegistry
                .rateLimiter("rate-limiter-instance1", config);

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .minimumNumberOfCalls(3)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .recordExceptions(RestClientException.class)
                .build();

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuit-breaker-instance1", circuitBreakerConfig);

        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.ofSeconds(1))
                .retryExceptions(RestClientException.class)
                .ignoreExceptions(CallNotPermittedException.class)
                .failAfterMaxAttempts(false)
                .build();

        Retry retry = retryRegistry.retry("retry-instance1", retryConfig);

        CheckedRunnable decoratedRunnable = Decorators.ofCheckedRunnable(this::internalRestClient)
                .withCircuitBreaker(circuitBreaker)
                .withRateLimiter(rateLimiter)
                .withRetry(retry)
                .decorate();

        Try.run(decoratedRunnable);

        Exception ex = new Exception();

        if(ex instanceof CallNotPermittedException) {
            CallNotPermittedException e = (CallNotPermittedException) ex;
            e.getCausingCircuitBreakerName();
        }

    }

    private void internalRestClient() {
        log.debug("rest call...");
        log.debug(" Making a request to {} at :{}", SERVICE_URL + "scheduled", LocalDateTime.now());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(SERVICE_URL, String.class);
    }
}

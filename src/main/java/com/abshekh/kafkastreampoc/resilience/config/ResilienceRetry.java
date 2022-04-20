package com.abshekh.kafkastreampoc.resilience.config;


import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Configuration
@Slf4j
public class ResilienceRetry {
    private final RetryRegistry retryRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ResilienceRetry(RetryRegistry retryRegistry, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.retryRegistry = retryRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Bean
    public RetryConfig defaultRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(10)
                .retryExceptions(RestClientException.class, CallNotPermittedException.class)
                .intervalBiFunction((integer, objects) -> {
                    var exception = objects.getLeft();
                    if (exception instanceof CallNotPermittedException) {
                        var ex = (CallNotPermittedException) exception;
                        var intervalFunction = circuitBreakerRegistry.circuitBreaker(ex.getCausingCircuitBreakerName())
                                .getCircuitBreakerConfig()
                                .getWaitIntervalFunctionInOpenState();

                        return intervalFunction.apply(integer) + 1000L;
                    } else {
                        return Duration.ofSeconds(1).toMillis();
                    }
                })
                .build();
    }

    public IntervalFunction jitterExponentialFunction() {
        return IntervalFunction.ofExponentialRandomBackoff(Duration.ofSeconds(10), 2, 0.6D);
    }

    @Bean
    public RetryConfig jitterRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(10)
                .retryExceptions(RestClientException.class, CallNotPermittedException.class)
                .intervalBiFunction((integer, objects) -> {
                    var exception = objects.getLeft();
                    if (exception instanceof CallNotPermittedException) {
                        var ex = (CallNotPermittedException) exception;
                        var intervalFunction = circuitBreakerRegistry.circuitBreaker(ex.getCausingCircuitBreakerName())
                                .getCircuitBreakerConfig()
                                .getWaitIntervalFunctionInOpenState();

                        return intervalFunction.apply(integer) + 1000L;
                    } else {
                        return jitterExponentialFunction().apply(integer);
                    }
                })
                .build();

    }

    @Bean
    public Retry retryInstanceTopic1(RetryConfig defaultRetryConfig) {
        return retryRegistry.retry("retry-instance-topic1", defaultRetryConfig);
    }

    @Bean
    public Retry retryInstanceTopic2(RetryConfig defaultRetryConfig) {
        return retryRegistry.retry("retry-instance-topic2", defaultRetryConfig);
    }

    @Bean
    public Retry retryInstanceTopic3(RetryConfig jitterRetryConfig) {
        return retryRegistry.retry("retry-instance-topic3", jitterRetryConfig);
    }

    @Bean
    public Retry retryInstanceTopic4(RetryConfig defaultRetryConfig) {
        return retryRegistry.retry("retry-instance-topic4", defaultRetryConfig);
    }
}

package com.abshekh.kafkastreampoc.resilience.config;


import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Configuration
@AllArgsConstructor
@Slf4j
public class ResilienceRetry {
    public static final String RETRY_INSTANCE_TOPIC_1 = "retry-instance-topic1";
    public static final String RETRY_INSTANCE_TOPIC_2 = "retry-instance-topic2";
    public static final String RETRY_INSTANCE_TOPIC_3 = "retry-instance-topic3";
    public static final String RETRY_INSTANCE_TOPIC_4 = "retry-instance-topic4";

    private final RetryRegistry retryRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    @Bean
    public RetryConfig defaultRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(10)
                .retryExceptions(RestClientException.class,
                        CallNotPermittedException.class,
                        RequestNotPermitted.class)
                .intervalBiFunction((integer, objects) -> {
                    var exception = objects.getLeft();
                    log.debug("retry: {}, is RequestNotPermitted: {}", integer, exception instanceof RequestNotPermitted);
                    if (exception instanceof CallNotPermittedException) {
                        var ex = (CallNotPermittedException) exception;
                        var intervalFunction = circuitBreakerRegistry.circuitBreaker(ex.getCausingCircuitBreakerName())
                                .getCircuitBreakerConfig()
                                .getWaitIntervalFunctionInOpenState();

                        return intervalFunction.apply(integer) + 1000L;
                    } else if (exception instanceof RequestNotPermitted) {
                        var rateLimiter = rateLimiterRegistry.rateLimiter(exception.getMessage().split("'")[1]);
                        return rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod().toMillis() + 1000L;
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
                .retryExceptions(RestClientException.class,
                        CallNotPermittedException.class,
                        RequestNotPermitted.class)
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
        return retryRegistry.retry(RETRY_INSTANCE_TOPIC_1, defaultRetryConfig);
    }

    @Bean
    public Retry retryInstanceTopic2(RetryConfig defaultRetryConfig) {
        return retryRegistry.retry(RETRY_INSTANCE_TOPIC_2, defaultRetryConfig);
    }

    @Bean
    public Retry retryInstanceTopic3(RetryConfig jitterRetryConfig) {
        return retryRegistry.retry(RETRY_INSTANCE_TOPIC_3, jitterRetryConfig);
    }

    @Bean
    public Retry retryInstanceTopic4(RetryConfig defaultRetryConfig) {
        return retryRegistry.retry(RETRY_INSTANCE_TOPIC_4, defaultRetryConfig);
    }
}

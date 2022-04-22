package com.abshekh.kafkastreampoc.resilience.config;


import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class ResilienceRetry {
    public static final String RETRY_INSTANCE_TOPIC_1 = "retry-instance-topic1";
    public static final String RETRY_INSTANCE_TOPIC_2 = "retry-instance-topic2";
    public static final String RETRY_INSTANCE_TOPIC_3 = "retry-instance-topic3";
    public static final String RETRY_INSTANCE_TOPIC_4 = "retry-instance-topic4";

    private final RetryRegistry retryRegistry;

    private final CircuitBreaker circuitBreakerInstanceTopic1;
    private final CircuitBreaker circuitBreakerInstanceTopic2;
    private final CircuitBreaker circuitBreakerInstanceTopic3;
    private final CircuitBreaker circuitBreakerInstanceTopic4;

    private final RateLimiter rateLimiterInstanceTopic4;

    public RetryConfig defaultRetryConfig(List<CircuitBreaker> circuitBreakers,
                                          List<RateLimiter> rateLimiters) {
        return RetryConfig.custom()
                .maxAttempts(10)
                .retryExceptions(RestClientException.class,
                        CallNotPermittedException.class,
                        RequestNotPermitted.class)
                .intervalBiFunction((integer, objects) -> {
                    long duration = Duration.ofSeconds(1).toMillis();
                    long cbDuration = circuitBreakers.stream().filter(circuitBreaker -> !circuitBreaker.tryAcquirePermission())
                            .map(circuitBreaker -> circuitBreaker.getCircuitBreakerConfig()
                                    .getWaitIntervalFunctionInOpenState().apply(integer) + 1000L)
                            .max(Comparator.comparingLong(Long::longValue))
                            .orElse(0L);
                    long rateLimiterDuration = rateLimiters.stream().filter(rateLimiter -> !rateLimiter.acquirePermission())
                            .map(rateLimiter -> rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod().toMillis() + 1000L)
                            .max(Comparator.comparingLong(Long::longValue))
                            .orElse(0L);

                    final var maxDuration = Math.max(duration, Math.max(cbDuration, rateLimiterDuration));
                    log.debug("retry backoff: {}, {}ms", integer, maxDuration);
                    return maxDuration;
                })
                .build();
    }

    public IntervalFunction jitterExponentialFunction() {
        return IntervalFunction.ofExponentialRandomBackoff(Duration.ofSeconds(10), 2, 0.6D);
    }

    public RetryConfig jitterRetryConfig(List<CircuitBreaker> circuitBreakers,
                                         List<RateLimiter> rateLimiters) {
        return RetryConfig.custom()
                .maxAttempts(10)
                .retryExceptions(RestClientException.class,
                        CallNotPermittedException.class,
                        RequestNotPermitted.class)
                .intervalBiFunction((integer, objects) -> {
                    long duration = jitterExponentialFunction().apply(integer);
                    long cbDuration = circuitBreakers.stream().filter(circuitBreaker -> !circuitBreaker.tryAcquirePermission())
                            .map(circuitBreaker -> circuitBreaker.getCircuitBreakerConfig()
                                    .getWaitIntervalFunctionInOpenState().apply(integer) + 1000L)
                            .max(Comparator.comparingLong(Long::longValue))
                            .orElse(0L);
                    long rateLimiterDuration = rateLimiters.stream().filter(rateLimiter -> !rateLimiter.acquirePermission())
                            .map(rateLimiter -> rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod().toMillis() + 1000L)
                            .max(Comparator.comparingLong(Long::longValue))
                            .orElse(0L);
                    return Math.max(duration, Math.max(cbDuration, rateLimiterDuration));
                })
                .build();

    }

    @Bean
    public Retry retryInstanceTopic1() {
        List<CircuitBreaker> circuitBreakers = List.of(circuitBreakerInstanceTopic1);
        RetryConfig retryConfig = defaultRetryConfig(circuitBreakers, Collections.emptyList());
        return retryRegistry.retry(RETRY_INSTANCE_TOPIC_1, retryConfig);
    }

    @Bean
    public Retry retryInstanceTopic2() {
        List<CircuitBreaker> circuitBreakers = List.of(circuitBreakerInstanceTopic2);
        RetryConfig retryConfig = defaultRetryConfig(circuitBreakers, Collections.emptyList());
        return retryRegistry.retry(RETRY_INSTANCE_TOPIC_2, retryConfig);
    }

    @Bean
    public Retry retryInstanceTopic3() {
        List<CircuitBreaker> circuitBreakers = List.of(circuitBreakerInstanceTopic3);
        RetryConfig retryConfig = jitterRetryConfig(circuitBreakers, Collections.emptyList());
        return retryRegistry.retry(RETRY_INSTANCE_TOPIC_3, retryConfig);
    }

    @Bean
    public Retry retryInstanceTopic4() {
        List<CircuitBreaker> circuitBreakers = List.of(circuitBreakerInstanceTopic4);
        List<RateLimiter> rateLimiters = List.of(rateLimiterInstanceTopic4);
        RetryConfig retryConfig = defaultRetryConfig(circuitBreakers, rateLimiters);
        return retryRegistry.retry(RETRY_INSTANCE_TOPIC_4, retryConfig);
    }
}

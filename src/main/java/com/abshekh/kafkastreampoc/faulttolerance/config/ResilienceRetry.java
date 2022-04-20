package com.abshekh.kafkastreampoc.faulttolerance.config;


import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Configuration
public class ResilienceRetry {
    private final RetryRegistry retryRegistry;

    public ResilienceRetry(RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

    @Bean
    public RetryConfig defaultRetryConfig() {
       return RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.ofSeconds(1))
                .retryExceptions(RestClientException.class)
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

    public IntervalFunction jitterExponentialFunction() {
        return IntervalFunction.ofExponentialRandomBackoff(Duration.ofSeconds(10), 2, 0.6D);
    }

    @Bean
    public Retry retryInstanceTopic3() {
        RetryConfig customConfig = RetryConfig.custom()
                .maxAttempts(10)
                .retryExceptions(RestClientException.class)
                .intervalFunction(jitterExponentialFunction())
                .build();

        return retryRegistry.retry("retry-instance-topic3", customConfig);
    }

    @Bean
    public Retry retryInstanceTopic4(RetryConfig defaultRetryConfig) {
        return retryRegistry.retry("retry-instance-topic4", defaultRetryConfig);
    }
}

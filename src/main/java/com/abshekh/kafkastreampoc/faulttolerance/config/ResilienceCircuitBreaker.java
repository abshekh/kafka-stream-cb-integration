package com.abshekh.kafkastreampoc.faulttolerance.config;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Configuration
@Slf4j
public class ResilienceCircuitBreaker {
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ResilienceCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Bean
    public CircuitBreakerConfig defaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(60)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .minimumNumberOfCalls(3)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .recordExceptions(RestClientException.class)
                .build();
    }

    @Bean
    public CircuitBreaker circuitBreakerInstanceTopic1(CircuitBreakerConfig defaultCircuitBreakerConfig) {
        return circuitBreakerRegistry.circuitBreaker("circuit-breaker-instance-topic1", defaultCircuitBreakerConfig);
    }

    @Bean
    public CircuitBreaker circuitBreakerInstanceTopic2(CircuitBreakerConfig defaultCircuitBreakerConfig) {
        return circuitBreakerRegistry.circuitBreaker("circuit-breaker-instance-topic2", defaultCircuitBreakerConfig);
    }

    @Bean
    public CircuitBreaker circuitBreakerInstanceTopic3(CircuitBreakerConfig defaultCircuitBreakerConfig) {
        return circuitBreakerRegistry.circuitBreaker("circuit-breaker-instance-topic3", defaultCircuitBreakerConfig);
    }

    @Bean
    public CircuitBreaker circuitBreakerInstanceTopic4(CircuitBreakerConfig defaultCircuitBreakerConfig) {
        return circuitBreakerRegistry.circuitBreaker("circuit-breaker-instance-topic4", defaultCircuitBreakerConfig);
    }
}

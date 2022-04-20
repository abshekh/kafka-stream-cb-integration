package com.abshekh.kafkastreampoc.resilience.config;


import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceRateLimiter {
    private final RateLimiterRegistry rateLimiterRegistry;

    public ResilienceRateLimiter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Bean
    public RateLimiterConfig defaultRateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .limitForPeriod(1)
                .timeoutDuration(Duration.ofSeconds(25))
                .build();
    }

    @Bean
    public RateLimiter rateLimiterInstanceTopic4(RateLimiterConfig defaultRateLimiterConfig) {
        return rateLimiterRegistry.rateLimiter("rate-limiter-instance-topic4", defaultRateLimiterConfig);
    }
}

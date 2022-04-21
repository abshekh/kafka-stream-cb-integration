package com.abshekh.kafkastreampoc.resilience.config;


import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@AllArgsConstructor
public class ResilienceRateLimiter {
    public static final String RATE_LIMITER_INSTANCE_TOPIC_4 = "rate-limiter-instance-topic4";

    private final RateLimiterRegistry rateLimiterRegistry;

    @Bean
    public RateLimiterConfig defaultRateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(60))
                .limitForPeriod(1)
                .timeoutDuration(Duration.ofSeconds(25))
                .build();
    }

    @Bean
    public RateLimiter rateLimiterInstanceTopic4(RateLimiterConfig defaultRateLimiterConfig) {
        return rateLimiterRegistry.rateLimiter(RATE_LIMITER_INSTANCE_TOPIC_4, defaultRateLimiterConfig);
    }
}

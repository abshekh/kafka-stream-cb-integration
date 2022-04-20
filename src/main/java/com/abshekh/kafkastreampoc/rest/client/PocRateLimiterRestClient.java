package com.abshekh.kafkastreampoc.rest.client;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PocRateLimiterRestClient {
    private static final String SERVICE_URL = "http://localhost:6060/?message=";

    @Scheduled(cron = "0 * * * * *")
    public void scheduler() {
        log.debug("cron request...");
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(5))
                .limitForPeriod(2)
                .timeoutDuration(Duration.ofSeconds(25))
                .build();

        // Create registry
        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);

        RateLimiter rateLimiterWithCustomConfig = rateLimiterRegistry
                .rateLimiter("rate-limiter-instance1", config);

        CheckedRunnable rateLimitedRunnable = RateLimiter.decorateCheckedRunnable(rateLimiterWithCustomConfig, this::internalRestClient);

        Retry retryConfig = Retry.ofDefaults("retry-instance4");
        CheckedRunnable retryRateLimiter = Retry.decorateCheckedRunnable(retryConfig, rateLimitedRunnable);
        Try.run(retryRateLimiter);
    }

    private void internalRestClient() {
        log.debug("rest call...");
        log.debug(" Making a request to {} at :{}", SERVICE_URL + "scheduled", LocalDateTime.now());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(SERVICE_URL, String.class);
    }
}

package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.TopicMessage;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Slf4j
public class Topic4ConsumerService {
    private final PocRestClient pocRestClient;
    private final CircuitBreaker circuitBreakerInstanceTopic4;
    private final Retry retryInstanceTopic4;
    private final RateLimiter rateLimiterInstanceTopic4;

    @Bean
    public Consumer<KStream<Object, TopicMessage>> topic4Consumer() {
        return input -> input.foreach(this::businessLogic);
    }

    private void businessLogic(Object key, TopicMessage val) {
        log.debug("topic4Consumer: {}", val);
        log.debug("cb: {}, {}", circuitBreakerInstanceTopic4.getName(), circuitBreakerInstanceTopic4.getCircuitBreakerConfig());
        log.debug("retry: {}, {}", retryInstanceTopic4.getName(), retryInstanceTopic4.getRetryConfig().getMaxAttempts());
        log.debug("rate-limiter: {}, {}", rateLimiterInstanceTopic4.getName(), rateLimiterInstanceTopic4.getRateLimiterConfig().getLimitRefreshPeriod());
        var runnable = Decorators.ofCheckedRunnable(() -> pocRestClient.restClient(val.getMessage()))
                .withCircuitBreaker(circuitBreakerInstanceTopic4)
                .withRateLimiter(rateLimiterInstanceTopic4)
                .withRetry(retryInstanceTopic4)
                .decorate();
        Try.run(runnable);
        log.debug("topic4Consumer end...");
    }
}

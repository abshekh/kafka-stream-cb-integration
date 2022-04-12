package com.abshekh.kafkastreampoc.circuitbreaker.config;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.core.IntervalFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerCustomizer {

    public IntervalFunction jitterExponentialFunction() {
        return IntervalFunction.ofExponentialRandomBackoff(Duration.ofSeconds(10), 2, 0.6D);
    }

    @Bean
    public RetryConfigCustomizer retryInstanceTopic3Customizer() {
        return RetryConfigCustomizer
                .of("retry-instance-topic3", builder -> builder.intervalFunction(jitterExponentialFunction()));
    }
}

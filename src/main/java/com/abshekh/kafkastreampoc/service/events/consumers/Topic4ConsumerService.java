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

    @Bean
    public Consumer<KStream<Object, TopicMessage>> topic4Consumer() {
        return input -> input.foreach(this::businessLogic);
    }

    private void businessLogic(Object key, TopicMessage val) {
        log.debug("topic4Consumer: {}", val);
        pocRestClient.restClient4(val.getMessage());
        log.debug("topic4Consumer end...");
    }
}

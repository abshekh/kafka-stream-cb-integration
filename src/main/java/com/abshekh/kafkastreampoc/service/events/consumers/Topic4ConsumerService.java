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
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@AllArgsConstructor
@Slf4j
public class Topic4ConsumerService {
    private final PocRestClient pocRestClient;

    @Bean
    @SuppressWarnings("unchecked")
    public Function<KStream<Object, TopicMessage>, KStream<Object, TopicMessage>[]> topic4Consumer() {
        return input ->
                input.map(this::businessLogic)
                        .split(Named.as("topic2-"))
                        .branch((key, value) -> value != null, Branched.as("error"))
                        .noDefaultBranch()
                        .values()
                        .toArray(KStream[]::new);
    }

    private KeyValue<Object, TopicMessage> businessLogic(Object key, TopicMessage val) {
        try {
            log.debug("topic4Consumer: {}", val);
            pocRestClient.restClient4(val.getMessage());
            log.debug("topic4Consumer end...");
            return null;
        } catch (Exception ignored) {
            log.debug("topic4Consumer end with exception...");
            return new KeyValue<>(key, val);
        }
    }
}

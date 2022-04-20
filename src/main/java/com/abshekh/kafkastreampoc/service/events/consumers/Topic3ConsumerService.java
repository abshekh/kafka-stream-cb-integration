package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.TopicMessage;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Slf4j
public class Topic3ConsumerService {
    private final PocRestClient pocRestClient;
    private final CircuitBreaker circuitBreakerInstanceTopic3;
    private final Retry retryInstanceTopic3;

    @Bean
    public Consumer<KStream<Object, TopicMessage>> topic3Consumer() {
        return input -> input.foreach(this::businessLogic);
    }

    private void businessLogic(Object key, TopicMessage val) {
        log.debug("topic3Consumer: {}", val);
        var runnable = Decorators.ofCheckedRunnable(() -> pocRestClient.restClient(val.getMessage()))
                .withCircuitBreaker(circuitBreakerInstanceTopic3)
                .withRetry(retryInstanceTopic3)
                .decorate();
        Try.run(runnable);
        log.debug("topic3Consumer end...");
    }
}

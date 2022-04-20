package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.TopicMessage;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@Slf4j
@AllArgsConstructor
public class Topic2ConsumerService {
    private final PocRestClient pocRestClient;

    @Bean
    public Consumer<KStream<Object, TopicMessage>> topic2Consumer() {
        return input -> input.foreach(this::businessLogic);
    }

    private void businessLogic(Object key, TopicMessage val) {
        log.debug("topic2Consumer: {}", val);
        pocRestClient.restClient2(val.getMessage());
        log.debug("topic2Consumer end...");
    }
}

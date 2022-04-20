package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.Sensor;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.processor.api.Record;
import org.springframework.cloud.stream.annotation.StreamRetryTemplate;
import org.springframework.cloud.stream.binder.RequeueCurrentMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Slf4j
public class TopicConsumerService {
    private final PocRestClient pocRestClient;

    @Bean
    public Consumer<KStream<Object, Sensor>> topicConsumer() {
        return input -> input.foreach(this::businessLogic);
    }

    @StreamRetryTemplate
    RetryTemplate customRetryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(1)
                .build();
    }

    private void businessLogic(Object key, Sensor val) {
        log.debug("topicConsumer: {}", val);
        customRetryTemplate().execute(
                retryContext -> {
                    log.debug("inside spring retry: {}", retryContext.getRetryCount());
                    pocRestClient.restClient1(val.getId());
                    return null;
                },
                recoveryContext -> {
                    log.debug("inside spring retry recovery");
                    return null;
                });
        log.debug("topicConsumer end...");
    }
}

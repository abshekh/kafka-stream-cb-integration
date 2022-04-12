package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.Topic2Message;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.StreamRetryTemplate;
import org.springframework.cloud.stream.binder.RequeueCurrentMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class Topic2ConsumerService {
    private final PocRestClient pocRestClient;

    public Topic2ConsumerService(PocRestClient pocRestClient) {
        this.pocRestClient = pocRestClient;
    }

    @Bean
    public Consumer<KStream<Object, Topic2Message>> topic2Consumer() {
        return input -> input.foreach(this::businessLogic);
    }

    private void businessLogic(Object key, Topic2Message val) {
        log.debug("topic2Consumer: {}", val);
        pocRestClient.restClient2(val.getMessage());
        log.debug("topic2Consumer end...");
    }
}

package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.Topic2Message;
import com.abshekh.kafkastreampoc.model.kafka.Topic3Message;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class Topic3ConsumerService {
    private final PocRestClient pocRestClient;

    public Topic3ConsumerService(PocRestClient pocRestClient) {
        this.pocRestClient = pocRestClient;
    }

    @Bean
    public Consumer<KStream<Object, Topic3Message>> topic3Consumer() {
        return input -> input.foreach(this::businessLogic);
    }

    private void businessLogic(Object key, Topic3Message val) {
        log.debug("topic3Consumer: {}", val);
        pocRestClient.restClient3(val.getMessage());
        log.debug("topic3Consumer end...");
    }
}

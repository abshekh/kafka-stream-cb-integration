package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.Sensor;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class TopicConsumerService {
    private final PocRestClient pocRestClient;

    public TopicConsumerService(PocRestClient pocRestClient) {
        this.pocRestClient = pocRestClient;
    }

    @Bean
    public Consumer<KStream<Object, Sensor>> topicConsumer() {
        return input -> input.foreach(this::businessLogic);
    }

    private void businessLogic(Object key, Sensor val) {
        log.debug("topicConsumer: {}", val);
        pocRestClient.restClient(val.getId());
        log.debug("topicConsumer end...");
    }
}

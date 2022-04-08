package com.abshekh.kafkastreampoc.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.Topic2Message;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component("topic2Consumer")
@Slf4j
public class Topic2Consumer implements Consumer<KStream<Object, Topic2Message>> {
    private final PocRestClient pocRestClient;

    public Topic2Consumer(PocRestClient pocRestClient) {
        this.pocRestClient = pocRestClient;
    }

    @Override
    public void accept(KStream<Object, Topic2Message> message) {
        message.foreach((k, val) -> {
            log.debug("topic2Consumer: {}", val);
            pocRestClient.restClient(val.getMessage());
            log.debug("topic2Consumer end...");
        });
    }

    @Override
    public Consumer<KStream<Object, Topic2Message>> andThen(Consumer<? super KStream<Object, Topic2Message>> after) {
        return Consumer.super.andThen(after);
    }
}

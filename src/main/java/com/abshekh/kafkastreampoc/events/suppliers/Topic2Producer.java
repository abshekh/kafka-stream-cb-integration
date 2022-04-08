package com.abshekh.kafkastreampoc.events.suppliers;

import com.abshekh.kafkastreampoc.model.kafka.Topic2Message;
import com.abshekh.kafkastreampoc.model.request.Topic2Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class Topic2Producer {
    private final StreamBridge streamBridge;

    public Topic2Producer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void topic2Publisher(Topic2Request topic2Request) {
        Topic2Message topic2Message = new Topic2Message();
        topic2Message.setId(UUID.randomUUID() + "-v1");
        topic2Message.setMessage(topic2Request.getMessage());
        log.debug("topic2Publisher: {}", topic2Message);
        streamBridge.send("topic2Producer-out-0", topic2Message);
    }
}

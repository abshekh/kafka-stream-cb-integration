package com.abshekh.kafkastreampoc.service.events.suppliers;

import com.abshekh.kafkastreampoc.model.kafka.Sensor;
import com.abshekh.kafkastreampoc.model.kafka.TopicMessage;
import com.abshekh.kafkastreampoc.model.request.TopicRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class StreamBridgeProducersService {
    private final StreamBridge streamBridge;
    private final Random random;

    public StreamBridgeProducersService(StreamBridge streamBridge) {
        this.random = new Random();
        this.streamBridge = streamBridge;
    }

    public void topicPublisher() {
        Sensor sensor = new Sensor();
        sensor.setId(UUID.randomUUID() + "-v1");
        sensor.setAcceleration(random.nextFloat() * 10);
        sensor.setVelocity(random.nextFloat() * 100);
        sensor.setTemperature(random.nextFloat() * 50);
        log.debug("topicPublisher: {}", sensor);
        streamBridge.send("topicProducer-out-0", sensor);
    }

    public void topic2Publisher(TopicRequest topicRequest) {
        TopicMessage topic2Message = new TopicMessage();
        topic2Message.setId(UUID.randomUUID() + "-v1");
        topic2Message.setMessage(topicRequest.getMessage());
        log.debug("topic2Publisher: {}", topic2Message);
        streamBridge.send("topic2Producer-out-0", topic2Message);
    }

    public void topic3Publisher(TopicRequest topicRequest) {
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setId(UUID.randomUUID() + "-v1");
        topicMessage.setMessage(topicRequest.getMessage());
        log.debug("topic3Publisher: {}", topicMessage);
        streamBridge.send("topic3Producer-out-0", topicMessage);
    }

    public void topic4Publisher(TopicRequest topicRequest) {
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setId(UUID.randomUUID() + "-v1");
        topicMessage.setMessage(topicRequest.getMessage());
        log.debug("topic4Publisher: {}", topicMessage);
        streamBridge.send("topic4Producer-out-0", topicMessage);
    }
}

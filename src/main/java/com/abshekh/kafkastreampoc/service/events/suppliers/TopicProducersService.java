package com.abshekh.kafkastreampoc.service.events.suppliers;

import com.abshekh.kafkastreampoc.model.kafka.Sensor;
import com.abshekh.kafkastreampoc.model.kafka.Topic2Message;
import com.abshekh.kafkastreampoc.model.kafka.Topic3Message;
import com.abshekh.kafkastreampoc.model.request.Topic2Request;
import com.abshekh.kafkastreampoc.model.request.Topic3Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class TopicProducersService {
    private final StreamBridge streamBridge;
    private final Random random;

    public TopicProducersService(StreamBridge streamBridge) {
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

    public void topic2Publisher(Topic2Request topic2Request) {
        Topic2Message topic2Message = new Topic2Message();
        topic2Message.setId(UUID.randomUUID() + "-v1");
        topic2Message.setMessage(topic2Request.getMessage());
        log.debug("topic2Publisher: {}", topic2Message);
        streamBridge.send("topic2Producer-out-0", topic2Message);
    }

    public void topic3Publisher(Topic3Request topic3Request) {
        Topic3Message topic3Message = new Topic3Message();
        topic3Message.setId(UUID.randomUUID() + "-v1");
        topic3Message.setMessage(topic3Request.getMessage());
        log.debug("topic3Publisher: {}", topic3Message);
        streamBridge.send("topic3Producer-out-0", topic3Message);
    }
}

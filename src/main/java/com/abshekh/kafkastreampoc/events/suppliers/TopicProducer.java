package com.abshekh.kafkastreampoc.events.suppliers;

import com.abshekh.kafkastreampoc.model.kafka.Sensor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

@Component("topicProducer")
@Slf4j
public class TopicProducer implements Supplier<Sensor> {
    private final Random random;

    public TopicProducer() {
        this.random = new Random();
    }

    @SneakyThrows
    @Override
    public Sensor get() {
        Sensor sensor = new Sensor();
        sensor.setId(UUID.randomUUID() + "-v1");
        sensor.setAcceleration(random.nextFloat() * 10);
        sensor.setVelocity(random.nextFloat() * 100);
        sensor.setTemperature(random.nextFloat() * 50);
        Thread.sleep(5000);
        log.debug("topicProducer: {}", sensor);
        return sensor;
    }
}

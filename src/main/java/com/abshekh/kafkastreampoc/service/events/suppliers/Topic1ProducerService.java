package com.abshekh.kafkastreampoc.service.events.suppliers;

import com.abshekh.kafkastreampoc.model.kafka.Sensor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@Slf4j
public class Topic1ProducerService {
    private final Random random;

    public Topic1ProducerService() {
        this.random = new Random();
    }

    @Bean
    public Supplier<Sensor> topicProducer() {
        return () -> {
            Sensor sensor = new Sensor();
            sensor.setId(UUID.randomUUID() + "-v1");
            sensor.setAcceleration(random.nextFloat() * 10);
            sensor.setVelocity(random.nextFloat() * 100);
            sensor.setTemperature(random.nextFloat() * 50);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }

            log.debug("topicProducer: {}", sensor);
            return sensor;
        };
    }
}

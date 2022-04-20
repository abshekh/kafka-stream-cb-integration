package com.abshekh.kafkastreampoc.service.events.suppliers;

import com.abshekh.kafkastreampoc.model.kafka.TopicMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

@Service
@Slf4j
public class Topic4ProducerService {
    @Bean
    public Supplier<TopicMessage> topic4Producer() {
        return () -> {
            TopicMessage topicMessage = new TopicMessage();
            topicMessage.setId(UUID.randomUUID() + "-v1");
            topicMessage.setMessage("rate-limited-consumer-message");
            log.debug("topic4Producer: {}", topicMessage);
            return topicMessage;
        };
    }
}

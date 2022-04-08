package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.Sensor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.StreamRetryTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.function.Consumer;

@Service
@Slf4j
public class TopicConsumerService {
    private final Random random;

    public TopicConsumerService() {
        random = new Random();
    }

    @Bean
    @StreamRetryTemplate
    private RetryTemplate topicRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        RetryPolicy retryPolicy = new SimpleRetryPolicy(4);
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1);

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public Consumer<KStream<Object, Sensor>> topicConsumer(@Lazy RetryTemplate topicRetryTemplate) {
        return input -> input.foreach((k, val) ->
                topicRetryTemplate.execute(
                        retryContext -> businessLogic(retryContext, val),
                        retryContext -> retryLogic(retryContext, val))
        );
    }

    private Object businessLogic(RetryContext retryContext, Sensor val) {
        log.debug("retry-num: {}", retryContext.getRetryCount());
        if (random.nextBoolean()) {
            log.debug("topicConsumer exception occurred: {}", val);
            throw new RuntimeException();
        } else {
            log.debug("topicConsumer processed: {}", val);
        }
        return null;
    }

    private Object retryLogic(RetryContext retryContext, Sensor val) {
        log.debug("topicConsumer retry logic...");
        return null;
    }
}

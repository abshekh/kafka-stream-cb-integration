package com.abshekh.kafkastreampoc.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.Sensor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.springframework.cloud.stream.annotation.StreamRetryTemplate;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.Consumer;

@Component("topicConsumer")
@Slf4j
public class TopicConsumer implements Consumer<KStream<Object, Sensor>> {
    private final Random random;

    public TopicConsumer() {
        random = new Random();
    }

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

    @Override
    public void accept(KStream<Object, Sensor> message) {
        message
                .foreach((k, val) -> topicRetryTemplate().execute(retryContext -> businessLogic(retryContext, val),
                        retryContext -> retryLogic(retryContext, val)));
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

    @Override
    public Consumer<KStream<Object, Sensor>> andThen(Consumer<? super KStream<Object, Sensor>> after) {
        return Consumer.super.andThen(after);
    }
}

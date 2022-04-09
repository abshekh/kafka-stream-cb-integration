package com.abshekh.kafkastreampoc.service.events.consumers;

import com.abshekh.kafkastreampoc.model.kafka.Topic2Message;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.StreamRetryTemplate;
import org.springframework.cloud.stream.binder.RequeueCurrentMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class Topic2ConsumerService {
    private final PocRestClient pocRestClient;

    public Topic2ConsumerService(PocRestClient pocRestClient) {
        this.pocRestClient = pocRestClient;
    }

    @Bean
    public Consumer<KStream<Object, Topic2Message>> topic2Consumer() {
        return input -> input.foreach(this::businessLogic);
    }

    @StreamRetryTemplate
    RetryTemplate customRetryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(1)
                .notRetryOn(CallNotPermittedException.class)
                .build();
    }

    private void businessLogic(Object key, Topic2Message val) {
        log.debug("topic2Consumer: {}", val);
        customRetryTemplate().execute(
                retryContext -> {
                    log.debug("inside spring retry: {}", retryContext.getRetryCount());
                    pocRestClient.restClient2(val.getMessage());
                    return null;
                },
                retryContext -> {
                    if(retryContext.getLastThrowable() instanceof CallNotPermittedException) {
                        throw new RequeueCurrentMessageException(retryContext.getLastThrowable());
                    }
                    log.debug("inside spring retry recovery");
                    return null;
                });
        log.debug("topic2Consumer end...");
    }
}

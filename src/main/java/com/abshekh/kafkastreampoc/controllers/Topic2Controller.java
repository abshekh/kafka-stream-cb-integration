package com.abshekh.kafkastreampoc.controllers;

import com.abshekh.kafkastreampoc.events.suppliers.Topic2Producer;
import com.abshekh.kafkastreampoc.model.request.Topic2Request;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
@Slf4j
public class Topic2Controller {
    private final Topic2Producer topic2Producer;
    private final PocRestClient pocRestClient;

    public Topic2Controller(Topic2Producer topic2Producer, PocRestClient pocRestClient) {
        this.topic2Producer = topic2Producer;
        this.pocRestClient = pocRestClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> postTopic2Message(@RequestBody Topic2Request request) {
        log.debug("postTopic2Message: {}", request);
        topic2Producer.topic2Publisher(request);
        return Mono.just("ok");
    }


    @GetMapping("/cb")
    @ResponseStatus(HttpStatus.OK)
    public void activateCb() {
        log.debug("activateCb...");
        pocRestClient.restClient("cb");
        log.debug("endCb...");
    }
}

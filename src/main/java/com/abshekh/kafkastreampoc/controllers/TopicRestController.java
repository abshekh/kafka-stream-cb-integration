package com.abshekh.kafkastreampoc.controllers;

import com.abshekh.kafkastreampoc.model.request.Topic3Request;
import com.abshekh.kafkastreampoc.service.events.suppliers.TopicProducersService;
import com.abshekh.kafkastreampoc.model.request.Topic2Request;
import com.abshekh.kafkastreampoc.rest.client.PocRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
@Slf4j
public class TopicRestController {
    private final TopicProducersService topicProducersService;
    private final PocRestClient pocRestClient;

    public TopicRestController(TopicProducersService topicProducersService, PocRestClient pocRestClient) {
        this.topicProducersService = topicProducersService;
        this.pocRestClient = pocRestClient;
    }

    @PostMapping("/1")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> postTopic1Message() {
        topicProducersService.topicPublisher();
        return Mono.just("ok");
    }

    @PostMapping("/2")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> postTopic2Message(@RequestBody Topic2Request request) {
        log.debug("postTopic2Message: {}", request);
        topicProducersService.topic2Publisher(request);
        return Mono.just("ok");
    }

    @PostMapping("/3")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> postTopic3Message(@RequestBody Topic3Request request) {
        log.debug("postTopic3Message: {}", request);
        topicProducersService.topic3Publisher(request);
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

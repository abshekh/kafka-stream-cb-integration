package com.abshekh.kafkastreampoc.controllers;

import com.abshekh.kafkastreampoc.service.events.suppliers.StreamBridgeProducersService;
import com.abshekh.kafkastreampoc.model.request.TopicRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
@Slf4j
public class TopicRestController {
    private final StreamBridgeProducersService streamBridgeProducersService;

    public TopicRestController(StreamBridgeProducersService streamBridgeProducersService) {
        this.streamBridgeProducersService = streamBridgeProducersService;
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> postTopicMessage(@PathVariable(name = "id") String id,
                                         @RequestBody(required = false) TopicRequest request) {
        switch (id) {
            case "1":
                log.debug("postTopicMessage");
                streamBridgeProducersService.topicPublisher();
                break;
            case "2":
                log.debug("postTopic2Message: {}", request);
                streamBridgeProducersService.topic2Publisher(request);
                break;
            case "3":
                log.debug("postTopic3Message: {}", request);
                streamBridgeProducersService.topic3Publisher(request);
                break;
            case "4":
                log.debug("postTopic4Message: {}", request);
                streamBridgeProducersService.topic4Publisher(request);
                break;
            default:
                break;
        }
        return Mono.just("ok");
    }
}

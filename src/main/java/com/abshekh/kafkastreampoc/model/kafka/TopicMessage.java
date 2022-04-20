package com.abshekh.kafkastreampoc.model.kafka;

import lombok.Data;

@Data
public class TopicMessage {
    private String id;
    private String message;
}

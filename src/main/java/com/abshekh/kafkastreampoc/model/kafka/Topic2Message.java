package com.abshekh.kafkastreampoc.model.kafka;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Topic2Message {
    private String id;
    private String message;
}
